package com.ql.util.express.instruction;

import java.util.List;

import com.ql.util.express.InstructionSet;
import com.ql.util.express.OperateData;


public class InstructionCallMacro extends Instruction{
		String name;
		InstructionCallMacro(String aName){
			this.name = aName;
	    }
		
		public void execute(RunEnvironment environment,List<String> errorList)throws Exception{
			if(environment.isTrace()){
				log.debug(this);
			}
			Object functionSet = environment.getContext().getSymbol(this.name);
			
			Object result =InstructionSet.execute(new InstructionSet[]{(InstructionSet)functionSet},environment.getContext().getExpressLoader(),
					environment.getContext(), errorList, environment.getContext().getFunctionCachManagerNoCreate(),
					environment.isTrace(),false,false,this.log);
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
	}