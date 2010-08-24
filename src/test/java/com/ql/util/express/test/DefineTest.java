package com.ql.util.express.test;

import org.junit.Assert;

import com.ql.util.express.DefaultContext;

public class DefineTest {
	@org.junit.Test
	public void testDefExpressInner() throws Exception{
		String express = "def int qh = 1 + 1";
		DefaultContext<String, Object>  context = new DefaultContext<String, Object>();
		IfTest runner = new IfTest();	
		context.put("qh",100);
		Object r = runner.execute(express, null, false, context);
		Assert.assertTrue("表达式变量作用域错误", r.toString().equalsIgnoreCase("2"));
		Assert.assertTrue("表达式变量作用域错误", context.get("qh").toString().equalsIgnoreCase("100"));
	}		
	
	@org.junit.Test
	public void testDefUserContext() throws Exception{
		String express = "qh = 1 + 1";
		DefaultContext<String, Object>  context = new DefaultContext<String, Object>();
		IfTest runner = new IfTest();
		context.put("qh",100);
		Object r = runner.execute(express, null, false, context);
		Assert.assertTrue("表达式变量作用域错误", r.toString().equalsIgnoreCase("2"));
		Assert.assertTrue("表达式变量作用域错误", context.get("qh").toString().equalsIgnoreCase("2"));
	}
	@org.junit.Test
	public void testAlias() throws Exception {
		String express = " alias qh example.child ; "
				+ "{alias qh example.child.a;" + " qh =qh + \"-ssss\";" + "};"
				+ " qh.a = qh.a +\"-qh\";" + " return example.child.a";
		IfTest runner = new IfTest();
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		context.put("example", new BeanExample());
		runner.addOperatorWithAlias("如果", "if", null);
		runner.addOperatorWithAlias("则", "then", null);
		runner.addOperatorWithAlias("否则", "else", null);
		Object r = runner.execute(express, null, false, context,null, true);
		Assert.assertTrue("别名实现 错误", r.toString().equalsIgnoreCase("qh-ssss-qh"));
		System.out.println(((BeanExample) context.get("example")).child.a);
	}
}
