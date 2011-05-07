package com.ql.util.express.instruction.op;

import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.opdata.OperateDataLocalVar;

public class OperatorDef extends OperatorBase {
	public OperatorDef(String aName) {
		this.name = aName;
	}
	public OperatorDef(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	
	public OperateData executeInner(InstructionSetContext<String,Object> context, OperateData[] list) throws Exception {
		Class<?> tmpClass = (Class<?>) list[0].getObject(context);
		String varName = (String)list[1].getObject(context);		
		OperateDataLocalVar result = new OperateDataLocalVar(varName,tmpClass);
		context.addSymbol(varName, result);
		return result;
	}
}