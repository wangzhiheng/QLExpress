package com.ql.util.express.instruction.detail;

import java.util.List;

import org.apache.commons.logging.Log;

import com.ql.util.express.InstructionSet;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.InstructionSetRunner;
import com.ql.util.express.OperateData;
import com.ql.util.express.OperateDataCacheManager;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.instruction.opdata.OperateDataAttr;
import com.ql.util.express.instruction.opdata.OperateDataLocalVar;

public class InstructionCallSelfDefineFunction extends Instruction{
	private static final long serialVersionUID = 8315682251443515151L;
	String functionName;
	int opDataNumber;
	public InstructionCallSelfDefineFunction(String name,int aOpDataNumber){
	  this.functionName = name;
	  this.opDataNumber =aOpDataNumber;
	}
	
	public void execute(RunEnvironment environment, List<String> errorList)
			throws Exception {
			OperateData[] parameters = environment.popArray(
					environment.getContext(), this.opDataNumber);
			if (environment.isTrace() && log.isDebugEnabled()) {
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
				throw new Exception("��Runner�Ĳ�����������Զ��庯���ж�û���ҵ�\""
						+ this.functionName + "\"�Ķ���");
			}
			InstructionSet functionSet = (InstructionSet)function;
			OperateData result = InstructionCallSelfDefineFunction
					.executeSelfFunction(environment, functionSet, parameters,
							errorList, this.log);
			environment.push(result);
			environment.programPointAddOne();
	}	
	public static OperateData executeSelfFunction(RunEnvironment environment,InstructionSet functionSet,
			OperateData[] parameters,List<String> errorList,Log log)throws Exception{	
		InstructionSetContext  context = OperateDataCacheManager.fetchInstructionSetContext (
				true,environment.getContext().getExpressRunner(),environment.getContext(),environment.getContext().getExpressLoader(),environment.getContext().isSupportDynamicFieldName());
		OperateDataLocalVar[] vars = functionSet.getParameters();
		for(int i=0;i<vars.length;i++){
			//ע��˴�����new һ���µĶ��󣬷���ͻ��ڶ�ε��õ�ʱ�������ݳ�ͻ
			OperateDataLocalVar var = OperateDataCacheManager.fetchOperateDataLocalVar(vars[i].getName(),vars[i].getOrgiType());
			context.addSymbol(var.getName(), var);
			var.setObject(context, parameters[i].getObject(environment.getContext()));
		}
		Object result =InstructionSetRunner.execute(new InstructionSet[]{(InstructionSet)functionSet},
				context,errorList,environment.isTrace(),false,true,log);
		return OperateDataCacheManager.fetchOperateData(result,null);
	}
	public String toString(){
	  return "call Function[" + this.functionName +"] OPNUMBER["+ this.opDataNumber +"]"  ;	
	}

}