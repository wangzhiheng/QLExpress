package com.ql.util.express.instruction.detail;

import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.ql.util.express.RunEnvironment;


public class InstructionClearDataStack extends Instruction{
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
	public void toJavaCode(Type classType,ClassWriter cw,GeneratorAdapter staticInitialMethod,GeneratorAdapter executeMethod,int index, Map<Integer,Label>  lables){
		executeMethod.loadArg(0);
		executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),Method.getMethod("void clearDataStack()"));
	}
}

