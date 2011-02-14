package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.parse.ExpressNode;


public class CastInstructionFactory  extends InstructionFactory{
	public boolean createInstruction(ExpressRunner aCompile,InstructionSet result,Stack<ForRelBreakContinue> forStack, ExpressNode node,boolean isRoot)
			throws Exception {
		boolean returnVal = false;	
		OperatorBase op = aCompile.getOperatorFactory().newInstance(node);
		ExpressNode[] children = node.getChildren();
		ExpressNode[] castClass = children[0].getChildren();
		if(castClass.length ==0){
			throw new Exception("��չ���Ͳ�����");
		}else if(castClass.length > 1) {
			throw new Exception("��չ����ֻ����һ������ΪClass�Ĳ�����");
		}else if(castClass[0].getNodeType().isEqualsOrChild("CONST_CLASS") == false){
			throw new Exception("��չ����ֻ����һ������ΪClass�Ĳ�����,��ǰ�����������ǣ�" + castClass[0].getNodeType().getTag());
		}
		
		for(int i =0;i < children.length;i++){
			ExpressNode tmpNode = children[i];
			boolean tmpHas =    aCompile.createInstructionSetPrivate(result,forStack,tmpNode,false);
			returnVal = returnVal || tmpHas;
		}	
		result.addInstruction(new InstructionOperator(op,children.length));
		return returnVal;
	}
}
