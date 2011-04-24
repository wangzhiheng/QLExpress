package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionOperator;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.parse.ExpressNode;


public class InInstructionFactory  extends InstructionFactory{
	public boolean createInstruction(ExpressRunner aCompile,InstructionSet result,Stack<ForRelBreakContinue> forStack, ExpressNode node,boolean isRoot)
			throws Exception {
		boolean returnVal = false;		
		OperatorBase op = aCompile.getOperatorFactory().newInstance(node);
		ExpressNode[] rootchildren = node.getChildren();
		boolean tmpHas =    aCompile.createInstructionSetPrivate(result,forStack,rootchildren[0],false);
		returnVal = returnVal || tmpHas;
		
		ExpressNode[] inChildren = rootchildren[1].getChildren();
		for(int i =0;i < inChildren.length;i++){
			ExpressNode tmpNode = inChildren[i];
			tmpHas =    aCompile.createInstructionSetPrivate(result,forStack,tmpNode,false);
			returnVal = returnVal || tmpHas;
		}
	
		int len = inChildren.length;
		if (len > 0) {
			len = (len + 1) / 2;
		}
		len = len + 1;
		result.addInstruction(new InstructionOperator(op, len));
		return returnVal;
	}
}
