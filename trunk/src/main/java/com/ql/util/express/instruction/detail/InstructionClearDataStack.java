package com.ql.util.express.instruction.detail;

import java.util.List;

import com.ql.util.express.RunEnvironment;


public class InstructionClearDataStack extends Instruction{
	public void execute(RunEnvironment environment,List<String> errorList)throws Exception{
		//Ŀǰ��ģʽ������Ҫִ���κβ���
		if(environment.isTrace() && log.isDebugEnabled()){
			log.debug(this);
		}
		environment.clearDataStack();
		environment.programPointAddOne();
	}
	public String toString(){
	  return "clearDataStack";	
	}
}

