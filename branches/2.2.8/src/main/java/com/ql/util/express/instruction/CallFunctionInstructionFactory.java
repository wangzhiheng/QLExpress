package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionCallSelfDefineFunction;
import com.ql.util.express.instruction.detail.InstructionOperator;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.parse.ExpressNode;


public class CallFunctionInstructionFactory extends InstructionFactory{
	public boolean createInstruction(ExpressRunner aCompile,
			InstructionSet result, Stack<ForRelBreakContinue> forStack,
			ExpressNode node, boolean isRoot) throws Exception {
		ExpressNode[] children = node.getChildren();
		String functionName = children[0].getValue();

		node.getLeftChildren().clear();
		ExpressNode[] parameterList = children[1].getChildren();
		for (int i = 0; i < parameterList.length; i++) {
			if (parameterList[i].isTypeEqualsOrChild(",") == false) {
				node.getLeftChildren().add(parameterList[i]);
			}
		}

		boolean returnVal = false;
		children = node.getChildren();
		for (int i = 0; i < children.length; i++) {
			boolean tmpHas = aCompile.createInstructionSetPrivate(result,
					forStack, children[i], false);
			returnVal = returnVal || tmpHas;
		}

		OperatorBase op = aCompile.getOperatorFactory().getOperator(
				functionName);
		if (op != null) {
			result.addInstruction(new InstructionOperator(op, children.length));
		} else {
			result.addInstruction(new InstructionCallSelfDefineFunction(
					functionName, children.length));
		}
		return returnVal;
	}
}


