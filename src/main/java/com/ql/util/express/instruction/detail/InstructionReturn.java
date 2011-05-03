package com.ql.util.express.instruction.detail;

import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.RunEnvironment;

public class InstructionReturn extends Instruction{
	boolean haveReturnValue;
	public InstructionReturn(boolean aHaveReturnValue){
		this.haveReturnValue = aHaveReturnValue;
	}
	public void execute(RunEnvironment environment,List<String> errorList)throws Exception{
		//目前的模式，不需要执行任何操作
		if(environment.isTrace()){
			log.debug(this);
		}
		if(this.haveReturnValue == true){			
		   environment.quitExpress(environment.pop().getObject(environment.getContext()));
		}else{
		   environment.quitExpress();
		}
		environment.programPointAddOne();
	}
	public void toJavaCode(Type classType,ClassWriter cw,GeneratorAdapter staticInitialMethod,GeneratorAdapter executeMethod,int index, Map<Integer,Label>  lables){
		if(this.haveReturnValue == true){
			executeMethod.loadArg(0);
			executeMethod.loadArg(0);
			executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),Method.getMethod(OperateData.class.getName() + " pop()"));
			executeMethod.loadLocal(3);
			executeMethod.invokeVirtual(Type.getType(OperateData.class),Method.getMethod("Object getObject(" + InstructionSetContext.class.getName()+")"));
			executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),Method.getMethod("void quitExpress(Object)"));			
		}else{
			executeMethod.loadArg(0);
			executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),Method.getMethod("void quitExpress()"));
		}
		executeMethod.returnValue();
	}
	public String toString(){
	  return "return ";	
	}	
}