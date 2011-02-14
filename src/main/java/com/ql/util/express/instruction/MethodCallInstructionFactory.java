package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.parse.ExpressNode;


public class MethodCallInstructionFactory  extends InstructionFactory{
	public boolean createInstruction(ExpressRunner aCompile,InstructionSet result,Stack<ForRelBreakContinue> forStack, ExpressNode node,boolean isRoot)
			throws Exception {
		boolean returnVal = false;	
		OperatorBase op = aCompile.getOperatorFactory().newInstance(node);
		int len = -1;
		ExpressNode[] children = node.getChildren();
		for(int i =0;i < children.length;i++){
			ExpressNode tmpNode = children[i];
			boolean tmpHas =    aCompile.createInstructionSetPrivate(result,forStack,tmpNode,false);
			returnVal = returnVal || tmpHas;
		}
	
		len = children[children.length - 1].getChildren().length;
			if (len > 0) {
				len = (len + 1) / 2;
			}
		len = len + children.length - 1;
		result.addInstruction(new InstructionOperator(op, len));
		return returnVal;
	}
}
