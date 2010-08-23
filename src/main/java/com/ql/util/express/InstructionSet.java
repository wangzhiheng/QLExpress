package com.ql.util.express;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 表达式执行编译后形成的指令集合
 * @author qhlhl2010@gmail.com
 *
 */
@SuppressWarnings("unchecked")
public class InstructionSet {
  private int maxStackSize  = -1;
  private List<Instruction> list = new ArrayList<Instruction>();
  /**
   * 执行所有的操作指令
   * @param context
   * @param errorList
   * @return
   * @throws Exception
   */
  public Object excute(IExpressContext context,List errorList,boolean isTrace) throws Exception{
	  //进行上下文包装，进行变量的作用域隔离，在表达式内部通过 def 定义的变量，不会影响传入的上下文。
	  context = new InstructionSetContext<String, Object>(context);
	  RunEnvironment environmen = new RunEnvironment(this.maxStackSize,(InstructionSetContext)context,isTrace);
	  Instruction instruction;
	  while(environmen.getProgramPoint() < this.list.size()){
		  instruction = this.list.get(environmen.getProgramPoint());
		  //System.out.println("执行：" + instruction);
		  instruction.execute(environmen,errorList);
	  }
	  if(environmen.getDataStackSize() > 1){
		 // throw new Exception("在表达式执行完毕后，堆栈中还存在多个数据");
	  }
		OperateData result = environmen.pop();
		if (result != null) {
			return result.getObject(context);
		} else {
			return null;
		}
  }
  /**
   * 添加操作数入栈指令
   * @param operateData
   * @param stackSize
   */
  public void addLoadDataInstruction(OperateData operateData,int stackSize){
	  this.list.add(new InstructionLoadData(operateData));
	  if(stackSize > this.maxStackSize){
		  this.maxStackSize = stackSize;
	  }	  
  }
  
/**
 * 添加操作运行指令
 * @param operator
 * @param aOpDataNumber
 * @param stackSize
 */
  public void addOperatorInstruction(OperatorBase operator,int aOpDataNumber,int stackSize){
	  this.list.add(new InstructionOperator(operator,aOpDataNumber));
	  if(stackSize > this.maxStackSize){
		  this.maxStackSize = stackSize;
	  }
  }
  public void insertInstruction(int point,Instruction instruction){
	  this.list.add(point,instruction);
  } 
  public int getCurrentPoint(){
	  return this.list.size() - 1; 
  }
  public String toString(){
	  StringBuffer buffer = new StringBuffer();
	  buffer.append("指令集： 最大数据长度:" + (this.maxStackSize)).append("\n");
	  for(int i=0;i<list.size();i++){
		  buffer.append(i + 1).append(":").append(list.get(i)).append("\n");
	  }
	  return buffer.toString();
  }
  
}

/**
 * 执行的运行环境
 * @author qhlhl2010@gmail.com
 *
 */
@SuppressWarnings("unchecked")
class RunEnvironment {
    private boolean isTrace = false;	
	private int point = -1;
    private int programPoint = 0;
	private OperateData[] dataContainer;
	

	private InstructionSetContext context;
	public RunEnvironment(int aStackSize,InstructionSetContext aContext,boolean aIsTrace){
		dataContainer = new OperateData[aStackSize];
		this.context = aContext;
		this.isTrace = aIsTrace;
	}
	public InstructionSetContext getContext(){
		return this.context;
	}
	public void setContext(InstructionSetContext aContext){
		this.context = aContext;
	}
	
	public boolean isTrace(){
		return this.isTrace;
	}
	public int getProgramPoint() {
		return programPoint;
	}
	public void programPointAddOne() {
		programPoint ++ ;
	}
    public int getDataStackSize(){
    	return this.point + 1;
    }
	public void push(OperateData data){
		this.point++;
		if(this.point >= this.dataContainer.length){
		   ensureCapacity(this.point + 1);
		}
		this.dataContainer[point] = data;
	}
	public OperateData peek(){
		if(point <0){
			throw new RuntimeException("系统异常，堆栈指针错误");
		}
		return this.dataContainer[point];		
	}
	public OperateData pop(){
		if(point <0)
			throw new RuntimeException("系统异常，堆栈指针错误");
		OperateData result = this.dataContainer[point];
		this.point--;
		return result;
	}
	public void clearDataStack(){
		this.point = -1;
	}
	public void gotoWithOffset(int aOffset ){
		this.programPoint = this.programPoint + aOffset;
	}

