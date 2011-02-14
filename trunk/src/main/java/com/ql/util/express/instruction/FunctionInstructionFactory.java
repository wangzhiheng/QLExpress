package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.parse.ExpressNode;

public class FunctionInstructionFactory extends InstructionFactory {
	public boolean createInstruction(ExpressRunner aCompile,InstructionSet result,
			Stack<ForRelBreakContinue> forStack, ExpressNode node,boolean isRoot)
			throws Exception {		
    	ExpressNode[] children = node.getChildren();
    	if(children.length != 3){
    		throw new Exception("funciton ��������Ҫ3�������� " );
    	}
		String functionName =children[0].getValue();
    	ExpressNode[] varDefines = children[1].getChildren();
    	int point =0;
    	InstructionSet functionSet = new InstructionSet(InstructionSet.TYPE_FUNCTION);
    	while(point<varDefines.length){
    		if(varDefines[point].isTypeEqualsOrChild("def") == false){
    		  throw new Exception("function�Ĳ����������," + varDefines[point] + "����һ��Class");
    		}
    		Class<?> varClass = (Class<?>)varDefines[point].getChildren()[0].getObjectValue();
    		String varName = varDefines[point].getChildren()[1].getValue();    		
    		OperateDataLocalVar tmpVar = new OperateDataLocalVar(varName,varClass);
    		functionSet.addParameter(tmpVar);
    		point = point + 2;
    	}
    	
    	ExpressNode functionRoot = new ExpressNode(aCompile.getNodeTypeManager().findNodeType("FUNCTION_DEFINE"),"function-" + functionName);
		for(ExpressNode tempNode :  children[2].getChildren()){
			functionRoot.addLeftChild(tempNode);
		}
		aCompile.createInstructionSet(functionRoot,functionSet);		
		result.addMacroDefine(functionName, new FunctionInstructionSet(functionName,"function",functionSet));
		return false;
	}
}
