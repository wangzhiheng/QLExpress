package com.ql.util.express.instruction;

import java.util.List;
import java.util.Stack;

import com.ql.util.express.ExportItem;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.OperateData;
import com.ql.util.express.parse.ExpressNode;


class OperatorInstructionFactory  extends InstructionFactory{
	public boolean createInstruction(ExpressRunner aCompile,InstructionSet result,Stack<ForRelBreakContinue> forStack, ExpressNode node,boolean isRoot)
			throws Exception {
		boolean returnVal = false;		
		ExpressNode[] children = node.getChildren();
		int [] finishPoint = new int[children.length];
		for(int i =0;i < children.length;i++){
			ExpressNode tmpNode = children[i];
			boolean tmpHas =    aCompile.createInstructionSetPrivate(result,forStack,tmpNode,false);
			returnVal = returnVal || tmpHas;
			finishPoint[i] = result.getCurrentPoint();
		}
		
		if(node.isTypeEqualsOrChild("return")){
			result.addInstruction(new InstructionReturn());	
		}else{	
			OperatorBase op = aCompile.getOperatorFactory().newInstance(node);
			result.addInstruction(new InstructionOperator(op,children.length));
			if(node.isTypeEqualsOrChild("&&")){
				result.insertInstruction(finishPoint[0]+1,new InstructionGoToWithCondition(false,result.getCurrentPoint() - finishPoint[0] + 1,false));
			}else if(node.isTypeEqualsOrChild("||")){
				result.insertInstruction(finishPoint[0]+1,new InstructionGoToWithCondition(true,result.getCurrentPoint() - finishPoint[0] + 1,false));
			}else if(node.isTypeEqualsOrChild("def") || node.isTypeEqualsOrChild("alias")){
				returnVal = true;
			}else if(node.isTypeEqualsOrChild("exportDef")){
				//添加对外的变量声明
				result.addExportDef(new ExportItem(children[1].toString(),ExportItem.TYPE_DEF,"还没有实现"));
			}else if(node.isTypeEqualsOrChild("exportAlias")){
				result.addExportDef(new ExportItem(children[0].toString(),ExportItem.TYPE_ALIAS,"还没有实现"));
			}
		}
		return returnVal;
	}
}

class InstructionOperator extends Instruction{
	OperatorBase operator;
	int opDataNumber;
	public InstructionOperator(OperatorBase aOperator,int aOpDataNumber){
	  this.operator = aOperator;
	  this.opDataNumber =aOpDataNumber;
	}
	public void execute(RunEnvironment environment,List<String> errorList) throws Exception{		
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