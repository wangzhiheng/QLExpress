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

}
