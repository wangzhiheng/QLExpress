package com.ql.util.express.instruction.detail;

import java.util.List;

import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.RunEnvironment;

public class InstructionCloseNewArea extends Instruction{
	public void execute(RunEnvironment environment,List<String> errorList)throws Exception{
		//Ŀǰ��ģʽ������Ҫִ���κβ���
		if(environment.isTrace() && log.isDebugEnabled()){
			log.debug(this);
		}
		environment.setContext((InstructionSetContext<String, Object>)environment.getContext().getParent());
		environment.programPointAddOne();
	}
	public String toString(){
	  return "closeNewArea";	
	}
}