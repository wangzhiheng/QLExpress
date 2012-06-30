package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;

public class OperatorPrint extends Operator {
	public OperatorPrint(String name) {
		this.name = name;
	}
	public OperatorPrint(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	public Object executeInner(Object[] list) throws Exception {
		if (list.length != 1 ){
			throw new Exception("�������쳣,����ֻ����һ��������");
		}
        System.out.print(list[0]);
        return null;
	}


}