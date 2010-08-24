package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;

public class IfTest extends ExpressRunner {
	@org.junit.Test
	public void testDemo() throws Exception{
//		String express =" def int a=0; if false then a = 5 else  a=10+1 ; return a ";
//		String express =" def int a=100;  return a ";
//		String express =
//				" qh = 1; " +
//				"如果 ( 如果 true 则 false 否则 true)  则 {" +
//				"  3 + {3} + {4 + 1}" +
//				" }否则{" +
//				" qh = 3;" +
//				" qh = qh + 100;" +
//				"}; " +
//				"qh = qh + 1;" +
//				"return qh;";
		String express =" alias qh example.child ; " +
				"{alias qh example.child.a;" +
				" qh =qh + \"-ssss\";" +
				"};" +
				" qh.a = qh.a +\"-qh\";" +
				" return example.child.a" ;
		IfTest runner = new IfTest();
		DefaultContext<String, Object>  context = new DefaultContext<String, Object>();
		context.put("example", new BeanExample());
		runner.addOperatorWithAlias("如果", "if",null);
		runner.addOperatorWithAlias("则", "then",null);
		runner.addOperatorWithAlias("否则", "else",null);
		Object r = runner.execute(express, null, false, context,true);
		System.out.println(r);
		System.out.println(context);
		System.out.println(((BeanExample)context.get("example")).child.a);		
	}
}