	public OperateData[] popArray(IExpressContext<Object,Object> context,int len) throws Exception {
		OperateData[] result = new OperateData[len];
		int start = point - len + 1;
		if(start <0){
			throw new Exception("堆栈溢出，请检查表达式是否错误");
		}
		for (int i = 0 ; i < len; i++) {
			result[i] = this.dataContainer[start + i];
			if(void.class.equals(result[i].getType(context))){
				throw new Exception("void 不能参与任何操作运算,请检查使用在表达式中使用了没有返回值的函数,或者分支不完整的if语句");
			}
		}
		point = point - len;
		return result;
	}

	public void ensureCapacity(int minCapacity) {
		int oldCapacity = this.dataContainer.length;
		if (minCapacity > oldCapacity) {
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if (newCapacity < minCapacity){
				newCapacity = minCapacity;
			}
			this.dataContainer = Arrays.copyOf(this.dataContainer, newCapacity);
		}
	}
}
@SuppressWarnings("unchecked")
abstract class Instruction{
	protected static final Log log = LogFactory.getLog(Instruction.class);	
	public abstract void execute(RunEnvironment environment,List errorList)throws Exception;
}
/**
 * 装载数据到堆栈
 * @author qhlhl2010@gmail.com
 *
 */
@SuppressWarnings("unchecked")
class InstructionLoadData extends Instruction{
    OperateData operateData;
    InstructionLoadData(OperateData aOperateData){
    	this.operateData = aOperateData;
    }

	public void execute(RunEnvironment environment,List errorList)throws Exception{
		if(environment.isTrace()){			
			if(this.operateData instanceof OperateDataAttr){
				log.debug(this +":" + this.operateData.getObject(environment.getContext()));
			}else{
				log.debug(this );
			}			
		}
		environment.push(this.operateData);
		environment.programPointAddOne();
	}
	public String toString(){
	  if(this.operateData instanceof OperateDataAttr){
		  return "LoadData attr:" +this.operateData.toString();	
	  }else{
		  return "LoadData " +this.operateData.toString();	
	  }
	}
	
}
class InstructionClearDataStack extends Instruction{
	InstructionClearDataStack(){
    }
	@SuppressWarnings("unchecked")
	public void execute(RunEnvironment environment,List errorList)throws Exception{
		//目前的模式，不需要执行任何操作
		if(environment.isTrace()){
			log.debug(this);
		}
		environment.clearDataStack();
		environment.programPointAddOne();
	}
	public String toString(){
	  return "clearDataStack";	
	}
}

/**
 * 开始一个新的作用域
 * @author xuannan
 *
 */
class InstructionOpenNewArea extends Instruction{
	InstructionOpenNewArea(){
    }
	@SuppressWarnings("unchecked")
	public void execute(RunEnvironment environment,List errorList)throws Exception{
		//目前的模式，不需要执行任何操作
		if(environment.isTrace()){
			log.debug(this);
		}
		environment.setContext(new InstructionSetContext<String,Object>(environment.getContext()));
		environment.programPointAddOne();
	}
	public String toString(){
	  return "openNewArea";	
	}
}

/**
 * 
 * 关闭一个作用域
 * @author xuannan
 *
 */
