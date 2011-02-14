package com.ql.util.express.instruction;

import java.util.List;
import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.parse.ExpressNode;

public class CacheFunctionCallInstructionFactory extends  InstructionFactory {
	public boolean createInstruction(ExpressRunner aCompile,InstructionSet result,
			Stack<ForRelBreakContinue> forStack, ExpressNode node,boolean isRoot)
			throws Exception {		
		boolean returnVal = false;
		for(ExpressNode item: node.getChildren()){
			boolean tmpHas =    aCompile.createInstructionSetPrivate(result,forStack,item,false);
			returnVal = returnVal || tmpHas;
		}
		result.addInstruction(new InstructionCacheFuncitonCall());
		return returnVal;
	}
}
class InstructionCacheFuncitonCall extends Instruction{
	InstructionCacheFuncitonCall(){
    }
	public void execute(RunEnvironment environment,List<String> errorList)throws Exception{
		//目前的模式，不需要执行任何操作
		if(environment.isTrace()){
			log.debug(this);
		}
		environment.getContext().startFunctionCallCache();
		environment.programPointAddOne();
	}
	public String toString(){
	  return "cacheFunctionCall";	
	}
}
