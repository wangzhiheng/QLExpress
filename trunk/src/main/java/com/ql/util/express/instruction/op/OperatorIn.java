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
			// ����Ϊ�գ�����ִ�з���
			String msg = "����Ϊ�գ�����ִ�з���:";
			throw new Exception(msg + this.name);
		} else if (((obj instanceof Number) || (obj instanceof String)) == false) {
			String msg = "�������Ͳ�ƥ�䣬ֻ�����ֺ��ַ������ͲŲ���ִ�� in ����,��ǰ����������:";
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