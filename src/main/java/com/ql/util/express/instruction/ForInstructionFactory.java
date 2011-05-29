package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionCloseNewArea;
import com.ql.util.express.instruction.detail.InstructionGoTo;
import com.ql.util.express.instruction.detail.InstructionGoToWithCondition;
import com.ql.util.express.instruction.detail.InstructionOpenNewArea;
import com.ql.util.express.parse.ExpressNode;

public class ForInstructionFactory extends  InstructionFactory {
	public boolean createInstruction(ExpressRunner aCompile,InstructionSet result,
			Stack<ForRelBreakContinue> forStack, ExpressNode node,boolean isRoot)
			throws Exception {		
    	if(node.getChildren().length < 2){
    		throw new Exception("for ������������Ҫ2�������� " );
    	}else if(node.getChildren().length > 2){
    		throw new Exception("for ���������ֻ��2�������� " );
    	}
    	if(node.getChildren()[0].getChildren()!= null && node.getChildren()[0].getChildren().length > 3){
    		throw new Exception("ѭ���������ò�����:" + node.getChildren()[0]);	
    	}
    	//����������ʼָ��
	    result.addInstruction(new InstructionOpenNewArea());			
	    forStack.push(new ForRelBreakContinue());
	    
    	//����������䲿��ָ��
    	ExpressNode conditionNode = node.getChildren()[0];
    	int nodePoint = 0;
    	if (conditionNode.getChildren() != null && conditionNode.getChildren().length == 3){//�������壬�жϣ�����������
    		int tempPoint = result.getCurrentPoint();
    		aCompile.createInstructionSetPrivate(result,forStack,conditionNode.getChildren()[0],false);
    		if(result.getCurrentPoint() > tempPoint){
    		   nodePoint = nodePoint + 1;
    		}
    	}
    	//ѭ���Ŀ�ʼ��λ��
    	int loopStartPoint = result.getCurrentPoint()+ 1;
    	
    	//���������
    	InstructionGoToWithCondition conditionInstruction=null;
    	if(conditionNode.getChildren() != null 
    		&& (conditionNode.getChildren().length == 1
    			|| conditionNode.getChildren().length == 2 
    			|| conditionNode.getChildren().length == 3)
    		)	{    		
    		aCompile.createInstructionSetPrivate(result,forStack,conditionNode.getChildren()[nodePoint],false);
    		//��ת��λ����Ҫ���ݺ�����ָ���������    		
    		conditionInstruction = new InstructionGoToWithCondition(false,-1,true);
    		result.insertInstruction(result.getCurrentPoint()+1,conditionInstruction);   
    		nodePoint = nodePoint+ 1;
    	}
    	int conditionPoint = result.getCurrentPoint();
    	//����ѭ����Ĵ���
    	aCompile.createInstructionSetPrivate(result,forStack,node.getChildren()[1],false);
    	
    	int selfAddPoint = result.getCurrentPoint()+1;
    	//������������ָ��
    	if(conditionNode.getChildren()!= null &&(
    			conditionNode.getChildren().length == 2 || conditionNode.getChildren().length == 3
    			)){
    		aCompile.createInstructionSetPrivate(result,forStack,conditionNode.getChildren()[nodePoint],false);
    	}
    	//����һ����������ת
    	InstructionGoTo reStartGoto = new InstructionGoTo(loopStartPoint - (result.getCurrentPoint() + 1));
    	result.addInstruction(reStartGoto); 
    	
    	//�޸������жϵ���תλ��
    	if(conditionInstruction != null){
    	   conditionInstruction.setOffset( result.getCurrentPoint() - conditionPoint + 1);
    	}
    	
    	//�޸�Break��Continueָ�����תλ��,ѭ������
    	ForRelBreakContinue rel =  forStack.pop();
    	for(InstructionGoTo item:rel.breakList){
    		item.setOffset(result.getCurrentPoint() -  item.getOffset()) ;
    	}
    	for(InstructionGoTo item:rel.continueList){
    		item.setOffset(selfAddPoint -  item.getOffset() - 1);
    	}    	
    	
    	//�������������ָ��
	    result.addInstruction(new InstructionCloseNewArea());

        return false;
	}
}
