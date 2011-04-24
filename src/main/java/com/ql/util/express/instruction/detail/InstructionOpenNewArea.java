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
		environment.setContext(new InstructionSetContext<String, Object>(
				environment.getContext().getExpressRunner(),environment.getContext(),environment.getContext().getExpressLoader(),environment.getContext().isSupportDynamicFieldName()));
		environment.programPointAddOne();
	}
	public void toJavaCode(StringBuilder staticFieldDefine,StringBuilder methodDefine,int index){
		throw new RuntimeException("��û��ʵ�ַ�����toJavaCode" );
	}
	public String toString(){
	  return "openNewArea";	
	}
}
