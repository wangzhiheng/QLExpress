package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;

public class IfTest extends ExpressRunner {
	@org.junit.Test
	public void testDemo() throws Exception{
		String express ="def int defVar = 100; defVar = defVar + 100;";
//		String express =
//				" qh = 1; " +
//				"如果 ( 如果 true 则 false 否则 true)  则 {" +
//				"  3 + {3} + {4 + 1}" +
//				" }否则{" +
//				" qh = 3;" +
//				" qh = qh + 100;" +
//				"}; " +
//				"qh = qh + 1;";
		IfTest runner = new IfTest();
		DefaultContext<String, Object>  context = new DefaultContext<String, Object>();
		context.put("defVar",1000);
		runner.addOperatorWithAlias("如果", "if",null);
		runner.addOperatorWithAlias("则", "then",null);
		runner.addOperatorWithAlias("否则", "else",null);
		Object r = runner.execute(express, null, false, context,true);
		System.out.println(r);
		System.out.println(context);
		
	}
}
