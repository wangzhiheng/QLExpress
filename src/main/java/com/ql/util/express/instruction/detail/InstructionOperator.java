package com.ql.util.express.instruction.detail;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.ql.util.express.InstructionSet;
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
	public OperatorBase getOperator(){
		return this.operator;
	}
	public void execute(RunEnvironment environment,List<String> errorList) throws Exception{
		execute(this.operator,this.opDataNumber, environment, errorList, this.log);
	}
	public static void execute(OperatorBase aOperator,int aOpNum,RunEnvironment environment,List<String> errorList,Log log) throws Exception{		
		OperateData[] parameters = environment.popArray(environment.getContext(),aOpNum);		
		if(environment.isTrace()){
			String str = aOperator.toString() + "(";
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
		
		OperateData result = aOperator.execute(environment.getContext(),parameters, errorList);
		environment.push(result);
		environment.programPointAddOne();
	}
	public void toJavaCode(Type classType,ClassWriter cw,GeneratorAdapter staticInitialMethod,GeneratorAdapter executeMethod,int index, Map<Integer,Label>  lables){
		toJavaCode(this.operator.getAliasName(),this.opDataNumber, classType, cw, staticInitialMethod, executeMethod, index);
	}	
	public static void toJavaCodeNew(String opAliasName,int opNumber,Type classType,ClassWriter cw,GeneratorAdapter staticInitialMethod,GeneratorAdapter executeMethod,int index){
		//paramRunEnvironment.push(localOperatorFactory.getOperator("+")
		//  .execute(localInstructionSetContext, paramRunEnvironment.popArray(localInstructionSetContext, 2),
		//   paramList));
		executeMethod.visitLdcInsn(opNumber);
		executeMethod.newArray(Type.getType(OperateData.class));
		executeMethod.storeLocal(5, Type.getType(OperateData[].class));
		
		for(int i=6 + opNumber - 1 ;i>=6 ;i--){
			executeMethod.storeLocal(i, Type.getType(OperateData.class));
		}
		for(int i=0;i< opNumber;i++){
			executeMethod.loadLocal(5);
			executeMethod.visitLdcInsn(i);
			executeMethod.loadLocal(6+i);
			executeMethod.arrayStore(Type.getType(OperateData.class));
		}
		
		//executeMethod.loadArg(0);   
		executeMethod.loadLocal(4);
		executeMethod.visitLdcInsn(opAliasName);
		executeMethod.invokeVirtual(Type.getType(OperatorFactory.class),
				Method.getMethod(OperatorBase.class.getName() + "  getOperator(String)"));
		executeMethod.loadLocal(3);		
		executeMethod.loadLocal(5);
		executeMethod.loadArg(1);
		executeMethod.invokeVirtual(Type.getType(OperatorBase.class),
				Method.getMethod(OperateData.class.getName() + "  execute(" 
				  + InstructionSetContext.class.getName() +"," + OperateData.class.getName() + "[]," + List.class.getName()  + ")"));
//        executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),
//        		Method.getMethod("void push(" + OperateData.class.getName() + ")"));
    }	
	public static void toJavaCode(String opAliasName,int opNumber,Type classType,ClassWriter cw,GeneratorAdapter staticInitialMethod,GeneratorAdapter executeMethod,int index){
		//paramRunEnvironment.push(localOperatorFactory.getOperator("+")
		//  .execute(localInstructionSetContext, paramRunEnvironment.popArray(localInstructionSetContext, 2),
		//   paramList));
		executeMethod.loadArg(0);   
		executeMethod.loadLocal(4);
		executeMethod.visitLdcInsn(opAliasName);
		executeMethod.invokeVirtual(Type.getType(OperatorFactory.class),
				Method.getMethod(OperatorBase.class.getName() + "  getOperator(String)"));
		executeMethod.loadLocal(3);		
		executeMethod.loadArg(0);
		executeMethod.loadLocal(3);
		executeMethod.visitLdcInsn(opNumber);
		executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),
				Method.getMethod(OperateData.class.getName() + "[]  popArray(" + InstructionSetContext.class.getName()+"," + int.class +")"));	
		executeMethod.loadArg(1);
		executeMethod.invokeVirtual(Type.getType(OperatorBase.class),
				Method.getMethod(OperateData.class.getName() + "  execute(" 
				  + InstructionSetContext.class.getName() +"," + OperateData.class.getName() + "[]," + List.class.getName()  + ")"));
        executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),
        		Method.getMethod("void push(" + OperateData.class.getName() + ")"));
    }
	public String toString(){
		String result = "OP : " + this.operator.toString() +  " OPNUMBER[" + this.opDataNumber +"]";
		return result;
	}
}	