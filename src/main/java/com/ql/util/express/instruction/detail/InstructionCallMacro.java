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
import com.ql.util.express.ExpressLoader;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.InstructionSetRunner;
import com.ql.util.express.OperateData;
import com.ql.util.express.RunEnvironment;


public class InstructionCallMacro extends Instruction{
		String name;
		public InstructionCallMacro(String aName){
			this.name = aName;
	    }
		
		public void execute(RunEnvironment environment,List<String> errorList)throws Exception{
			if(environment.isTrace()){
				log.debug(this);
			}
			Object functionSet = environment.getContext().getSymbol(this.name);
			
			Object result =InstructionSetRunner.execute(
					environment.getContext().getExpressRunner(),
					new InstructionSet[]{(InstructionSet)functionSet},
					environment.getContext().getExpressLoader(),
					environment.getContext(), 
					errorList,
					environment.isTrace(),
					false,false,this.log,
					environment.getContext().isSupportDynamicFieldName());
			if(result instanceof OperateData){
				environment.push((OperateData)result);
			}else{
			   environment.push(new OperateData(result,null));
			}
			
			environment.programPointAddOne();
		}
		public String toString(){
		  return "call macro " + this.name ;	
		}
		public void toJavaCode(Type classType,ClassWriter cw,GeneratorAdapter staticInitialMethod,GeneratorAdapter executeMethod,int index, Map<Integer,Label>  lables){
			//(InstructionSet)context.getSymbol(name);
			executeMethod.loadLocal(3);
			AsmUtil.transferCode(executeMethod,this.name);
			executeMethod.invokeVirtual(Type.getType(InstructionSetContext.class),
					Method.getMethod(Object.class.getName() + " getSymbol(String)"));
			executeMethod.unbox(Type.getType(InstructionSet.class));
			executeMethod.storeLocal(5,Type.getType(InstructionSet.class));
			
/*			Object result =InstructionSetRunner.execute(
					environment.getContext().getExpressRunner(),
					new InstructionSet[]{(InstructionSet)functionSet},
					environment.getC t().getExpressLoader(),
					environment.getContext(), 
					errorList,
					environment.isTrace(),
					false,false,this.log,
					environment.getContext().isSupportDynamicFieldName());*/
			
			//context().getExpressRunner()
			executeMethod.loadLocal(3);
			executeMethod.invokeVirtual(Type.getType(InstructionSetContext.class),Method.getMethod(ExpressRunner.class.getName() + " getExpressRunner()"));
			//new InstructionSet[]{(InstructionSet)functionSet}
			executeMethod.visitLdcInsn(1);
			executeMethod.newArray(Type.getType(InstructionSet.class));
			executeMethod.dup();
			executeMethod.visitLdcInsn(0);
			executeMethod.loadLocal(5);
			executeMethod.arrayStore(Type.getType(InstructionSet.class));
			//context().getExpressLoader()
			executeMethod.loadLocal(3);
			executeMethod.invokeVirtual(Type.getType(InstructionSetContext.class),Method.getMethod(ExpressLoader.class.getName() + " getExpressLoader()"));
			//context
			executeMethod.loadLocal(3);
			//errorList
			executeMethod.loadArg(1);
			//environment.isTrace()
			executeMethod.loadArg(0);
			executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),Method.getMethod("boolean isTrace()"));
			
			executeMethod.visitInsn(Opcodes.ICONST_0);
			executeMethod.visitInsn(Opcodes.ICONST_0);

			//log
			executeMethod.loadArg(2);
			//context().isSupportDynamicFieldName()
			executeMethod.loadLocal(3);
			executeMethod.invokeVirtual(Type.getType(InstructionSetContext.class),Method.getMethod("boolean isSupportDynamicFieldName()"));
			
			executeMethod.invokeStatic(Type.getType(InstructionSetRunner.class),Method.getMethod("Object execute("+ExpressRunner.class.getName()+","
					+InstructionSet.class.getName()+"[],"+ExpressLoader.class.getName()+","
				+IExpressContext.class.getName()+", java.util.List, boolean,boolean,boolean,"+Log.class.getName()+",boolean)"));
			
			executeMethod.storeLocal(6,Type.getType(Object.class));
			
			Label falseLabel = executeMethod.newLabel();
			Label lastLabel = executeMethod.newLabel();
			
			executeMethod.loadLocal(6);
			executeMethod.instanceOf(Type.getType(OperateData.class));
			executeMethod.ifZCmp(Opcodes.IFEQ, falseLabel);
			//environment.push((OperateData)result);	
			executeMethod.loadArg(0);
			executeMethod.loadLocal(6);
			executeMethod.checkCast(Type.getType(OperateData.class));
			executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),Method.getMethod("void push("+OperateData.class.getName()+")"));
			executeMethod.goTo(lastLabel);
			executeMethod.visitLabel(falseLabel);
			//environment.push(new OperateData(result,null));
			executeMethod.loadArg(0);
			executeMethod.newInstance(Type.getType(OperateData.class));
			executeMethod.dup();
			executeMethod.loadLocal(6);
			executeMethod.visitInsn(Opcodes.ACONST_NULL);
			//AsmUtil.transferCode(executeMethod,Object.class);
			executeMethod.invokeConstructor(Type.getType(OperateData.class),Method.getMethod("void <init>(Object,Class)"));
			executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),Method.getMethod("void push("+OperateData.class.getName()+")"));
			executeMethod.visitLabel(lastLabel);
		}
	}