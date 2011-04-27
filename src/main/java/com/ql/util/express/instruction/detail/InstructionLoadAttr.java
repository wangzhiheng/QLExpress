package com.ql.util.express.instruction.detail;

import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import com.ql.util.express.InstructionSet;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.instruction.opdata.OperateDataAttr;

public class InstructionLoadAttr extends Instruction{
    String attrName;
    public InstructionLoadAttr(String aName){
    	this.attrName = aName;
    }
	public void execute(RunEnvironment environment,List<String> errorList)throws Exception{
		Object o = environment.getContext().getSymbol(this.attrName);
		if(o != null && o instanceof InstructionSet){//是函数，则执行
			if(environment.isTrace()){
				log.debug("指令转换： LoadAttr -- >CallMacro ");						
			}
			InstructionCallMacro macro = new InstructionCallMacro(this.attrName);
			macro.setLog(this.log);
			macro.execute(environment, errorList);
		}else{
			if(environment.isTrace()){
				log.debug(this +":" + ((OperateDataAttr)o).getObject(environment.getContext()));						
			}
		    environment.push((OperateDataAttr)o);
		    environment.programPointAddOne();
		}
	}
	public void toJavaCode(Type classType,ClassWriter cw,GeneratorAdapter staticInitialMethod,GeneratorAdapter executeMethod,int index){
		throw new RuntimeException("还没有实现方法：toJavaCode" );
	}
	public String toString(){
		  return "LoadAttr:" +this.attrName;	
	}
}
