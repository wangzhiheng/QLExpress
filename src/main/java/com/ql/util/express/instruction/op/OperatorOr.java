package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;

public class OperatorOr extends Operator {
	public OperatorOr(String name) {
		this.name = name;
	}
	public OperatorOr(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	public Object executeInner(Object[] list) throws Exception {
		return executeInner(list[0], list[1]);
	}

	public Object executeInner(Object op1,
			Object op2) throws Exception {
		boolean result = false;
		Object o1 = op1;
		Object o2 = op2;

		if ((o1 instanceof Boolean) && (o2 instanceof Boolean)) {
			result = ((Boolean) o1).booleanValue()
						|| ((Boolean) o2).booleanValue();
		} else {
			String msg = "û�ж�������" + o1 + "��" + o2 + " �� " + this.name + "����";
			throw new Exception(msg);
		}
		return  Boolean.valueOf(result);

	}
}