package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.ExpressUtil;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionOperator;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.parse.ExpressNode;


public class NewInstructionFactory  extends InstructionFactory{
	public boolean createInstruction(ExpressRunner aCompile,
			InstructionSet result, Stack<ForRelBreakContinue> forStack,
			ExpressNode node, boolean isRoot) throws Exception {
		ExpressNode[] children = node.getChildren();
		if (node.isTypeEqualsOrChild("NEW_ARRAY")) {
			String tempStr = children[0].getValue();
			for (int i = 0; i < children.length - 1; i++) {
				tempStr = tempStr + "[]";
			}
			children[0].setValue(tempStr);
			children[0].setOrgiValue(tempStr);
			children[0].setObjectValue(ExpressUtil.getJavaClass(tempStr));
		} else if (node.isTypeEqualsOrChild("NEW_OBJECT")) {
			node.getLeftChildren().remove(1);
			ExpressNode[] parameterList = children[1].getChildren();
			for (int i = 0; i < parameterList.length; i++) {
				if (parameterList[i].isTypeEqualsOrChild(",") == false) {
					node.getLeftChildren().add(parameterList[i]);
				}
			}
		} else {
			throw new Exception("��֧�ֵ����ͣ�" + node.getTreeType().getTag());
		}

		boolean returnVal = false;
		children = node.getChildren();// ��Ҫ���»�ȡ����
		for (int i = 0; i < children.length; i++) {
			boolean tmpHas = aCompile.createInstructionSetPrivate(result,forStack, children[i], false);
			returnVal = returnVal || tmpHas;
		}
		OperatorBase op = aCompile.getOperatorFactory().newInstance(node);
		result.addInstruction(new InstructionOperator(op, children.length));
		return returnVal;
	}
}
