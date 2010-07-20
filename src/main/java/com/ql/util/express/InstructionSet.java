package com.ql.util.express;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
  public Object excute(IExpressContext context,List errorList) throws Exception{
	  RunEnvironment environmen = new RunEnvironment(this.maxStackSize);
	  for(int i=0;i < this.list.size() ;i++){
		  this.list.get(i).execute(environmen,context,errorList);
	  }
	  return environmen.pop().getObject(context);	  
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
class RunEnvironment {
	private int point = -1;
	private OperateData[] dataContainer;
	public RunEnvironment(int aStackSize){
		dataContainer = new OperateData[aStackSize];
	}
	public void push(OperateData data){
		this.point++;
		if(this.point >= this.dataContainer.length){
		   ensureCapacity(this.point + 1);
		}
		this.dataContainer[point] = data;
	}
	public OperateData pop(){
		if(point <0)
			throw new RuntimeException("系统异常，堆栈指针错误");
		OperateData result = this.dataContainer[point];
		this.point--;
		return result;
	}

	public OperateData[] popArray(IExpressContext context,int len) throws Exception {
		OperateData[] result = new OperateData[len];
		int start = point - len + 1;
		for (int i = 0 ; i < len; i++) {
			result[i] = this.dataContainer[start + i];
			if(void.class.equals(result[i].getType(context))){
				throw new Exception("void 不能参与任何操作运算,请检查使用在表达式中使用了没有返回值的函数");
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
	public abstract void execute(RunEnvironment environment,IExpressContext context,List errorList)throws Exception;
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

	public void execute(RunEnvironment environment,IExpressContext context,List errorList)throws Exception{
		environment.push(this.operateData);
	}
	public String toString(){
	  return "LoadData " +this.operateData.toString();	
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
	public void execute(RunEnvironment environment,IExpressContext context,List errorList) throws Exception{		
		OperateData[] parameters = environment.popArray(context,this.opDataNumber);		
		OperateData result = this.operator.execute(context,parameters, errorList);
		environment.push(result);
	}
	public String toString(){
		String result = "OP : " + this.operator.toString() +  " OPNUMBER[" + this.opDataNumber +"] ";
		return result;
	}
}