package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;

/**
 * 处理 And,Or,&&,||操作
 */

public class OperatorAnd extends Operator {
	public OperatorAnd(String name) {
		this.name = name;
	}
	public OperatorAnd(String aAliasName, String aName, String aErrorInfo) {
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
						&& ((Boolean) o2).booleanValue();
		} else {
			String msg = "没有定义类型" + o1 + "和" + o2 + " 的 " + this.name + "操作";
			throw new Exception(msg);
		}
		return  Boolean.valueOf(result);

	}
}