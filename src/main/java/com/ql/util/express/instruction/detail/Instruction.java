package com.ql.util.express.instruction.detail;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ql.util.express.RunEnvironment;


public abstract class Instruction {
	protected static Log staticLog = LogFactory.getLog(Instruction.class);
	protected Log log = staticLog;
	public void setLog(Log aLog) {
		if (aLog != null) {
			this.log = aLog;
		}
	}
	public abstract void toJavaCode(StringBuilder staticFieldDefine,StringBuilder methodDefine,int index);
	
	public abstract void execute(RunEnvironment environment, List<String> errorList)
			throws Exception;
}
