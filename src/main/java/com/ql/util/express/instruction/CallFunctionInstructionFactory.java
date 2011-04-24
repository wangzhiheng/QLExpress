package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionCallSelfDefineFunction;
import com.ql.util.express.instruction.op.OperatorBase;
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


