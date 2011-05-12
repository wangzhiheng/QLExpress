package com.ql.util.express.instruction.op;

import java.lang.reflect.Array;

import com.ql.util.express.ExpressUtil;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;

public class OperatorAnonymousNewArray extends OperatorBase {
	public OperatorAnonymousNewArray(String aName) {
		this.name = aName;
	}
	public OperatorAnonymousNewArray(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}

	public OperateData executeInner(InstructionSetContext<String,Object> context, OperateData[] list) throws Exception {
		Class<?> type = Object.class;
		if(list.length >0){
		  type = list[0].getType(context);
		}
		type = ExpressUtil.getSimpleDataType(type);
		int[] dims = new int[1];
		dims[0]= list.length;
		Object data = Array.newInstance(type,dims);
		for(int i=0;i<list.length;i++){
			Array.set(data, i, list[i].getObject(context));
		}
		return new OperateData(data,data.getClass());
	}
}
