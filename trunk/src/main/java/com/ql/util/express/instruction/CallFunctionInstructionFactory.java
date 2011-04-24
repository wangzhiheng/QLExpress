package com.ql.util.express.instruction;

import java.util.List;
import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.parse.ExpressNode;


public class CallFunctionInstructionFactory extends InstructionFactory{

	public boolean createInstruction(ExpressRunner aCompile,InstructionSet result,Stack<ForRelBreakContinue> forStack, ExpressNode node,boolean isRoot)
			throws Exception {
		boolean returnVal = false;		
		ExpressNode[] children = node.getChildren();
		String functionName = children[0].getValue();
		ExpressNode[] parameters = children[1].getChildren();
		for(int i =0;i < parameters.length;i++){
			ExpressNode tmpNode = parameters[i];
			boolean tmpHas =    aCompile.createInstructionSetPrivate(result,forStack,tmpNode,false);
			returnVal = returnVal || tmpHas;
		}
		int len = parameters.length;
		if (len > 0) {
			len = (len + 1) / 2;
		}
		
		OperatorBase operator = aCompile.getOperatorFactory().getOperator(functionName);
		result.addInstruction(new InstructionCallSelfDefineFunction(operator,functionName,len));
		return returnVal;
	}
}

class InstructionCallSelfDefineFunction extends Instruction{
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
				throw new Exception("在Runner的操作符定义和自定义函数中都没有找到\"" + this.functionName + "\"的定义");
			}
			InstructionSet functionSet = (InstructionSet)environment.getContext().getSymbol(functionName);
			result = executeSelfFunction(environment,functionSet,parameters,errorList);
		}
		environment.push(result);
		environment.programPointAddOne();
	}
	public OperateData executeSelfFunction(RunEnvironment environment,InstructionSet functionSet,OperateData[] parameters,List<String> errorList)throws Exception{	
		InstructionSetContext<String, Object> context = new InstructionSetContext<String, Object>(
			environment.getContext(),environment.getContext().getExpressLoader(),environment.getContext().isSupportDynamicFieldName());
		OperateDataLocalVar[] vars = functionSet.getParameters();
		for(int i=0;i<vars.length;i++){
			//注意此处必须new 一个新的对象，否则就会在多次调用的时候导致数据冲突
			OperateDataLocalVar var = new OperateDataLocalVar(vars[i].name,vars[i].type);
			context.addSymbol(var.name, var);
			var.setObject(context, parameters[i].getObject(environment.getContext()));
		}
		Object result =InstructionSet.execute(new InstructionSet[]{(InstructionSet)functionSet},
				context,errorList,environment.isTrace(),false,true,this.log);
		return new OperateData(result,null);
	}
	public String toString(){
	  return "call Function[" + this.functionName +"] OPNUMBER["+ this.opDataNumber +"]"  ;	
	}
}
