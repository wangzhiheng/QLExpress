package com.ql.util.express.example.operator;

import com.ql.util.express.Operator;

public class ApproveOperator extends Operator{
	int operater = 0;
	public ApproveOperator(int op){
		this.operater = op;
	}

	public Object executeInner(Object[] list) throws Exception {
		if(this.operater == 1){
		  System.out.println(list[0] + "����:���:" + list[1]);
		  if(((Integer)list[1]) > 6000)
		  	return false;
		}
		else if(this.operater == 2)
			System.out.println("�����뿨:���:"+list[0]);
		else
			System.out.println("����:������:"+list[0]);
		return true;
	}

}
