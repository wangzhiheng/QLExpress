package com.ql.util.express.instruction.detail;

import java.util.List;

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
	
	public void execute(RunEnvironment environment,List<String> errorList)throws Exception{
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
		
		OperateData result = null;
		if(operator != null){
			result = operator.execute(environment.getContext(),parameters, errorList);
		}else{
			Object obj = environment.getContext().getSymbol(functionName);
			if(obj == null || obj instanceof InstructionSet == false){
				throw new Exception("��Runner�Ĳ�����������Զ��庯���ж�û���ҵ�\"" + this.functionName + "\"�Ķ���");
			}
			InstructionSet functionSet = (InstructionSet)environment.getContext().getSymbol(functionName);
			result = executeSelfFunction(environment,functionSet,parameters,errorList);
		}
		environment.push(result);
		environment.programPointAddOne();
	}
	public OperateData executeSelfFunction(RunEnvironment environment,InstructionSet functionSet,OperateData[] parameters,List<String> errorList)throws Exception{	
		InstructionSetContext<String, Object> context = new InstructionSetContext<String, Object>(
				environment.getContext().getExpressRunner(),environment.getContext(),environment.getContext().getExpressLoader(),environment.getContext().isSupportDynamicFieldName());
		OperateDataLocalVar[] vars = functionSet.getParameters();
		for(int i=0;i<vars.length;i++){
			//ע��˴�����new һ���µĶ��󣬷���ͻ��ڶ�ε��õ�ʱ�������ݳ�ͻ
			OperateDataLocalVar var = new OperateDataLocalVar(vars[i].getName(),vars[i].type);
			context.addSymbol(var.getName(), var);
			var.setObject(context, parameters[i].getObject(environment.getContext()));
		}
		Object result =InstructionSetRunner.execute(new InstructionSet[]{(InstructionSet)functionSet},
				context,errorList,environment.isTrace(),false,true,this.log);
		return new OperateData(result,null);
	}
	public String toString(){
	  return "call Function[" + this.functionName +"] OPNUMBER["+ this.opDataNumber +"]"  ;	
	}
	public void toJavaCode(StringBuilder staticFieldDefine,StringBuilder methodDefine,int index){
		throw new RuntimeException("��û��ʵ�ַ�����toJavaCode" );
	}
}