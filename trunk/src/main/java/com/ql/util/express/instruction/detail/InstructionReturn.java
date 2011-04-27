package com.ql.util.express.instruction.detail;

import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import com.ql.util.express.RunEnvironment;

public class InstructionReturn extends Instruction{
	public void execute(RunEnvironment environment,List<String> errorList)throws Exception{
		//Ŀǰ��ģʽ������Ҫִ���κβ���
		if(environment.isTrace()){
			log.debug(this);
		}
		if(environment.getDataStackSize() >= 1){
			
		   environment.quitExpress(environment.pop().getObject(environment.getContext()));
		}else{
		   environment.quitExpress(null);
		}
		environment.programPointAddOne();
	}
	public void toJavaCode(Type classType,ClassWriter cw,GeneratorAdapter staticInitialMethod,GeneratorAdapter executeMethod,int index){
		throw new RuntimeException("��û��ʵ�ַ�����toJavaCode" );
	}
	public String toString(){
	  return "return ";	
	}	
}