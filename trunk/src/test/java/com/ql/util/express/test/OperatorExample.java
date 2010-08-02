package com.ql.util.express.test;


import com.ql.util.express.IExpressContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.Operator;
import com.ql.util.express.OperatorOfNumber;


class GroupOperator extends Operator {
	public GroupOperator(String aName) {
		this.name= aName;
	}
	public Object executeInner(Object[] list)throws Exception {
		Object result = new Integer(0);
		for (int i = 0; i < list.length; i++) {
			result = OperatorOfNumber.Add.execute(result, list[i]);
		}
		return result;
	}
}

class LoveOperator extends Operator {	
	public LoveOperator(String aName) {
		this.name= aName;
	}
	public Object executeInner(Object[] list)
			throws Exception {
		String op1 = list[0].toString();
		String op2 = list[1].toString();
		String result = op2 +"{" + op1 + "}" + op2;		
		return result;
	}
}