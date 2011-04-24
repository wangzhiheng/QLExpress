package com.ql.util.express;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ql.util.express.instruction.FunctionInstructionSet;
import com.ql.util.express.instruction.detail.Instruction;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.instruction.opdata.OperateDataLocalVar;



/**
 * ���ʽִ�б�����γɵ�ָ���
 * @author qhlhl2010@gmail.com
 *
 */

public class InstructionSet {

	private static final Log log = LogFactory.getLog(InstructionSet.class);
	public static AtomicInteger uniqIndex = new AtomicInteger(1);
	public static String TYPE_MAIN ="main";
	public static String TYPE_FUNCTION ="function";
	public static String TYPE_MARCO ="marco";
	
	private String type ="main";
	private String name;
	private String globeName;
  /**
   * ָ��
   */
  private Instruction[] instructionList = new Instruction[0];
  /**
   * �����ͺ궨��
   */
  private Map<String,FunctionInstructionSet> functionDefine = new HashMap<String,FunctionInstructionSet>();
  private List<ExportItem> exportVar = new ArrayList<ExportItem>();
  /**
   * ������������
   */
  private List<OperateDataLocalVar> parameterList = new ArrayList<OperateDataLocalVar>();
  
  public InstructionSet(String aType){
	  this.type = aType;
  }
  
  public static int getUniqClassIndex(){
	  return uniqIndex.getAndIncrement();
  }
  
  /**
   * ���ָ�Ϊ����������ڵ�Ч�ʣ�ָ�������洢
   * @param item
   * @return
   */
  private void addArrayItem(Instruction item){
	  Instruction[] newArray = new Instruction[this.instructionList.length + 1];
	  System.arraycopy(this.instructionList, 0, newArray, 0, this.instructionList.length);
	  newArray[this.instructionList.length] = item;
	  this.instructionList = newArray;
  }
  /**
   * ��������
   * @param aPoint
   * @param item
   */
  private void insertArrayItem(int aPoint,Instruction item){
	  Instruction[] newArray = new Instruction[this.instructionList.length + 1];
	  System.arraycopy(this.instructionList, 0, newArray, 0, aPoint);
	  System.arraycopy(this.instructionList, aPoint, newArray, aPoint + 1,this.instructionList.length - aPoint);
	  newArray[aPoint] = item;
	  this.instructionList = newArray;
  }

/**
 * 
 * @param environmen
 * @param context
 * @param errorList
 * @param isLast
 * @param isReturnLastData �Ƿ����Ľ������Ҫ����ִ�к궨���ʱ����Ҫ
 * @param aLog
 * @return
 * @throws Exception
 */
	public CallResult excute(RunEnvironment environmen,InstructionSetContext<String,Object> context,
			List<String> errorList,boolean isLast,boolean isReturnLastData,Log aLog)
			throws Exception {
		//������export����������
		for(FunctionInstructionSet item : this.functionDefine.values()){
			context.addSymbol(item.name, item.instructionSet);
		}
		//ѭ��ִ��ָ��
		this.executeInner(environmen, errorList, aLog);
		
		if (environmen.isExit() == false && isLast == true) {// ����ִ�������е�ָ�������Ĵ���
			if (environmen.getDataStackSize() > 0) {
				OperateData tmpObject = environmen.pop();
				if (tmpObject == null) {
					environmen.quitExpress(null);
				} else {
					if(isReturnLastData == true){
						environmen.quitExpress(tmpObject.getObject(context));
					}else{
					    environmen.quitExpress(tmpObject);
					}
				}
			}
		}
		if (environmen.getDataStackSize() > 1) {
			throw new Exception("�ڱ��ʽִ����Ϻ󣬶�ջ�л����ڶ������");
		}
		CallResult result = new CallResult();		
		result.returnValue = environmen.getReturnValue();
		result.isExit = environmen.isExit();
		return result;
	}

