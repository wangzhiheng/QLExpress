package com.ql.util.express.instruction.detail;

import java.util.List;

import com.ql.util.express.OperateData;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.instruction.opdata.OperateDataAttr;

public class InstructionOperator extends Instruction{
	OperatorBase operator;
	int opDataNumber;
	public InstructionOperator(OperatorBase aOperator,int aOpDataNumber){
	  this.operator = aOperator;
	  this.opDataNumber =aOpDataNumber;
	}
	public void execute(RunEnvironment environment,List<String> errorList) throws Exception{		
		OperateData[] parameters = environment.popArray(environment.getContext(),this.opDataNumber);		
		if(environment.isTrace()){
			String str = this.operator.toString() + "(";
			for(int i=0;i<parameters.length;i++){
				if(i > 0){
					str = str + ",";
				}
				if(parameters[i] instanceof OperateDataAttr){
					str = str + parameters[i] + ":" + parameters[i].getObject(environment.getContext());
				}else{
				   str = str + parameters[i];
				}
			}
			str = str + ")";
			log.debug(str);
		}
		
		OperateData result = this.operator.execute(environment.getContext(),parameters, errorList);
		environment.push(result);
		environment.programPointAddOne();
	}
	public void toJavaCode(StringBuilder staticFieldDefine,StringBuilder methodDefine,int index){
		methodDefine.append("{").append("\n");
		methodDefine.append("  ").append("OperateData[] parameters = environment.popArray(environment.getContext(),"+ this.opDataNumber +");").append("\n");
		methodDefine.append("  ").append("OperatorBase tempOp =   environment.getContext().getExpressRunner().getOperatorFactory().getOperator(\""+ this.operator.getAliasName() +"\");").append("\n");
		methodDefine.append("  ").append("OperateData result = tempOp.execute(environment.getContext(),parameters, errorList);").append("\n");
		methodDefine.append("  ").append("environment.push(result);").append("\n");
		methodDefine.append("  ").append("environment.programPointAddOne();").append("\n");
		methodDefine.append("}").append("\n");
    }
	public String toString(){
		String result = "OP : " + this.operator.toString() +  " OPNUMBER[" + this.opDataNumber +"]";
		return result;
	}
}	