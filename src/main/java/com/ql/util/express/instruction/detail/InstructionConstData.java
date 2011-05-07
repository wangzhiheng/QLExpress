package com.ql.util.express.instruction.detail;

import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.ql.util.express.AsmUtil;
import com.ql.util.express.OperateData;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.instruction.opdata.OperateDataAttr;

public class InstructionConstData extends Instruction {
	OperateData operateData;

	public InstructionConstData(OperateData data) {
       this.operateData = data;
	}
    public OperateData getOperateData(){
    	return this.operateData;
    }
	public void execute(RunEnvironment environment, List<String> errorList)
			throws Exception {
		if (environment.isTrace()) {
			if (this.operateData instanceof OperateDataAttr) {
				log.debug(this + ":"
						+ this.operateData.getObject(environment.getContext()));
			} else {
				log.debug(this);
			}
		}
		environment.push(this.operateData);
		environment.programPointAddOne();
	}

	public void toJavaCodeNew(Type classType,ClassWriter cw,GeneratorAdapter staticInitialMethod,GeneratorAdapter executeMethod,int index, Map<Integer,Label>  lables){
		Class<?> realDataClass = this.operateData.getClass();
		String constFieldName = "const_" + index;
		//���徲̬����
		FieldVisitor fv = cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC,
				constFieldName,AsmUtil.getInnerClassDesc(realDataClass),null,null);  
        fv.visitEnd();   
        
        //���徲̬�����ĳ�ʼ��
        AsmUtil.transferOperatorData(staticInitialMethod,this.operateData);
        staticInitialMethod.putStatic(classType,constFieldName, Type.getType(realDataClass));

	    //���������ڴ���
        //executeMethod.loadArg(0);   
        executeMethod.getStatic(classType, constFieldName, Type.getType(realDataClass));
        //executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),Method.getMethod("void push(" + OperateData.class.getName() + ")"));
    }
	public void toJavaCode(Type classType,ClassWriter cw,GeneratorAdapter staticInitialMethod,GeneratorAdapter executeMethod,int index, Map<Integer,Label>  lables){
		Class<?> realDataClass = this.operateData.getClass();
		String constFieldName = "const_" + index;
		//���徲̬����
		FieldVisitor fv = cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC,
				constFieldName,AsmUtil.getInnerClassDesc(realDataClass),null,null);  
        fv.visitEnd();   
        
        //���徲̬�����ĳ�ʼ��
        AsmUtil.transferOperatorData(staticInitialMethod,this.operateData);
        staticInitialMethod.putStatic(classType,constFieldName, Type.getType(realDataClass));

	    //���������ڴ���
        executeMethod.loadArg(0);   
        executeMethod.getStatic(classType, constFieldName, Type.getType(realDataClass));
        executeMethod.invokeVirtual(Type.getType(RunEnvironment.class),Method.getMethod("void push(" + OperateData.class.getName() + ")"));
    }	
	
	public String toString() {
		if (this.operateData instanceof OperateDataAttr) {
			return "LoadData attr:" + this.operateData.toString();
		} else {
			return "LoadData " + this.operateData.toString();
		}
	}

}