package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionOperator;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.parse.ExpressNode;


public class CastInstructionFactory  extends InstructionFactory{
	public boolean createInstruction(ExpressRunner aCompile,InstructionSet result,Stack<ForRelBreakContinue> forStack, ExpressNode node,boolean isRoot)
			throws Exception {
		boolean returnVal = false;	
		OperatorBase op = aCompile.getOperatorFactory().newInstance(node);
		ExpressNode[] children = node.getChildren();
		if(children.length ==0){
			throw new Exception("��չ���Ͳ�����");
		}else if(children.length > 2) {
			throw new Exception("��չ����ֻ����һ������ΪClass�Ĳ�����");
		}else if(children[0].getNodeType().isEqualsOrChild("CONST_CLASS") == false){
			throw new Exception("��չ����ֻ����һ������ΪClass�Ĳ�����,��ǰ�����������ǣ�" + children[0].getNodeType().getName());
		}
		
		for(int i =0;i < children.length;i++){
			boolean tmpHas =    aCompile.createInstructionSetPrivate(result,forStack,children[i],false);
			returnVal = returnVal || tmpHas;
		}	
		result.addInstruction(new InstructionOperator(op,children.length));
		return returnVal;
	}
}
