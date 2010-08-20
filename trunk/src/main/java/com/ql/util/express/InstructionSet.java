package com.ql.util.express;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ����ʽִ�б�����γɵ�ָ���
 * @author qhlhl2010@gmail.com
 *
 */
@SuppressWarnings("unchecked")
public class InstructionSet {
  private int maxStackSize  = -1;
  private List<Instruction> list = new ArrayList<Instruction>();
  /**
   * ִ�����еĲ���ָ��
   * @param context
   * @param errorList
   * @return
   * @throws Exception
   */
  public Object excute(IExpressContext context,List errorList) throws Exception{
	  RunEnvironment environmen = new RunEnvironment(this.maxStackSize);
	  Instruction instruction;
	  while(environmen.getProgramPoint() < this.list.size()){
		  instruction = this.list.get(environmen.getProgramPoint());
		  //System.out.println("ִ�У�" + instruction);
		  instruction.execute(environmen,context,errorList);
	  }
	  return environmen.pop().getObject(context);	  
  }
  /**
   * ���Ӳ�������ջָ��
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
 * ���Ӳ�������ָ��
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
	  buffer.append("ָ��� ������ݳ���:" + (this.maxStackSize)).append("\n");
	  for(int i=0;i<list.size();i++){
		  buffer.append(i + 1).append(":").append(list.get(i)).append("\n");
	  }
	  return buffer.toString();
  }
  
}

/**
 * ִ�е����л���
 * @author qhlhl2010@gmail.com
 *
 */
class RunEnvironment {
	private int point = -1;
    private int programPoint = 0;


	private OperateData[] dataContainer;

	public RunEnvironment(int aStackSize){
		dataContainer = new OperateData[aStackSize];
	}
	public int getProgramPoint() {
		return programPoint;
	}
	public void programPointAddOne() {
		programPoint ++ ;
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
			throw new RuntimeException("ϵͳ�쳣����ջָ�����");
		}
		return this.dataContainer[point];
		
	}
	public OperateData pop(){
		if(point <0)
			throw new RuntimeException("ϵͳ�쳣����ջָ�����");
		OperateData result = this.dataContainer[point];
		this.point--;
		return result;
	}
	public void gotoWithOffset(int aOffset ){
		this.programPoint = this.programPoint + aOffset;
	}

	public OperateData[] popArray(IExpressContext context,int len) throws Exception {
		OperateData[] result = new OperateData[len];
		int start = point - len + 1;
		for (int i = 0 ; i < len; i++) {
			result[i] = this.dataContainer[start + i];
			if(void.class.equals(result[i].getType(context))){
				throw new Exception("void ���ܲ����κβ�������,����ʹ���ڱ���ʽ��ʹ����û�з���ֵ�ĺ���");
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
 * װ�����ݵ���ջ
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
		environment.programPointAddOne();
	}
	public String toString(){
	  return "LoadData " +this.operateData.toString();	
	}
	
}
class InstructionReturn extends Instruction{
    InstructionReturn(){
    }
	public void execute(RunEnvironment environment,IExpressContext context,List errorList)throws Exception{
		//Ŀǰ��ģʽ������Ҫִ���κβ���
		environment.programPointAddOne();
	}
	public String toString(){
	  return "return ";	
	}
	
}
class InstructionGoToWithCondition extends Instruction{
	/**
	 * ��תָ���ƫ����
	 */
    int offset;
    boolean condition;
    public InstructionGoToWithCondition(boolean aCondition,int aOffset){
    	this.offset = aOffset;
    	this.condition = aCondition;
    }

	public void execute(RunEnvironment environment,IExpressContext context,List errorList)throws Exception{
		Object o = environment.peek().getObject(context);		
		if(o != null && o instanceof Boolean){
			if(((Boolean)o).booleanValue() == this.condition){
				environment.gotoWithOffset(this.offset);
			}else{
				environment.programPointAddOne();
			}
		}else{
			throw new Exception("ָ�����:" + o + " ����Boolean");
		}
	}
	public String toString(){
	  String result = "GoToIf[" + this.condition +"] " ;
	  if(this.offset >=0){
		  result = result +"+";
	  }
	  result = result + this.offset;
	  return result;
	}
}

/**
 * �������ָ������Ը���ÿ�����ͽ�����չ�������ϵͳ�ڵ�ָ���ϵ�ִ��Ч��
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
		environment.programPointAddOne();
	}
	public String toString(){
		String result = "OP : " + this.operator.toString() +  " OPNUMBER[" + this.opDataNumber +"]";
		return result;
	}
}