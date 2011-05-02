package com.ql.util.express.instruction.detail;

import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.ql.util.express.IExpressContext;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.RunEnvironment;

public class InstructionCloseNewArea extends Instruction{
	public void execute(RunEnvironment environment,List<String> errorList)throws Exception{
		//目前的模式，不需要执行任何操作
		if(environment.isTrace()){
			log.debug(this);
		}
		environment.setContext((InstructionSetContext<String, Object>)environment.getContext().getParent());
		environment.programPointAddOne();
	}
	public String toString(){
	  return "closeNewArea";	
	}
	public void toJavaCode(Type classType,ClassWriter cw,GeneratorAdapter staticInitialMethod,GeneratorAdapter executeMethod,int index, Map<Integer,Label>  lables){
		executeMethod.loadArg(0);
		executeMethod.loadLocal(3);
		executeMethod.invokeVirtual(Type.getType(InstructionSetContext.class),Method.getMethod(IExpressContext.class.getName() + " getParent()"));
		executeMethod.unbox(Type.getType(InstructionSetContext.class));
		//特别注意，需要恢复临时变量context的值
		executeMethod.storeLocal(3);
		executeMethod.loadLocal(3);
		executeMethod.invokeVirtual(Type.getType(RunEnvironment.class), Method.getMethod("void setContext("+ InstructionSetContext.class.getName()+")"));
	}
}