package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;

public class OperatorPrintln extends Operator {
	public OperatorPrintln(String name) {
		this.name = name;
	}
	public OperatorPrintln(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	public Object executeInner(Object[] list) throws Exception {
		if (list.length != 1 ){
			throw new Exception("�������쳣,����ֻ����һ��������");
		}
        System.out.println(list[0]);
        return null;
	}


}