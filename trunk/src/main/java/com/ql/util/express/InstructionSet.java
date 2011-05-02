package com.ql.util.express;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.ql.util.express.instruction.FunctionInstructionSet;
import com.ql.util.express.instruction.detail.Instruction;
import com.ql.util.express.instruction.detail.InstructionConstData;
import com.ql.util.express.instruction.detail.InstructionGoTo;
import com.ql.util.express.instruction.detail.InstructionGoToWithCondition;
import com.ql.util.express.instruction.detail.InstructionLoadAttr;
import com.ql.util.express.instruction.detail.InstructionOperator;
import com.ql.util.express.instruction.op.OperatorFactory;
import com.ql.util.express.instruction.opdata.OperateDataLocalVar;



/**
 * 表达式执行编译后形成的指令集合
 * @author qhlhl2010@gmail.com
 *
 */

public class InstructionSet {

	private static final Log log = LogFactory.getLog(InstructionSet.class);
	public static boolean IS_COMPILE2JAVACODE = false;
	public static AtomicInteger uniqIndex = new AtomicInteger(1);
	public static String TYPE_MAIN ="main";
	public static String TYPE_FUNCTION ="function";
	public static String TYPE_MARCO ="marco";
	
	private String type ="main";
	private String name;
	private String globeName;
	
	private java.lang.reflect.Method executeMethod;
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
  
  public static int getUniqClassIndex(){
	  return uniqIndex.getAndIncrement();
  }
  public InstructionSet(String aType){
	  this.type = aType;
  }
  
