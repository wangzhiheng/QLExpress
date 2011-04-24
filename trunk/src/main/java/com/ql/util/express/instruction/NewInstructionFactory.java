package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.ExpressUtil;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionOperator;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.parse.ExpressNode;


public class NewInstructionFactory  extends InstructionFactory{
	public boolean createInstruction(ExpressRunner aCompile,InstructionSet result,Stack<ForRelBreakContinue> forStack, ExpressNode node,boolean isRoot)
			throws Exception {
		boolean returnVal = false;		
		OperatorBase op = aCompile.getOperatorFactory().newInstance(node);
		int len = -1;
		ExpressNode[] children = node.getChildren();
		if(node.isTypeEqualsOrChild("NEW_ARRAY")){
			String tempStr = children[0].getValue();
			for(int i=0;i<children.length - 1;i++){
			  tempStr = tempStr +"[]";	
			}
			children[0].setValue(tempStr);
			children[0].setOrgiValue(tempStr);
			children[0].setObjectValue(ExpressUtil.getJavaClass(tempStr));
		}
		
		
		int [] finishPoint = new int[children.length];
		
		for(int i =0;i < children.length;i++){
			ExpressNode tmpNode = children[i];
			boolean tmpHas =    aCompile.createInstructionSetPrivate(result,forStack,tmpNode,false);
			returnVal = returnVal || tmpHas;
			finishPoint[i] = result.getCurrentPoint();
		}
	
		if (node.isTypeEqualsOrChild("NEW_OBJECT")) {
			len = children[1].getChildren().length;
			if (len > 0) {
				len = (len + 1) / 2;
			}
			len = len + 1;
		}else if(node.isTypeEqualsOrChild("NEW_ARRAY")){		
			len = children.length; 
		}else{
			throw new Exception("不支持的类型：" + node.getTreeType().getTag());
		}
		result.addInstruction(new InstructionOperator(op, len));
		return returnVal;
	}
}
