package com.ql.util.express.test;

import java.util.HashMap;

import org.junit.Assert;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.FuncitonCacheManager;
import com.ql.util.express.InstructionSet;

public class DefineTest {
	@org.junit.Test
	public void testDefExpressInner() throws Exception{
		String express = "定义变量 int qh = 1 + 1";
		DefaultContext<String, Object>  context = new DefaultContext<String, Object>();
		IfTest runner = new IfTest();	
		runner.addOperatorWithAlias("定义变量", "def", null);
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
		String express = " 定义别名 qh example.child ; "
				+ "{定义别名 qh example.child.a;" + " qh =qh + \"-ssss\";" + "};"
				+ " qh.a = qh.a +\"-qh\";" + " return example.child.a";
		IfTest runner = new IfTest();
		runner.addOperatorWithAlias("定义别名", "alias", null);
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		context.put("example", new BeanExample());
		runner.addOperatorWithAlias("如果", "if", null);
		runner.addOperatorWithAlias("则", "then", null);
		runner.addOperatorWithAlias("否则", "else", null);
		Object r = runner.execute(express, null, false, context,null, true);
		Assert.assertTrue("别名实现 错误", r.toString().equalsIgnoreCase("qh-ssss-qh"));
		Assert.assertTrue("别名实现 错误", ((BeanExample) context.get("example")).child.a.toString().equalsIgnoreCase("qh-ssss-qh"));
	}
	@org.junit.Test
	public void testMacro() throws Exception{
		String express ="定义宏  惩罚   {cache bean.unionName(name)}; 惩罚; return  惩罚";
		IfTest runner = new IfTest();
		runner.addOperatorWithAlias("定义宏", "macro", null);
		DefaultContext<String, Object>  context = new DefaultContext<String, Object>();		
		context.put("bean", new BeanExample("qhlhl2010@gmail.com"));
		context.put("name","xuannn");
		Object r = runner.execute(express, null, false, context,null,true);
		Assert.assertTrue("别名宏 错误", r.toString().equalsIgnoreCase("qhlhl2010@gmail.com-xuannn"));
		System.out.println(r);
	}	
	@org.junit.Test
	public void testProperty() throws Exception{
		//String express =" cache isVIP(\"qh\") ;  cache isVIP(\"xuannan\") cache isVIP(\"qh\") ;";
		
		String express =" example.child.a = \"ssssssss\";" +
				" map.name =\"ffff\";" +
				"return map.name;";
		IfTest runner = new IfTest();
		DefaultContext<String, Object>  context = new DefaultContext<String, Object>();
		context.put("example", new BeanExample("张三"));
		context.put("map",new HashMap());
		runner.addFunctionOfClassMethod("isVIP", BeanExample.class.getName(),
				"isVIP", new String[] { "String" }, "$1不是VIP用户");
		FuncitonCacheManager mananger = new FuncitonCacheManager();
		Object r = runner.execute(express, null, false, context,mananger,true);
		Assert.assertTrue("属性操作错误", r.toString().equalsIgnoreCase("ffff"));
		Assert.assertTrue("属性操作错误", ((BeanExample)context.get("example")).child.a.toString().equalsIgnoreCase("ssssssss"));		
	}	
	@org.junit.Test
	public void test批量执行指令() throws Exception{
		IfTest runner = new IfTest();
		runner.addOperatorWithAlias("定义宏", "macro", null);
		DefaultContext<String, Object>  context = new DefaultContext<String, Object>();		
		context.put("bean", new BeanExample("qhlhl2010@gmail.com"));
		context.put("name","xuannn");
		String[] sets =  new String[]{
				"def int qh = 1;",	
				"qh = qh + 10;",
				"定义宏  惩罚   {qh = qh + 100 };",
				"惩罚;",
				"qh = qh + 1000;"
		};
		Object r = runner.execute(sets, null, true, context, null, true);
		Assert.assertTrue("别名实现 错误", r.toString().equalsIgnoreCase("1111"));
//		System.out.println(r);
//		System.out.println(context);
	}	
}
