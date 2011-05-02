package com.ql.util.express.instruction.detail;

import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.RunEnvironment;

public class InstructionReturn extends Instruction{
	public void execute(RunEnvironment environment,List<String> errorList)throws Exception{
		//目前的模式，不需要执行任何操作
		if(environment.isTrace()){
			log.debug(this);
		}
		if(environment.getDataStackSize() >= 1){
			
		   environment.quitExpress(environment.pop().getObject(environment.getContext()));
		}else{
		   environment.quitExpress();
		}
		environment.programPointAddOne();
	}
	public void toJavaCode(Type classType,ClassWriter cw,GeneratorAdapter staticInitialMethod,GeneratorAdapter executeMethod,int index, Map<Integer,Label>  lables){
		
		Label falseLabel = executeMethod.newLabel(); 
		Label laseLabel = executeMethod.newLabel(); 
		executeMethod.loadArg(0);
		executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),Method.getMethod("int getDataStackSize()"));
		//AsmUtil.transferCode(executeMethod, 1);
		executeMethod.visitLdcInsn(1);
		executeMethod.ifZCmp(Opcodes.IF_ICMPLT, falseLabel);
		executeMethod.loadArg(0);
		executeMethod.loadArg(0);
		executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),Method.getMethod(OperateData.class.getName() + " pop()"));
		executeMethod.loadLocal(3);
		executeMethod.invokeVirtual(Type.getType(OperateData.class),Method.getMethod("Object getObject(" + InstructionSetContext.class.getName()+")"));
		executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),Method.getMethod("void quitExpress(Object)"));
		executeMethod.goTo(laseLabel);
		executeMethod.visitLabel(falseLabel);
		executeMethod.loadArg(0);
		executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),Method.getMethod("void quitExpress()"));
		executeMethod.visitLabel(laseLabel);
		executeMethod.returnValue();
	}
	public String toString(){
	  return "return ";	
	}	
}