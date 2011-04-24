package com.ql.util.express.instruction.detail;

import java.util.List;

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

	public void toJavaCode(StringBuilder staticFieldDefine,StringBuilder methodDefine,int index){
		staticFieldDefine.append("private static OperateData const_" + index + " = ")
		     .append(this.operateData.toJavaCode()+";").append("\n");
		methodDefine.append("environment.push(").append("const_" + index).append(");").append("\n");
		methodDefine.append("environment.programPointAddOne();").append("\n");
    }
	public String toString() {
		if (this.operateData instanceof OperateDataAttr) {
			return "LoadData attr:" + this.operateData.toString();
		} else {
			return "LoadData " + this.operateData.toString();
		}
	}

}