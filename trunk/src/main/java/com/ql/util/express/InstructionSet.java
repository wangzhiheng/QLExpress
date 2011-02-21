package com.ql.util.express;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ql.util.express.instruction.FunctionInstructionSet;
import com.ql.util.express.instruction.Instruction;
import com.ql.util.express.instruction.OperateDataLocalVar;
import com.ql.util.express.instruction.RunEnvironment;



/**
 * ���ʽִ�б�����γɵ�ָ���
 * @author qhlhl2010@gmail.com
 *
 */

public class InstructionSet {

	private static final Log log = LogFactory.getLog(InstructionSet.class);
	  
	public static String TYPE_MAIN ="main";
	public static String TYPE_FUNCTION ="function";
	public static String TYPE_MARCO ="marco";
	
	private String type ="main";
	private String name;
	private String globeName;
  /**
   * ָ��
   */
  private List<Instruction> list = new ArrayList<Instruction>();
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
  public static Object executeOuter(InstructionSet[] sets,ExpressLoader loader,
			IExpressContext<String,Object> aContext, List<String> errorList,
			FuncitonCacheManager aFunctionCacheMananger, boolean isTrace,boolean isCatchException,
			Log aLog) throws Exception{
	 return execute(sets, loader, aContext, errorList, aFunctionCacheMananger, isTrace, isCatchException,true, aLog);
  }
  
  /**
   * ����ִ��ָ��ϣ�ָ�����Թ��� �����ͺ���
   * @param sets
   * @param aOperatorManager
   * @param aContext
   * @param errorList
   * @param aFunctionCacheMananger
   * @param isTrace
   * @return
   * @throws Exception
   */
  public static Object execute(InstructionSet[] sets,ExpressLoader loader,
			IExpressContext<String,Object> aContext, List<String> errorList,
			FuncitonCacheManager aFunctionCacheMananger, boolean isTrace,boolean isCatchException,
			boolean isReturnLastData,Log aLog)
			throws Exception {
	  InstructionSetContext<String,Object> context = new InstructionSetContext<String, Object>(
				aContext,loader, aFunctionCacheMananger);
	  Object result = execute(sets,context,errorList,isTrace,isCatchException,isReturnLastData,aLog);
		if (aFunctionCacheMananger == null) {
			// �����ָ��������Ļ��棬�����
			context.clearFuncitonCacheManager();
		}
     return result;
  }

	public static Object execute(InstructionSet[] sets,
			InstructionSetContext<String,Object> context, List<String> errorList, boolean isTrace,boolean isCatchException,
			boolean isReturnLastData,Log aLog) throws Exception {
		RunEnvironment environmen = null;
		Object result = null;
		for (int i = 0; i < sets.length; i++) {
			InstructionSet tmpSet = sets[i];
			environmen = new RunEnvironment(tmpSet,
					(InstructionSetContext<String,Object>) context, isTrace);
			try {
				CallResult tempResult = tmpSet.excuteInner(environmen, context,
						errorList, i == sets.length - 1,isReturnLastData,aLog);
				if (tempResult.isExit == true) {
					result = tempResult.returnValue;
					break;
				}
			} catch (Exception e) {
				if(isCatchException == true){
					if (aLog != null){
				       aLog.error(e.getMessage(), e);
					}else{
					   log.error(e.getMessage(),e);
					}
				}else{
					throw e;
				}
			}
		}
		return result;

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
	public CallResult excuteInner(RunEnvironment environmen,InstructionSetContext<String,Object> context,List<String> errorList,boolean isLast,boolean isReturnLastData,Log aLog)
			throws Exception {
		Instruction instruction;
		//������export����������
		for(FunctionInstructionSet item : this.functionDefine.values()){
			context.addSymbol(item.name, item.instructionSet);
		}
		while (environmen.getProgramPoint() < this.list.size()) {
			if (environmen.isExit() == true) {
				break;
			}
			instruction = this.list.get(environmen.getProgramPoint());
			instruction.setLog(aLog);//����log
			instruction.execute(environmen, errorList);
		}
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
  public List<Instruction> getInstructionList(){
	  return this.list;
  }
  public void addInstruction(Instruction instruction){
	  this.list.add(instruction);
  }
  public void insertInstruction(int point,Instruction instruction){
	  this.list.add(point,instruction);
  } 
  public Instruction getInstruction(int point){
	  return this.list.get(point);
  }
  public int getCurrentPoint(){
	  return this.list.size() - 1; 
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
	return this.list.size() >0;
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
			for (int i = 0; i < list.size(); i++) {
				buffer.append(i + 1).append(":").append(list.get(i))
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

	