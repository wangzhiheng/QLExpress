package com.ql.util.express.test;

import com.ql.util.express.ExpressRunner;

public class IfTest extends ExpressRunner {
	@org.junit.Test
	public void testDemo() throws Exception{
		String express = " 3+ (如果 1==2 则 4 否则 3) +8";
		//String express = " true and false ";
		IfTest runner = new IfTest();
		runner.addOperatorWithAlias("如果", "if",null);
		runner.addOperatorWithAlias("则", "then",null);
		runner.addOperatorWithAlias("否则", "else",null);
		this.setTrace(true); 
		Object r = runner.execute(express, null, false, null);
		System.out.println(r);
		
	}
}
