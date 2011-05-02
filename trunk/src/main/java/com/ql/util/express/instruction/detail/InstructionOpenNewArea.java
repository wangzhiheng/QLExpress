package com.ql.util.express.instruction.detail;

import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.ql.util.express.ExpressLoader;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.RunEnvironment;

public class InstructionOpenNewArea extends Instruction{
	public void execute(RunEnvironment environment,List<String> errorList)throws Exception{
		//目前的模式，不需要执行任何操作
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
	public void toJavaCode(Type classType,ClassWriter cw,GeneratorAdapter staticInitialMethod,GeneratorAdapter executeMethod,int index, Map<Integer,Label>  lables){
		executeMethod.loadArg(0);
		executeMethod.newInstance(Type.getType(InstructionSetContext.class));
		executeMethod.dup();
		executeMethod.loadLocal(3);
		executeMethod.invokeVirtual(Type.getType(InstructionSetContext.class),Method.getMethod(ExpressRunner.class.getName() + " getExpressRunner()"));
		executeMethod.loadLocal(3);
		executeMethod.loadLocal(3);
		executeMethod.invokeVirtual(Type.getType(InstructionSetContext.class),Method.getMethod(ExpressLoader.class.getName() + " getExpressLoader()"));
		executeMethod.loadLocal(3);
		executeMethod.invokeVirtual(Type.getType(InstructionSetContext.class),Method.getMethod(boolean.class.getName() + " isSupportDynamicFieldName()"));
		executeMethod.invokeConstructor(Type.getType(InstructionSetContext.class), Method.getMethod("void <init>("
				+ ExpressRunner.class.getName() + ","
				+ IExpressContext.class.getName()+ ","
				+ ExpressLoader.class.getName() +","
				+ "boolean"
				+ ")"));	
		//特别注意，需要更新临时变量context的值
		executeMethod.storeLocal(3);
		executeMethod.loadLocal(3);
		executeMethod.invokeVirtual(Type.getType(RunEnvironment.class), Method.getMethod("void setContext("+ InstructionSetContext.class.getName()+")"));
	}
	public String toString(){
	  return "openNewArea";	
	}
}
