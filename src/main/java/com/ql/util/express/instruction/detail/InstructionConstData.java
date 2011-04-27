package com.ql.util.express.instruction.detail;

import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.ql.util.express.AsmUtil;
import com.ql.util.express.OperateData;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.instruction.opdata.OperateClass;
import com.ql.util.express.instruction.opdata.OperateDataAttr;
import com.ql.util.express.parse.ExpressNode;

public class InstructionConstData extends Instruction {
	OperateData operateData;

	public InstructionConstData(ExpressNode node) {
		if(node.isTypeEqualsOrChild("CONST_CLASS")){
			this.operateData = new OperateClass(node.getValue(),(Class<?>)node.getObjectValue());
		}else{
			this.operateData = new OperateData(node.getObjectValue(), node
				.getObjectValue().getClass());
		}
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

	public void toJavaCode(Type classType,ClassWriter cw,GeneratorAdapter staticInitialMethod,GeneratorAdapter executeMethod,int index){
		String constFieldName = "const_" + index;
		//定义静态变量
		FieldVisitor fv = cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC,
				constFieldName,AsmUtil.getInnerClassDesc(OperateData.class),null,null);  
        fv.visitEnd();   
        
        //定义静态变量的初始化

        staticInitialMethod.newInstance(Type.getType(OperateData.class));
        staticInitialMethod.dup();
        //staticInitialMethod.visitLdcInsn(this.operateData.getObjectInner(null).toString());
        AsmUtil.transferCode(staticInitialMethod,this.operateData.getObjectInner(null));
        AsmUtil.transferCode(staticInitialMethod,this.operateData.type);
	    staticInitialMethod.invokeConstructor(Type.getType(OperateData.class), Method.getMethod("void <init>(Object,Class)"));
	    staticInitialMethod.putStatic(classType,constFieldName, Type.getType(OperateData.class));
	        //定义运行期代码
        executeMethod.loadArg(0);   
        executeMethod.getStatic(classType, constFieldName, Type.getType(OperateData.class));
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