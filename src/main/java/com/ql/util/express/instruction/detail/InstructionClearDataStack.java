package com.ql.util.express.instruction.detail;

import java.util.List;

import com.ql.util.express.RunEnvironment;


public class InstructionClearDataStack extends Instruction{
	public void execute(RunEnvironment environment,List<String> errorList)throws Exception{
		//目前的模式，不需要执行任何操作
		if(environment.isTrace()){
			log.debug(this);
		}
		environment.clearDataStack();
		environment.programPointAddOne();
	}
	public String toString(){
	  return "clearDataStack";	
	}
	public void toJavaCode(StringBuilder staticFieldDefine,StringBuilder methodDefine,int index){
		throw new RuntimeException("还没有实现方法：toJavaCode" );
	}
}

