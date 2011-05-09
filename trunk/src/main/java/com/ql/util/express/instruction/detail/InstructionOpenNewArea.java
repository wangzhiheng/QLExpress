package com.ql.util.express.instruction.detail;

import java.util.List;

import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.RunEnvironment;

public class InstructionOpenNewArea extends Instruction{
	public void execute(RunEnvironment environment,List<String> errorList)throws Exception{
		//Ŀǰ��ģʽ������Ҫִ���κβ���
		if(environment.isTrace()){
			log.debug(this);
		}
		InstructionSetContext<String, Object> parentContext = environment.getContext();
		environment.setContext(new InstructionSetContext<String, Object>(
				parentContext.getExpressRunner(),
				parentContext,
				parentContext.getExpressLoader(),
				parentContext.isSupportDynamicFieldName()));
		environment.programPointAddOne();
	}
	public String toString(){
	  return "openNewArea";	
	}
}
