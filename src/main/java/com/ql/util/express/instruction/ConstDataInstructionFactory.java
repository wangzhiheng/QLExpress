package com.ql.util.express.instruction;

import java.util.List;
import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.OperateData;
import com.ql.util.express.parse.ExpressNode;

public class ConstDataInstructionFactory extends InstructionFactory {
	public boolean createInstruction(ExpressRunner aCompile,InstructionSet result,
			Stack<ForRelBreakContinue> forStack, ExpressNode node,boolean isRoot)
			throws Exception{
		result.addInstruction(new ConstDataInstruction(node));
		return false;
	}
}

class ConstDataInstruction extends Instruction {
	OperateData operateData;

	ConstDataInstruction(ExpressNode node) {
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

	public String toString() {
		if (this.operateData instanceof OperateDataAttr) {
			return "LoadData attr:" + this.operateData.toString();
		} else {
			return "LoadData " + this.operateData.toString();
		}
	}
}