	/**
	 * ѭ��ִ��ָ��
	 * 
	 * @param environmen
	 * @param errorList
	 * @param aLog
	 * @throws Exception
	 */
  public void executeInner(RunEnvironment environmen,List<String> errorList,Log aLog) throws Exception{
		Instruction instruction;
		while (environmen.getProgramPoint() < this.instructionList.length) {
			if (environmen.isExit() == true) {
				return;
			}
			instruction = this.instructionList[environmen.getProgramPoint()];
			instruction.setLog(aLog);//����log
			try{
				instruction.execute(environmen, errorList);
			}catch(Exception e){
				log.error("��ǰProgramPoint = " + environmen.getProgramPoint());
				log.error("��ǰָ��" +  instruction);
				log.error(e);
	            throw e;
			}
		}
  }
  public String toJavaCode(){
	  StringBuilder javaCode = new StringBuilder();
	  StringBuilder staticFieldDefine = new StringBuilder();
	  StringBuilder functionDefine = new StringBuilder();
	  functionDefine.append("public void execute(RunEnvironment environment,List<String> errorList,Log aLog)  throws Exception{").append("\n");
	  for(int i = 0; i< this.instructionList.length;i++){
		  this.instructionList[i].toJavaCode(staticFieldDefine,functionDefine,i);
	  }
	  functionDefine.append("}").append("\n");
	  javaCode.append("package com.ql.util.express.test.asm;").append("\n");
	  javaCode.append("import ").append(OperateData.class.getName()+";").append("\n");
	  javaCode.append("import ").append(RunEnvironment.class.getName()+";").append("\n");
	  javaCode.append("import ").append(OperatorBase.class.getName()+";").append("\n");	  
	  javaCode.append("import ").append(List.class.getName()+";").append("\n");
	  javaCode.append("import ").append(Log.class.getName()+";").append("\n");	  
	  javaCode.append("\n");
	  javaCode.append("public class ExpressClass_" + getUniqClassIndex() + "{").append("\n");
	  javaCode.append(staticFieldDefine.toString());
	  javaCode.append(functionDefine.toString());
	  javaCode.append("}").append("\n");	  
	  return javaCode.toString();
  }
  public void addMacroDefine(String macroName,FunctionInstructionSet iset){
	  this.functionDefine.put(macroName, iset);
  }
  public FunctionInstructionSet getMacroDefine(String macroName){
	  return this.functionDefine.get(macroName);
  }
  public FunctionInstructionSet[] getFunctionInstructionSets(){
	  return this.functionDefine.values().toArray(new FunctionInstructionSet[0]);
  }
  public void addExportDef(ExportItem e){
	  this.exportVar.add(e);
  }
  public List<ExportItem> getExportDef(){
	  List<ExportItem> result = new ArrayList<ExportItem> ();
	  result.addAll(this.exportVar);
	  return result;
  }

	
	public OperateDataLocalVar[] getParameters() {
		return this.parameterList.toArray(new OperateDataLocalVar[0]);
	}

	public void addParameter(OperateDataLocalVar localVar) {
		this.parameterList.add(localVar);
	}	
  public void addInstruction(Instruction instruction){
	  this.addArrayItem(instruction);
  }
  public void insertInstruction(int point,Instruction instruction){
	  this.insertArrayItem(point, instruction);
  } 
  public Instruction getInstruction(int point){
	  return this.instructionList[point];
  }
  public int getCurrentPoint(){
	  return this.instructionList.length - 1; 
  }
  
  public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getGlobeName() {
	return globeName;
}

public void setGlobeName(String globeName) {
	this.globeName = globeName;
}
public boolean hasMain(){
	return this.instructionList.length >0;
}
public String getType() {
	return type;
}

	public String toString() {
		try {
			StringBuffer buffer = new StringBuffer();
			// ����궨��
			for (FunctionInstructionSet set : this.functionDefine.values()) {
				buffer.append(set.type + ":" + set.name).append("\n");
				buffer.append(set.instructionSet);
			}
			// ����
			for (OperateDataLocalVar var : this.parameterList) {
				buffer.append("���� ��" + var.getName()).append(" ")
						.append(var.getType(null)).append("\n");
			}
			buffer.append("ָ���\n");
			for (int i = 0; i < this.instructionList.length; i++) {
				buffer.append(i + 1).append(":").append(this.instructionList[i])
						.append("\n");
			}
			return buffer.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}



class CallResult{
	Object returnValue;
	boolean isExit;
}

	