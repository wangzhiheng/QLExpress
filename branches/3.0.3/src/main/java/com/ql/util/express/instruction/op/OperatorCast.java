package com.ql.util.express.instruction.op;

import com.ql.util.express.ExpressUtil;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;

public class OperatorCast extends OperatorBase {
	public OperatorCast(String aName) {
		this.name = aName;
	}

	public OperateData executeInner(InstructionSetContext<String,Object> parent, OperateData[] list) throws Exception {
		Class<?> tmpClass = (Class<?>) list[0].getObject(parent);
		Object castObj = ExpressUtil.castObject(list[1].getObject(parent), tmpClass,true);
		OperateData result = new OperateData(castObj,tmpClass);
		return result;
	}
}