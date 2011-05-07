package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;

public class OperatorIn extends Operator {
	public OperatorIn(String aName) {
		this.name = aName;
	}

	public OperatorIn(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}

	public Object executeInner(Object[] list) throws Exception {
		Object obj = list[0];
		if (obj == null) {
			// 对象为空，不能执行方法
			String msg = "对象为空，不能执行方法:";
			throw new Exception(msg + this.name);
		} else if (((obj instanceof Number) || (obj instanceof String)) == false) {
			String msg = "对象类型不匹配，只有数字和字符串类型才才能执行 in 操作,当前数据类型是:";
			throw new Exception(msg + obj.getClass().getName());
		} else {
			for (int i = 1; i < list.length; i++) {
				boolean f = OperatorEqualsLessMore.executeInner("==", obj,list[i]);
				// System.out.println(obj + ":" + list[i].getObject(parent) +
				// ":" + f);
				if (f == true) {
					return Boolean.TRUE;
				}
			}
			return Boolean.FALSE;
		}
	}

}