package com.ql.util.express.instruction.detail;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import com.ql.util.express.RunEnvironment;


public abstract class Instruction {
	protected static Log staticLog = LogFactory.getLog(Instruction.class);
	protected Log log = staticLog;
	public void setLog(Log aLog) {
		if (aLog != null) {
			this.log = aLog;
		}
	}
	public abstract void toJavaCode(Type classType,ClassWriter cw,GeneratorAdapter staticInitialMethod,GeneratorAdapter executeMethod,int index, Map<Integer,Label>  lables);
	
	public abstract void execute(RunEnvironment environment, List<String> errorList)
			throws Exception;
}
