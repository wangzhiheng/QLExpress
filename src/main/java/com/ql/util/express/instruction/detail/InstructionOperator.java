package com.ql.util.express.instruction.detail;

import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.instruction.op.OperatorFactory;
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
	public void toJavaCode(Type classType,ClassWriter cw,GeneratorAdapter staticInitialMethod,GeneratorAdapter executeMethod,int index){
		//OperateData[] parameters = environment.popArray(environment.getContext(),this.opDataNumber);
		executeMethod.loadArg(0);
		executeMethod.loadArg(0);
		executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),Method.getMethod(InstructionSetContext.class.getName() + "  getContext()"));
		executeMethod.visitLdcInsn(this.opDataNumber);
		executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),Method.getMethod(OperateData.class.getName() + "[]  popArray(" + InstructionSetContext.class.getName()+"," + int.class +")"));
		executeMethod.storeLocal(3,Type.getType(OperateData[].class));
		//OperatorBase tempOp =   environment.getContext().getExpressRunner().getOperatorFactory().getOperator("+");
		executeMethod.loadArg(0);
		executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),Method.getMethod(InstructionSetContext.class.getName() + "  getContext()"));
		executeMethod.invokeVirtual(Type.getType(InstructionSetContext.class),Method.getMethod(ExpressRunner.class.getName() + "  getExpressRunner()"));
		executeMethod.invokeVirtual(Type.getType(ExpressRunner.class),Method.getMethod(OperatorFactory.class.getName() + "  getOperatorFactory()"));
		executeMethod.visitLdcInsn(this.operator.getAliasName());
		executeMethod.invokeVirtual(Type.getType(OperatorFactory.class),Method.getMethod(OperatorBase.class.getName() + "  getOperator(String)"));
		
		executeMethod.storeLocal(4,Type.getType(OperatorBase.class));
		executeMethod.loadLocal(4);
		
		executeMethod.loadArg(0);
		executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),Method.getMethod(InstructionSetContext.class.getName() + "  getContext()"));
		executeMethod.loadLocal(3);
		executeMethod.loadArg(2);
		executeMethod.invokeVirtual(Type.getType(OperatorBase.class),Method.getMethod(OperateData.class.getName() + "  execute(" 
				  + InstructionSetContext.class.getName() +"," + OperateData.class.getName() + "[]," + List.class.getName()  + ")"));
		executeMethod.storeLocal(5,Type.getType(OperateData.class));

		
		//environment.push(result);
		executeMethod.loadArg(0);   
		executeMethod.loadLocal(5);
        executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),Method.getMethod("void push(" + OperateData.class.getName() + ")"));
    }
	public String toString(){
		String result = "OP : " + this.operator.toString() +  " OPNUMBER[" + this.opDataNumber +"]";
		return result;
	}
}	