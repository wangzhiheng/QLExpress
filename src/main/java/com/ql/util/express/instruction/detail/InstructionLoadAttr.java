package com.ql.util.express.instruction.detail;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.ql.util.express.AsmUtil;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.instruction.opdata.OperateDataAttr;

public class InstructionLoadAttr extends Instruction{
    String attrName;
    public InstructionLoadAttr(String aName){
    	this.attrName = aName;
    }
	public void execute(RunEnvironment environment,List<String> errorList)throws Exception{
		Object o = environment.getContext().getSymbol(this.attrName);
		if(o != null && o instanceof InstructionSet){//�Ǻ�������ִ��
			if(environment.isTrace()){
				log.debug("ָ��ת���� LoadAttr -- >CallMacro ");						
			}
			InstructionCallMacro macro = new InstructionCallMacro(this.attrName);
			macro.setLog(this.log);
			macro.execute(environment, errorList);
			//ע�⣬�˴�����������ָ���Ϊ��InstructionCallMacro�Ѿ����� environment.programPointAddOne();
		}else{
			if(environment.isTrace()){
				log.debug(this +":" + ((OperateDataAttr)o).getObject(environment.getContext()));						
			}
		    environment.push((OperateDataAttr)o);
		    environment.programPointAddOne();
		}
	}
	public void toJavaCode(Type classType,ClassWriter cw,GeneratorAdapter staticInitialMethod,GeneratorAdapter executeMethod,int index, Map<Integer,Label>  lables){
		//û�д�����
		executeMethod.loadLocal(3);
		AsmUtil.transferCode(executeMethod,this.attrName);
		executeMethod.invokeVirtual(Type.getType(InstructionSetContext.class),Method.getMethod("Object getSymbol(String)"));
		executeMethod.storeLocal(5,Type.getType(Object.class));
		
		Label falseLabel = executeMethod.newLabel();
		Label lastLabel = executeMethod.newLabel();
		
		executeMethod.loadLocal(5);
		executeMethod.ifNull(falseLabel);
		executeMethod.loadLocal(5);
		executeMethod.instanceOf(Type.getType(InstructionSet.class));
		executeMethod.ifZCmp(Opcodes.IFEQ, falseLabel);
		
		executeMethod.newInstance(Type.getType(InstructionCallMacro.class));
		executeMethod.dup();
		AsmUtil.transferCode(executeMethod,this.attrName);
		executeMethod.invokeConstructor(Type.getType(InstructionCallMacro.class),Method.getMethod("void <init>(String)"));
		executeMethod.storeLocal(6,Type.getType(InstructionCallMacro.class));
//		//macro.setLog(this.log);
		executeMethod.loadLocal(6);
		executeMethod.loadArg(2);
		executeMethod.invokeVirtual(Type.getType(Instruction.class), Method.getMethod("void setLog(" + Log.class.getName() + ")"));
//		//macro.execute(environment, errorList);//
		executeMethod.loadLocal(6);
		executeMethod.loadArg(0);
		executeMethod.loadArg(1);
		executeMethod.invokeVirtual(Type.getType(InstructionCallMacro.class), 
				Method.getMethod("void execute(" + RunEnvironment.class.getName()+"," + List.class.getName() + ")"));
		
		executeMethod.goTo(lastLabel);
		//fals����
		executeMethod.visitLabel(falseLabel);
		executeMethod.loadArg(0);
		executeMethod.loadLocal(5);
		executeMethod.unbox(Type.getType(OperateData.class));
		executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),Method.getMethod("void push(" + OperateData.class.getName() + ")"));
		executeMethod.visitLabel(lastLabel);
		System.out.println();
	}
	public String toString(){
		  return "LoadAttr:" +this.attrName;	
	}
}
