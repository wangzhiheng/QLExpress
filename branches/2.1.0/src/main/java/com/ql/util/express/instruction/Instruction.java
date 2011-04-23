package com.ql.util.express.instruction;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ql.util.express.InstructionSetContext;


public abstract class Instruction {
	protected static Log staticLog = LogFactory.getLog(Instruction.class);
	protected Log log = staticLog;
	public void setLog(Log aLog) {
		if (aLog != null) {
			this.log = aLog;
		}		
	}

	public abstract void execute(RunEnvironment environment, List<String> errorList)
			throws Exception;
}


class InstructionGoToWithCondition extends Instruction{
	/**
	 * 跳转指令的偏移量
	 */
    int offset;
    boolean condition;
    boolean isPopStackData;
    public InstructionGoToWithCondition(boolean aCondition,int aOffset,boolean aIsPopStackData){
    	this.offset = aOffset;
    	this.condition = aCondition;
    	this.isPopStackData = aIsPopStackData;
    }

	public void execute(RunEnvironment environment,List<String> errorList)throws Exception{

		Object o = null;
		if(this.isPopStackData == false){
		    o = environment.peek().getObject(environment.getContext());	
		}else{
			o = environment.pop().getObject(environment.getContext());	
		}
		if(o != null && o instanceof Boolean){
			if(((Boolean)o).booleanValue() == this.condition){
				if(environment.isTrace()){
					log.debug("goto +" + this.offset);
				}
				environment.gotoWithOffset(this.offset);
			}else{
				if(environment.isTrace()){
					log.debug("programPoint ++ ");
				}
				environment.programPointAddOne();
			}
		}else{
			throw new Exception("指令错误:" + o + " 不是Boolean");
		}
	}
	public String toString(){
	  String result = "GoToIf[" + this.condition +",isPop=" + this.isPopStackData +"] " ;
	  if(this.offset >=0){
		  result = result +"+";
	  }
	  result = result + this.offset;
	  return result;
	}
}
class InstructionGoTo extends Instruction{
	/**
	 * 跳转指令的偏移量
	 */
    int offset;
	public String name;

    public InstructionGoTo(int aOffset){
    	this.offset = aOffset;
    }

	public void execute(RunEnvironment environment,List<String> errorList) throws Exception {
		if(environment.isTrace()){
			log.debug(this);
		}
		environment.gotoWithOffset(this.offset);
	}
	public String toString(){
	  String result =  (this.name ==null?"":this.name +":") + "GoTo ";
	  if(this.offset >=0){
		  result = result +"+";
	  }
	  result = result + this.offset + " ";
	  return result;
	}
}


class InstructionReturn extends Instruction{
    InstructionReturn(){
    }
	public void execute(RunEnvironment environment,List<String> errorList)throws Exception{
		//目前的模式，不需要执行任何操作
		if(environment.isTrace()){
			log.debug(this);
		}
		if(environment.getDataStackSize() >= 1){
			
		   environment.quitExpress(environment.pop().getObject(environment.getContext()));
		}else{
		   environment.quitExpress(null);
		}
		environment.programPointAddOne();
	}
	public String toString(){
	  return "return ";	
	}	
}

class InstructionOpenNewArea extends Instruction{
	InstructionOpenNewArea(){
    }

	public void execute(RunEnvironment environment,List<String> errorList)throws Exception{
		//目前的模式，不需要执行任何操作
		if(environment.isTrace()){
			log.debug(this);
		}
		environment.setContext(new InstructionSetContext<String, Object>(
				environment.getContext(),environment.getContext().getExpressLoader()));
		environment.programPointAddOne();
	}
	public String toString(){
	  return "openNewArea";	
	}
}

class InstructionCloseNewArea extends Instruction{
	InstructionCloseNewArea(){
    }
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
}