package com.ql.util.express;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * ���ʽִ�б�����γɵ�ָ���
 * @author qhlhl2010@gmail.com
 *
 */
@SuppressWarnings("unchecked")
public class InstructionSet {
	public static String TYPE_MAIN ="main";
	public static String TYPE_FUNCTION ="function";
	public static String TYPE_MARCO ="marco";
	
	private static final Log log = LogFactory.getLog(InstructionSet.class);
	private String type ="main";
	private String name;
	private String globeName;
    private int maxStackSize  = -1;
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
  protected static Object executeOuter(InstructionSet[] sets,ExpressLoader loader,
			IExpressContext aContext, List errorList,
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
  protected static Object execute(InstructionSet[] sets,ExpressLoader loader,
			IExpressContext aContext, List errorList,
			FuncitonCacheManager aFunctionCacheMananger, boolean isTrace,boolean isCatchException,
			boolean isReturnLastData,Log aLog)
			throws Exception {
	  InstructionSetContext context = new InstructionSetContext<String, Object>(
				aContext,loader, aFunctionCacheMananger);
	  Object result = execute(sets,context,errorList,isTrace,isCatchException,isReturnLastData,aLog);
		if (aFunctionCacheMananger == null) {
			// �����ָ��������Ļ��棬�����
			context.clearFuncitonCacheManager();
		}
     return result;
  }

	protected static Object execute(InstructionSet[] sets,
			InstructionSetContext context, List errorList, boolean isTrace,boolean isCatchException,
			boolean isReturnLastData,Log aLog) throws Exception {
		RunEnvironment environmen = null;
		Object result = null;
		for (int i = 0; i < sets.length; i++) {
			InstructionSet tmpSet = sets[i];
			environmen = new RunEnvironment(tmpSet.maxStackSize, tmpSet,
					(InstructionSetContext) context, isTrace);
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
	public CallResult excuteInner(RunEnvironment environmen,InstructionSetContext context,List errorList,boolean isLast,boolean isReturnLastData,Log aLog)
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
/**
   * ��Ӳ�������ջָ��
   * @param operateData
   * @param stackSize
   */
  public void addLoadDataInstruction(OperateData operateData,int stackSize){
	  this.list.add(new InstructionLoadData(operateData));
	  if(stackSize > this.maxStackSize){
		  this.maxStackSize = stackSize;
	  }	  
  }
  
  public void addLoadAttrInstruction(OperateDataAttr operateData,int stackSize){
	  this.list.add(new InstructionLoadAttr(operateData.name));
	  if(stackSize > this.maxStackSize){
		  this.maxStackSize = stackSize;
	  }	  
  }  
/**
 * ��Ӳ�������ָ��
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
public String toResource(){
	String result ="";
	if(this.parameterList.size() >0){
		result = result +"(";
	}
	for(int i=0;i<this.parameterList.size();i++){
		 OperateDataLocalVar var = this.parameterList.get(i);
		 if(i > 0){
			 result = result +",";
		 }
		result =result + var.type+ " "+var.name;
	  }	
	if(this.parameterList.size() >0){
		result = result +")";
	}
	return result;
}
public String toString(){
	  
	  StringBuffer buffer = new StringBuffer();
	  //����궨��
	  for(FunctionInstructionSet set : this.functionDefine.values()){
		  buffer.append(set.type + ":" + set.name).append("\n");
		  buffer.append(set.instructionSet);
	  }	  
	  //����
	  for(OperateDataLocalVar var : this.parameterList){
		  buffer.append("���� ��" + var.name).append(" ").append(var.type).append("\n");
	  }	
	  buffer.append("ָ���\n");
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
@SuppressWarnings("unchecked")
class RunEnvironment {
    private boolean isTrace = false;	
	private int point = -1;
    private int programPoint = 0;
	private OperateData[] dataContainer;
	
	private boolean isExit = false;
	private Object returnValue = null; 
	
	private InstructionSet instructionSet;
	private InstructionSetContext context;
	
	
	public RunEnvironment(int aStackSize,InstructionSet aInstructionSet,InstructionSetContext aContext,boolean aIsTrace){
		if(aStackSize <0){
			aStackSize =0;
		}
		dataContainer = new OperateData[aStackSize];
		this.instructionSet = aInstructionSet;
		this.context = aContext;
		this.isTrace = aIsTrace;
	}
	
	
	public InstructionSet getInstructionSet() {
		return instructionSet;
	}


	public InstructionSetContext getContext(){
		return this.context;
	}
	public void setContext(InstructionSetContext aContext){
		this.context = aContext;
	}

	public boolean isExit() {
		return isExit;
	}
	public Object getReturnValue() {
		return returnValue;
	}
	public void setReturnValue(Object value){
		this.returnValue = value;
	}
	public void quitExpress(Object aReturnValue){
		this.isExit = true;
		this.returnValue = aReturnValue;
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
	public void clearDataStack(){
		this.point = -1;
	}
	public void gotoWithOffset(int aOffset ){
		this.programPoint = this.programPoint + aOffset;
	}

	public OperateData[] popArray(InstructionSetContext context,int len) throws Exception {
		OperateData[] result = new OperateData[len];
		int start = point - len + 1;
		if(start <0){
			throw new Exception("��ջ�����������ʽ�Ƿ����");
		}
		for (int i = 0 ; i < len; i++) {
			result[i] = this.dataContainer[start + i];
			if(void.class.equals(result[i].getType(context))){
				throw new Exception("void ���ܲ����κβ�������,����ʹ���ڱ��ʽ��ʹ����û�з���ֵ�ĺ���,���߷�֧��������if���");
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
	protected  Log log = LogFactory.getLog(Instruction.class);
	public void setLog(Log aLog){
		if(aLog != null){
		   this.log = aLog;
		}
	}
	public abstract void execute(RunEnvironment environment,List errorList)throws Exception;
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

@SuppressWarnings("unchecked")
class InstructionLoadAttr extends Instruction{
    String attrName;
    InstructionLoadAttr(String aName){
    	this.attrName = aName;
    }
	public void execute(RunEnvironment environment,List errorList)throws Exception{
		Object o = environment.getContext().getSymbol(this.attrName);
		if(o != null && o instanceof InstructionSet){//�Ǻ�������ִ��
			if(environment.isTrace()){
				log.debug("ָ��ת���� LoadAttr -- >CallMacro ");						
			}
			InstructionCallMacro macro = new InstructionCallMacro(this.attrName);
			macro.setLog(this.log);
			macro.execute(environment, errorList);
		}else{
			if(environment.isTrace()){
				log.debug(this +":" + ((OperateDataAttr)o).getObject(environment.getContext()));						
			}
		    environment.push((OperateDataAttr)o);
		    environment.programPointAddOne();
		}
	}
	public String toString(){
		  return "LoadAttr:" +this.attrName;	
	}
}

class InstructionClearDataStack extends Instruction{
	InstructionClearDataStack(){
    }
	@SuppressWarnings("unchecked")
	public void execute(RunEnvironment environment,List errorList)throws Exception{
		//Ŀǰ��ģʽ������Ҫִ���κβ���
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
 * ��ʼһ���µ�������
 * @author xuannan
 *
 */
class InstructionOpenNewArea extends Instruction{
	InstructionOpenNewArea(){
    }
	@SuppressWarnings("unchecked")
	public void execute(RunEnvironment environment,List errorList)throws Exception{
		//Ŀǰ��ģʽ������Ҫִ���κβ���
		if(environment.isTrace()){
			log.debug(this);
		}
		environment.setContext(new InstructionSetContext<String, Object>(
				environment.getContext(),environment.getContext().getExpressLoader(), environment.getContext()
						.getFunctionCachManagerNoCreate()));
		environment.programPointAddOne();
	}
	public String toString(){
	  return "openNewArea";	
	}
}


class InstructionCallFunction extends Instruction{
	InstructionCallFunction(){
    }
	@SuppressWarnings("unchecked")
	public void execute(RunEnvironment environment,List errorList)throws Exception{
		
		String functionName =  (String)environment.pop().getObjectInner(environment.getContext());
		if(environment.isTrace()){
			log.debug(this + functionName);
		}
		Object functionSet = environment.getContext().getSymbol(functionName);		
		Object result =InstructionSet.execute(new InstructionSet[]{(InstructionSet)functionSet},environment.getContext().getExpressLoader(),
				environment.getContext(), errorList, environment.getContext().getFunctionCachManagerNoCreate(),
				environment.isTrace(),false,true,this.log);
		environment.push(new OperateData(result,null));		
		environment.programPointAddOne();
	}
	public String toString(){
	  return "call function " ;	
	}
}

class InstructionCallSelfDefineFunction extends Instruction{
	String functionName;
	int opDataNumber;
	public InstructionCallSelfDefineFunction(String name,int aOpDataNumber){
	  this.functionName = name;
	  this.opDataNumber =aOpDataNumber;
	}
	@SuppressWarnings("unchecked")
	public void execute(RunEnvironment environment,List errorList)throws Exception{
		OperateData[] parameters = environment.popArray(environment.getContext(),this.opDataNumber);		
		if(environment.isTrace()){
			String str = this.functionName + "(";
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
		
		InstructionSet functionSet = (InstructionSet)environment.getContext().getSymbol(functionName);		
		InstructionSetContext context = new InstructionSetContext<String, Object>(
			environment.getContext(),environment.getContext().getExpressLoader(),
			environment.getContext().getFunctionCachManagerNoCreate());
		OperateDataLocalVar[] vars = functionSet.getParameters();
		for(int i=0;i<vars.length;i++){
			//ע��˴�����new һ���µĶ��󣬷���ͻ��ڶ�ε��õ�ʱ�������ݳ�ͻ
			OperateDataLocalVar var = new OperateDataLocalVar(vars[i].name,vars[i].type);
			context.addSymbol(var.name, var);
			var.setObject(context, parameters[i].getObject(environment.getContext()));
		}
		Object result =InstructionSet.execute(new InstructionSet[]{(InstructionSet)functionSet},
				context,errorList,environment.isTrace(),false,true,this.log);
		environment.push(new OperateData(result,null));		
		environment.programPointAddOne();
	}
	public String toString(){
	  return "call selfDefineFunction " ;	
	}
}

class InstructionCallMacro extends Instruction{
	String name;
	InstructionCallMacro(String aName){
		this.name = aName;
    }
	@SuppressWarnings("unchecked")
	public void execute(RunEnvironment environment,List errorList)throws Exception{
		if(environment.isTrace()){
			log.debug(this);
		}
		Object functionSet = environment.getContext().getSymbol(this.name);
		
		Object result =InstructionSet.execute(new InstructionSet[]{(InstructionSet)functionSet},environment.getContext().getExpressLoader(),
				environment.getContext(), errorList, environment.getContext().getFunctionCachManagerNoCreate(),
				environment.isTrace(),false,false,this.log);
		if(result instanceof OperateData){
			environment.push((OperateData)result);
		}else{
		   environment.push(new OperateData(result,null));
		}
		
		environment.programPointAddOne();
	}
	public String toString(){
	  return "call macro " + this.name ;	
	}
}
/**
 * 
 * �ر�һ��������
 * @author xuannan
 *
 */
class InstructionCloseNewArea extends Instruction{
	InstructionCloseNewArea(){
    }
	@SuppressWarnings("unchecked")
	public void execute(RunEnvironment environment,List errorList)throws Exception{
		//Ŀǰ��ģʽ������Ҫִ���κβ���
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

class InstructionCachFuncitonCall extends Instruction{
	InstructionCachFuncitonCall(){
    }
	@SuppressWarnings("unchecked")
	public void execute(RunEnvironment environment,List errorList)throws Exception{
		//Ŀǰ��ģʽ������Ҫִ���κβ���
		if(environment.isTrace()){
			log.debug(this);
		}
		environment.getContext().startFunctionCallCache();
		environment.programPointAddOne();
	}
	public String toString(){
	  return "cacheFunctionCall";	
	}
}

class InstructionReturn extends Instruction{
    InstructionReturn(){
    }
	@SuppressWarnings("unchecked")
	public void execute(RunEnvironment environment,List errorList)throws Exception{
		//Ŀǰ��ģʽ������Ҫִ���κβ���
		if(environment.isTrace()){
			log.debug(this);
		}
		if(environment.getDataStackSize() >= 1){
			
		   environment.quitExpress(environment.pop().getObject(environment.getContext()));
		}else{
		   environment.quitExpress(null);
		}
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
			throw new Exception("ָ�����:" + o + " ����Boolean");
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
	 * ��תָ���ƫ����
	 */
    int offset;
    String name;
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
	  result = result + this.offset + " " + this.name;
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
class CallResult{
	Object returnValue;
	boolean isExit;
}
class FunctionInstructionSet{
	String name;
	String type;
	InstructionSet instructionSet;
	public FunctionInstructionSet(String aName,String aType,InstructionSet aInstructionSet){
		this.name = aName;
		this.type = aType;
		this.instructionSet = aInstructionSet;		
	}
}
	