package com.ql.util.express.instruction.detail;

import java.util.List;
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
		public void toJavaCode(StringBuilder staticFieldDefine,StringBuilder methodDefine,int index){
			throw new RuntimeException("��û��ʵ�ַ�����toJavaCode" );
		}
	}