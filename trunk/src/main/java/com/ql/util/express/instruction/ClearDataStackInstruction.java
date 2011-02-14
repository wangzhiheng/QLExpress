package com.ql.util.express.instruction;

import java.util.List;


public class ClearDataStackInstruction extends Instruction{
	public ClearDataStackInstruction(){
    }
	public void execute(RunEnvironment environment,List<String> errorList)throws Exception{
		//目前的模式，不需要执行任何操作
		if(environment.isTrace()){
			log.debug(this);
		}
		environment.clearDataStack();
		environment.programPointAddOne();
	}
	public String toString(){
	  return "clearDataStack";	
	}
}