  public String[] getOutAttrNames() throws Exception{
	  Map<String,String> result = new TreeMap<String,String>();
	  for(Instruction instruction:instructionList){
		   if(instruction instanceof InstructionLoadAttr){
			   result.put(((InstructionLoadAttr)instruction).getAttrName(),null);
		   }
	  }
	 
	  //剔除本地变量定义和别名定义
		for (int i = 0; i < instructionList.length; i++) {
			Instruction instruction = instructionList[i];
			if (instruction instanceof InstructionOperator) {
				String opName = ((InstructionOperator) instruction)
						.getOperator().getName();
				if (opName.equalsIgnoreCase("def")
						|| opName.equalsIgnoreCase("exportDef")) {
					String varLocalName = (String) ((InstructionConstData) instructionList[i - 1])
							.getOperateData().getObject(null);
					result.remove(varLocalName);
				} else if (opName.equalsIgnoreCase("alias")
						|| opName.equalsIgnoreCase("exportAlias")) {
					String varLocalName = (String) ((InstructionConstData) instructionList[i - 2])
							.getOperateData().getObject(null);
					result.remove(varLocalName);
				}
			}
		}
	  return result.keySet().toArray(new String[0]);
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
	public CallResult excute(RunEnvironment environmen,InstructionSetContext<String,Object> context,
			List<String> errorList,boolean isLast,boolean isReturnLastData,Log aLog)
			throws Exception {
		//将函数export到上下文中
		for(FunctionInstructionSet item : this.functionDefine.values()){
			context.addSymbol(item.name, item.instructionSet);
		}
		//循环执行指令
		if(IS_COMPILE2JAVACODE == true){
			this.executeInnerJavaCode(environmen, errorList, aLog);
		}else{
			this.executeInnerOrigiInstruction(environmen, errorList, aLog);
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
	  public void executeInnerOrigiInstruction(RunEnvironment environmen,List<String> errorList,Log aLog) throws Exception{
			Instruction instruction;
			while (environmen.getProgramPoint() < this.instructionList.length) {
				if (environmen.isExit() == true) {
					return;
				}
				instruction = this.instructionList[environmen.getProgramPoint()];
				instruction.setLog(aLog);//设置log
				try{
					instruction.execute(environmen, errorList);
				}catch(Exception e){
					log.error("当前ProgramPoint = " + environmen.getProgramPoint());
					log.error("当前指令" +  instruction);
					log.error(e);
		            throw e;
				}
			}
	}
	/**
	 * 循环执行指令
	 * 
	 * @param environmen
	 * @param errorList
	 * @param aLog
	 * @throws Exception
	 */
  public void executeInnerJavaCode(RunEnvironment environmen,List<String> errorList,Log aLog) throws Exception{
	  initialExecuteMethod();
	  executeMethod.invoke(null, new Object[]{environmen,errorList,aLog});	  
  }
  public void initialExecuteMethod() throws SecurityException, NoSuchMethodException{
	    if(this.executeMethod != null){
	    	return ;
	    }
	    
		String className = "ExpressClass_" + getUniqClassIndex();
		byte[] code = toJavaCode(className);
		if(log.isDebugEnabled()){
			AsmUtil.writeClass(code, className);
		}
		Class<?> tempClass = new ExpressClassLoader(this.getClass()
				.getClassLoader()).loadClass(className, code);
		executeMethod = tempClass.getMethod("execute", new Class[] {
				RunEnvironment.class, List.class, Log.class });
  }
  public byte[] toJavaCode(String className){	  
		Type classType = Type.getType("L" + className.replaceAll("\\.", "\\/") + ";");
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classWriter.visit(Opcodes.V1_1, Opcodes.ACC_PUBLIC, className, null,
				"java/lang/Object", null);
		Method mStaticInitial = Method.getMethod("void <clinit>()");
		GeneratorAdapter mgStaticInitial = new GeneratorAdapter(Opcodes.ACC_STATIC, mStaticInitial, null, null, classWriter);

		String methodName = "void execute(" + RunEnvironment.class.getName()
				+ "," + List.class.getName() + "," + Log.class.getName() + ")";
		Method m = Method.getMethod(methodName);
		GeneratorAdapter mgExecuteMethod = new GeneratorAdapter(Opcodes.ACC_PUBLIC
				+ Opcodes.ACC_STATIC, m, null, null, classWriter);
		mgExecuteMethod.visitCode();
		//临时变量3是InstructionSetContext
		//临时变量4是OperatorFactory		
		
		//定义临时变量 InstructionSetContext<String, Object> context = environmen.getContext();
		mgExecuteMethod.loadArg(0);
		mgExecuteMethod.invokeVirtual(Type.getType(RunEnvironment.class),Method.getMethod(InstructionSetContext.class.getName() + "  getContext()"));
		mgExecuteMethod.storeLocal(3,Type.getType(InstructionSetContext.class));
		//context.getExpressRunner().getOperatorFactory()
		mgExecuteMethod.loadLocal(3);
		mgExecuteMethod.invokeVirtual(Type.getType(InstructionSetContext.class),Method.getMethod(ExpressRunner.class.getName() + "  getExpressRunner()"));
		mgExecuteMethod.invokeVirtual(Type.getType(ExpressRunner.class),Method.getMethod(OperatorFactory.class.getName() + "  getOperatorFactory()"));
		mgExecuteMethod.storeLocal(4,Type.getType(OperatorFactory.class));
		
		//找出所有的标签
		Map<Integer,Label> lables = new HashMap<Integer,Label>();
		for (int i = 0; i < this.instructionList.length; i++) {
			if(this.instructionList[i] instanceof InstructionGoToWithCondition){
				lables.put(((InstructionGoToWithCondition)this.instructionList[i]).getOffset() + i,mgExecuteMethod.newLabel());
			}else if(this.instructionList[i] instanceof InstructionGoTo){
				lables.put(((InstructionGoTo)this.instructionList[i]).getOffset() + i,mgExecuteMethod.newLabel());
			}
		}
		for (int i = 0; i < this.instructionList.length; i++) {
			if(lables.containsKey(i)){
				Label label =lables.get(i);
				mgExecuteMethod.visitLabel(label);
				lables.put(i, label);
			}
			this.instructionList[i].toJavaCode(classType, classWriter,
					mgStaticInitial, mgExecuteMethod, i, lables);
		}
		if(lables.containsKey(this.instructionList.length)){
			Label label = lables.get(this.instructionList.length);
			mgExecuteMethod.visitLabel(label);
		}
		
		mgStaticInitial.returnValue();
        mgStaticInitial.endMethod();
         
        mgExecuteMethod.returnValue();
        mgExecuteMethod.endMethod();
        
		classWriter.visitEnd();
		byte[] code = classWriter.toByteArray();
		return code;
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

	