class InstructionCloseNewArea extends Instruction{
	InstructionCloseNewArea(){
    }
	@SuppressWarnings("unchecked")
	public void execute(RunEnvironment environment,List errorList)throws Exception{
		//目前的模式，不需要执行任何操作
		if(environment.isTrace()){
			log.debug(this);
		}
		environment.setContext((InstructionSetContext)environment.getContext().getParent());
		environment.programPointAddOne();
	}
	public String toString(){
	  return "closeNewArea";	
	}
}
class InstructionReturn extends Instruction{
    InstructionReturn(){
    }
	@SuppressWarnings("unchecked")
	public void execute(RunEnvironment environment,List errorList)throws Exception{
		//目前的模式，不需要执行任何操作
		if(environment.isTrace()){
			log.debug(this);
		}
		environment.programPointAddOne();
	}
	public String toString(){
	  return "return ";	
	}
	
}
class InstructionGoToWithCondition extends Instruction{
	/**
	 * 跳转指令的偏移量
	 */
    int offset;
    boolean condition;
    boolean isPopStackData;
    public InstructionGoToWithCondition(boolean aCondition,int aOffset,boolean aIsPopStackData){
    	this.offset = aOffset;
    	this.condition = aCondition;
    	this.isPopStackData = aIsPopStackData;
    }

	@SuppressWarnings("unchecked")
	public void execute(RunEnvironment environment,List errorList)throws Exception{

		Object o = null;
		if(this.isPopStackData == false){
		    o = environment.peek().getObject(environment.getContext());	
		}else{
			o = environment.pop().getObject(environment.getContext());	
		}
		if(o != null && o instanceof Boolean){
			if(((Boolean)o).booleanValue() == this.condition){
				if(environment.isTrace()){
					log.debug("goto +" + this.offset);
				}
				environment.gotoWithOffset(this.offset);
			}else{
				if(environment.isTrace()){
					log.debug("programPoint ++ ");
				}
				environment.programPointAddOne();
			}
		}else{
			throw new Exception("指令错误:" + o + " 不是Boolean");
		}
	}
	public String toString(){
	  String result = "GoToIf[" + this.condition +",isPop=" + this.isPopStackData +"] " ;
	  if(this.offset >=0){
		  result = result +"+";
	  }
	  result = result + this.offset;
	  return result;
	}
}
class InstructionGoTo extends Instruction{
	/**
	 * 跳转指令的偏移量
	 */
    int offset;
    boolean condition;
    public InstructionGoTo(int aOffset){
    	this.offset = aOffset;
    }

	@SuppressWarnings("unchecked")
	public void execute(RunEnvironment environment,List errorList) throws Exception {
		if(environment.isTrace()){
			log.debug(this);
		}
		environment.gotoWithOffset(this.offset);
	}
	public String toString(){
	  String result = "GoTo ";
	  if(this.offset >=0){
		  result = result +"+";
	  }
	  result = result + this.offset;
	  return result;
	}
}

/**
 * 运算操作指令，今后可以根据每种类型进行扩展，以提高系统在单指令上的执行效率
 * @author qhlhl2010@gmail.com
 *
 */
@SuppressWarnings("unchecked")
class InstructionOperator extends Instruction{
	OperatorBase operator;
	int opDataNumber;
	public InstructionOperator(OperatorBase aOperator,int aOpDataNumber){
	  this.operator = aOperator;
	  this.opDataNumber =aOpDataNumber;
	}
	public void execute(RunEnvironment environment,List errorList) throws Exception{		
		OperateData[] parameters = environment.popArray(environment.getContext(),this.opDataNumber);		
		if(environment.isTrace()){
			String str = this.operator.toString() + "(";
			for(int i=0;i<parameters.length;i++){
				if(i > 0){
					str = str + ",";
				}
				if(parameters[i] instanceof OperateDataAttr){
					str = str + parameters[i] + ":" + parameters[i].getObject(environment.getContext());
				}else{
				   str = str + parameters[i];
				}
			}
			str = str + ")";
			log.debug(str);
		}
		OperateData result = this.operator.execute(environment.getContext(),parameters, errorList);
		environment.push(result);
		environment.programPointAddOne();
	}
	public String toString(){
		String result = "OP : " + this.operator.toString() +  " OPNUMBER[" + this.opDataNumber +"]";
		return result;
	}
}