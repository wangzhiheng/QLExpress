package com.ql.util.express.instruction.detail;

import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import com.ql.util.express.RunEnvironment;


public class InstructionClearDataStack extends Instruction{
	public void execute(RunEnvironment environment,List<String> errorList)throws Exception{
		//Ŀǰ��ģʽ������Ҫִ���κβ���
		if(environment.isTrace()){
			log.debug(this);
		}
		environment.clearDataStack();
		environment.programPointAddOne();
	}
	public String toString(){
	  return "clearDataStack";	
	}
	public void toJavaCode(Type classType,ClassWriter cw,GeneratorAdapter staticInitialMethod,GeneratorAdapter executeMethod,int index){
		throw new RuntimeException("��û��ʵ�ַ�����toJavaCode" );
	}
}

