package com.ql.util.express.instruction.op;

import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;

public  class OperatorIf extends OperatorBase {
	public OperatorIf(String aName) {
		this.name = aName;
	}

	public OperatorIf(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	
	public  OperateData executeInner(InstructionSetContext parent, OperateData[] list) throws Exception {
		if(list.length <2){
			throw new Exception("\"" + this.aliasName + "\"��������Ҫ����������");
		}
		Object obj = list[0].getObject(parent);
		if (obj == null) {
			String msg ="\"" + this.aliasName + "\"���ж���������Ϊ��";
			throw new Exception(msg);
		} else if ((obj instanceof Boolean) == false) {
			String msg = "\"" + this.aliasName + "\"���ж����� ������ Boolean,�����ǣ�";
			throw new Exception(msg + obj.getClass().getName());
		} else {
			if (((Boolean)obj).booleanValue() == true){
				return list[1];
			}else{
				if(list.length == 3){
					return list[2];
				}
			}
			return null;			
		}
	}
}
