package com.ql.util.express.instruction.op;

import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.opdata.OperateDataArrayItem;

public class OperatorArray extends OperatorBase {
	public OperatorArray(String aName) {
		this.name = aName;
	}
	public OperatorArray(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}

	public OperateData executeInner(InstructionSetContext<String,Object> context, OperateData[] list) throws Exception {
		if(list[0] == null || list[0].getObject(context) == null){
			throw new Exception("����Ϊnull,����ִ��������ز���");
		}
		Object tmpObject = list[0].getObject(context);
	    if( tmpObject.getClass().isArray() == false){
			throw new Exception("����:"+ tmpObject.getClass() +"��������,����ִ����ز���" );
		}
	    int index = ((Number)list[1].getObject(context)).intValue();		
	    OperateData result  = new OperateDataArrayItem((OperateData)list[0],index);
		return result;
	}
}
