package com.ql.util.express.instruction.detail;

import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import com.ql.util.express.InstructionSet;
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
			
			Object result =InstructionSetRunner.execute(environment.getContext().getExpressRunner(),
					new InstructionSet[]{(InstructionSet)functionSet},environment.getContext().getExpressLoader(),
					environment.getContext(), errorList,
					environment.isTrace(),false,false,this.log,environment.getContext().isSupportDynamicFieldName());
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
		public void toJavaCode(Type classType,ClassWriter cw,GeneratorAdapter staticInitialMethod,GeneratorAdapter executeMethod,int index){
			throw new RuntimeException("还没有实现方法：toJavaCode" );
		}
	}