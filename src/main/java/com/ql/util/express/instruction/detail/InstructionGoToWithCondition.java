package com.ql.util.express.instruction.detail;

import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.ql.util.express.AsmUtil;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.RunEnvironment;

public class InstructionGoToWithCondition extends Instruction{
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
	public void toJavaCode(Type classType,ClassWriter cw,GeneratorAdapter staticInitialMethod,GeneratorAdapter executeMethod,int index,
			Map<Integer,Label> lables){
		executeMethod.loadArg(0);
		if(this.isPopStackData == false){
			executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),
	        		Method.getMethod(OperateData.class.getName() +" peek()"));
		    //o = environment.peek().getObject(environment.getContext());	
		}else{
			executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),
	        		Method.getMethod(OperateData.class.getName() +" pop()"));
			//o = environment.pop().getObject(environment.getContext());	
		}
		executeMethod.loadLocal(3);//context
		executeMethod.invokeVirtual(Type.getType(OperateData.class),
        		Method.getMethod(Object.class.getName() +" getObject("+ InstructionSetContext.class.getName() +")"));
		//AsmUtil.transferCode(executeMethod,false);
		executeMethod.unbox(Type.getType(Boolean.class));
		executeMethod.invokeVirtual(Type.getType(Boolean.class),
        		Method.getMethod(boolean.class.getName() +" booleanValue()"));		
		AsmUtil.transferCode(executeMethod,this.condition);
		executeMethod.invokeVirtual(Type.getType(Boolean.class),
        		Method.getMethod(boolean.class.getName() +" booleanValue()"));		
		
		Label label = lables.get(index + this.offset);
		executeMethod.ifZCmp(Opcodes.IF_ICMPEQ,label);
	}	
	public String toString(){
	  String result = "GoToIf[" + this.condition +",isPop=" + this.isPopStackData +"] " ;
	  if(this.offset >=0){
		  result = result +"+";
	  }
	  result = result + this.offset;
	  return result;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public boolean isCondition() {
		return condition;
	}

	public void setCondition(boolean condition) {
		this.condition = condition;
	}

	public boolean isPopStackData() {
		return isPopStackData;
	}

	public void setPopStackData(boolean isPopStackData) {
		this.isPopStackData = isPopStackData;
	}
	
	
}