package com.ql.util.express.instruction.detail;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.ql.util.express.AsmUtil;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.InstructionSetRunner;
import com.ql.util.express.OperateData;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.instruction.opdata.OperateDataAttr;
import com.ql.util.express.instruction.opdata.OperateDataLocalVar;

public class InstructionCallSelfDefineFunction extends Instruction{
	String functionName;
	int opDataNumber;
	OperatorBase operator;
	public InstructionCallSelfDefineFunction(OperatorBase aOperator,String name,int aOpDataNumber){
	  this.operator = aOperator;
	  this.functionName = name;
	  this.opDataNumber =aOpDataNumber;
	}
	
	public void execute(RunEnvironment environment, List<String> errorList)
			throws Exception {
		if (this.operator != null) {
			InstructionOperator.execute(this.operator, this.opDataNumber,
					environment, errorList, this.log);
		} else {
			OperateData[] parameters = environment.popArray(
					environment.getContext(), this.opDataNumber);
			if (environment.isTrace()) {
				String str = this.functionName + "(";
				for (int i = 0; i < parameters.length; i++) {
					if (i > 0) {
						str = str + ",";
					}
					if (parameters[i] instanceof OperateDataAttr) {
						str = str
								+ parameters[i]
								+ ":"
								+ parameters[i].getObject(environment
										.getContext());
					} else {
						str = str + parameters[i];
					}
				}
				str = str + ")";
				log.debug(str);
			}

			Object function = environment.getContext().getSymbol(functionName);
			if (function == null || function instanceof InstructionSet == false) {
				throw new Exception("在Runner的操作符定义和自定义函数中都没有找到\""
						+ this.functionName + "\"的定义");
			}
			InstructionSet functionSet = (InstructionSet)function;
			OperateData result = InstructionCallSelfDefineFunction
					.executeSelfFunction(environment, functionSet, parameters,
							errorList, this.log);
			environment.push(result);
			environment.programPointAddOne();
		}
	}
	public void toJavaCode(Type classType,ClassWriter cw,GeneratorAdapter staticInitialMethod,GeneratorAdapter executeMethod,int index, Map<Integer,Label>  lables){
		if (this.operator != null) {
			InstructionOperator.toJavaCode(this.operator.getAliasName(),this.opDataNumber, classType, cw, staticInitialMethod, executeMethod, index);
		}else{
//			paramRunEnvironment.push(
//					InstructionCallSelfDefineFunction.executeSelfFunction(
//							paramRunEnvironment,
//							(InstructionSet) localInstructionSetContext.getSymbol("initial"), 
//							paramRunEnvironment.popArray(localInstructionSetContext, 0),
//							paramList,
//							paramLog));
			
			executeMethod.loadArg(0);//	environment	for push	
			
			executeMethod.loadArg(0);//	environment
			//(InstructionSet)context.getSymbol(functionName);
			executeMethod.loadLocal(3);
			AsmUtil.transferCode(executeMethod,this.functionName);			
			executeMethod.invokeVirtual(Type.getType(InstructionSetContext.class),
					Method.getMethod("Object getSymbol(String)"));
			executeMethod.unbox(Type.getType(InstructionSet.class));
			
//			//OperateData[] parameters = environment.popArray(context, this.opDataNumber);
			executeMethod.loadArg(0);//	environment	
			executeMethod.loadLocal(3);
			executeMethod.visitLdcInsn(this.opDataNumber);
			executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),
					Method.getMethod(OperateData.class.getName() + "[]  popArray(" + InstructionSetContext.class.getName()+"," + int.class +")"));	
//		    //errorList
			executeMethod.loadArg(1);
			executeMethod.loadArg(2);
			executeMethod.invokeStatic(Type.getType(InstructionCallSelfDefineFunction.class),
					Method.getMethod(OperateData.class.getName() + " executeSelfFunction("
							+RunEnvironment.class.getName() +","
							+InstructionSet.class.getName() +","
							+ OperateData.class.getName()+"[],"
							+ List.class.getName() + ","
							+ Log.class.getName()
							+")"));
			executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),
	        		Method.getMethod("void push(" + OperateData.class.getName() + ")"));
		}
	}	
	public static OperateData executeSelfFunction(RunEnvironment environment,InstructionSet functionSet,
			OperateData[] parameters,List<String> errorList,Log log)throws Exception{	
		InstructionSetContext<String, Object> context = new InstructionSetContext<String, Object>(
				environment.getContext().getExpressRunner(),environment.getContext(),environment.getContext().getExpressLoader(),environment.getContext().isSupportDynamicFieldName());
		OperateDataLocalVar[] vars = functionSet.getParameters();
		for(int i=0;i<vars.length;i++){
			//注意此处必须new 一个新的对象，否则就会在多次调用的时候导致数据冲突
			OperateDataLocalVar var = new OperateDataLocalVar(vars[i].getName(),vars[i].type);
			context.addSymbol(var.getName(), var);
			var.setObject(context, parameters[i].getObject(environment.getContext()));
		}
		Object result =InstructionSetRunner.execute(new InstructionSet[]{(InstructionSet)functionSet},
				context,errorList,environment.isTrace(),false,true,log);
		return new OperateData(result,null);
	}
	public String toString(){
	  return "call Function[" + this.functionName +"] OPNUMBER["+ this.opDataNumber +"]"  ;	
	}

}