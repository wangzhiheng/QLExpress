package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionNewVirClass;
import com.ql.util.express.parse.ExpressNode;


public class NewVClassInstructionFactory  extends InstructionFactory{
	public boolean createInstruction(ExpressRunner aCompile,
			InstructionSet result, Stack<ForRelBreakContinue> forStack,
			ExpressNode node, boolean isRoot) throws Exception {
		ExpressNode[] children = node.getChildren();
		node.getLeftChildren().remove(1);
		ExpressNode[] parameterList = children[1].getChildren();
		for (int i = 0; i < parameterList.length; i++) {
			if (parameterList[i].isTypeEqualsOrChild(",") == false) {
				node.getLeftChildren().add(parameterList[i]);
			}
		}

		boolean returnVal = false;
		children = node.getChildren();// 需要重新获取数据
		String virClassName = children[0].getValue();
		for (int i = 1; i < children.length; i++) {
			boolean tmpHas = aCompile.createInstructionSetPrivate(result,forStack, children[i], false);
			returnVal = returnVal || tmpHas;
		}
		result.addInstruction(new InstructionNewVirClass(virClassName, children.length -1));
		return returnVal;
	}
}
