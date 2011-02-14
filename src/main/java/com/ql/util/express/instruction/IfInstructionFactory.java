package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.parse.ExpressNode;

public class IfInstructionFactory extends  InstructionFactory {
	public boolean createInstruction(ExpressRunner aCompile,InstructionSet result,
			Stack<ForRelBreakContinue> forStack, ExpressNode node,boolean isRoot)
			throws Exception {		
		ExpressNode[] children = node.getChildren();
    	if(children.length < 3){
    		throw new Exception("if 操作符至少需要3个操作数 " );
    	}else if (children.length == 3) {
    		//补充一个分支
    		ExpressNode[] oldChilder =  children;
    		children = new ExpressNode[5];
    		children[0] = oldChilder[0];
    		children[1] = oldChilder[1];
    		children[2] = oldChilder[2];    		
    		children[3] = new ExpressNode(aCompile.getNodeTypeManager().findNodeType("else"),null); 
    		children[4] = new ExpressNode(aCompile.getNodeTypeManager().findNodeType("{}"),null);    			
    	}else if(children.length > 5){
    		throw new Exception("if 操作符最多只有5个操作数 " );
    	}
		int [] finishPoint = new int[children.length];
   		boolean r1 = aCompile.createInstructionSetPrivate(result,forStack,children[0],false);//condition	
		finishPoint[0] = result.getCurrentPoint();
		boolean r2 = aCompile.createInstructionSetPrivate(result,forStack,children[2],false);//true		
		result.insertInstruction(finishPoint[0]+1,new InstructionGoToWithCondition(false,result.getCurrentPoint() - finishPoint[0] + 2,true));
		finishPoint[2] = result.getCurrentPoint();
		boolean r3 = aCompile.createInstructionSetPrivate(result,forStack,children[4],false);//false
		result.insertInstruction(finishPoint[2]+1,new InstructionGoTo(result.getCurrentPoint() - finishPoint[2] + 1));  		
        return r1 || r2 || r3;
	}
}
