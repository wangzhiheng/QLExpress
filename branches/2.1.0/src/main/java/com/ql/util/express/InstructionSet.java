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
 * 表达式执行编译后形成的指令集合
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
   * 指令
   */
  private Instruction[] instructionList = new Instruction[0];
  /**
   * 函数和宏定义
   */
  private Map<String,FunctionInstructionSet> functionDefine = new HashMap<String,FunctionInstructionSet>();
  private List<ExportItem> exportVar = new ArrayList<ExportItem>();
  /**
   * 函数参数定义
   */
  private List<OperateDataLocalVar> parameterList = new ArrayList<OperateDataLocalVar>();
  public InstructionSet(String aType){
	  this.type = aType;
  }
  
  /**
   * 添加指令，为了提高运行期的效率，指令集用数组存储
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
   * 插入数据
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
  public static Object executeOuter(InstructionSet[] sets,ExpressLoader loader,
			IExpressContext<String,Object> aContext, List<String> errorList,
			boolean isTrace,boolean isCatchException,
			Log aLog) throws Exception{
	 return execute(sets, loader, aContext, errorList, isTrace, isCatchException,true, aLog);
  }
  
  /**
   * 批量执行指令集合，指令集间可以共享 变量和函数
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
			boolean isTrace,boolean isCatchException,
			boolean isReturnLastData,Log aLog)
			throws Exception {
	  InstructionSetContext<String,Object> context = new InstructionSetContext<String, Object>(
				aContext,loader);
	  Object result = execute(sets,context,errorList,isTrace,isCatchException,isReturnLastData,aLog);
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
 * @param isReturnLastData 是否最后的结果，主要是在执行宏定义的时候需要
 * @param aLog
 * @return
 * @throws Exception
 */
	public CallResult excuteInner(RunEnvironment environmen,InstructionSetContext<String,Object> context,List<String> errorList,boolean isLast,boolean isReturnLastData,Log aLog)
			throws Exception {
		Instruction instruction;
		//将函数export到上下文中
		for(FunctionInstructionSet item : this.functionDefine.values()){
			context.addSymbol(item.name, item.instructionSet);
		}
		while (environmen.getProgramPoint() < this.instructionList.length) {
			if (environmen.isExit() == true) {
				break;
			}
			instruction = this.instructionList[environmen.getProgramPoint()];
			instruction.setLog(aLog);//设置log
			try{
				instruction.execute(environmen, errorList);
			}catch(Exception e){
				log.error("当前ProgramPoint = " + environmen.getProgramPoint());
				log.error("当前指令" +  instruction);
				log.error("当前指令集" + this.instructionList);
				log.error(e);
	            throw e;
			}
		}
		if (environmen.isExit() == false && isLast == true) {// 是在执行完所有的指令后结束的代码
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
			throw new Exception("在表达式执行完毕后，堆栈中还存在多个数据");
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
			// 输出宏定义
			for (FunctionInstructionSet set : this.functionDefine.values()) {
				buffer.append(set.type + ":" + set.name).append("\n");
				buffer.append(set.instructionSet);
			}
			// 参数
			for (OperateDataLocalVar var : this.parameterList) {
				buffer.append("参数 ：" + var.getName()).append(" ")
						.append(var.getType(null)).append("\n");
			}
			buffer.append("指令集：\n");
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

	