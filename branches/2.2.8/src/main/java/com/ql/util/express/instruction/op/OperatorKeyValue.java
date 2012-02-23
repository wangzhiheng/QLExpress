package com.ql.util.express.instruction.op;

import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.opdata.OperateDataKeyValue;

public class OperatorKeyValue extends OperatorBase {
	
	public OperatorKeyValue(String aName) {
		this.name = aName;
	}
	public OperatorKeyValue(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}

	public OperateData executeInner(InstructionSetContext<String,Object> context, OperateData[] list) throws Exception {
		 return  new OperateDataKeyValue(list[0],list[1]);
	}
}
