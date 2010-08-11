package com.ql.util.express.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;

public class ExpressTest {

	@org.junit.Test
	public void testDemo() throws Exception{
		String express = "10 * 10 + 1 + 2 * 3 + 5 * 2";
		ExpressRunner runner = new ExpressRunner();
		Object result = runner.execute(express, null, false, null);
		System.out.println("表达式计算：" + express + " = " + result);
	}

	@org.junit.Test
	public void tes10000次() throws Exception{
		ExpressRunner runner = new ExpressRunner();
		String express = "10 * 10 + 1 + 2 * 3 + 5 * 2";
		int num = 100000;
		long start = System.currentTimeMillis();
		for(int i = 0;i< num;i++){
		   runner.execute(express, null, true,null);
		}
		System.out.println("执行" + num +"次\""+ express +"\" 耗时："
				+ (System.currentTimeMillis() - start));
	}
	@SuppressWarnings("unchecked")
	@org.junit.Test
	public void testExpress() throws Exception{
		ExpressRunner runner = new ExpressRunner();
		runner.addOperator("love", new LoveOperator("love"));
		runner.addOperatorWithAlias("属于", "in", "用户$1不在允许的范围");
		runner.addOperatorWithAlias("myand", "and", "用户$1不在允许的范围");
		runner.addFunction("累加", new GroupOperator("累加"));
		runner.addFunction("group", new GroupOperator("group"));
		runner.addFunctionOfClassMethod("isVIP", BeanExample.class.getName(),
				"isVIP", new String[] { "String" }, "$1不是VIP用户");
		runner.addFunctionOfClassMethod("取绝对值", Math.class.getName(), "abs",
				new String[] { "double" }, null);
		runner.addFunctionOfClassMethod("转换为大写", BeanExample.class.getName(),
				"upper", new String[] { "String" }, null);		
		runner.addFunctionOfClassMethod("testLong", BeanExample.class.getName(),
				"testLong", new String[] { "long" }, null);		
		
		String[][] expressTest = new String[][] {
				{ "System.out.println(\"ss\")", "null" },
				{"unionName = new com.ql.util.express.test.BeanExample(\"张三\").unionName(\"李四\")",
						"张三-李四" }, 
						{ "group(2,3,4)", "9" },
						{ "取绝对值(-5.0)", "5.0" },
				{ "max(2,3,4,10)", "10" },
				{ "max(3,2) + 转换为大写(\"abc\")", "3ABC" },
				{ "c = 1000 + 2000", "3000" },
				{ "b = 累加(1,2,3)+累加(4,5,6)", "21" },
				{ "三星卖家 and 消保用户 ", "true" },
				{ "new String(\"12345\").length()" ,"5"},
				{ "'a' love 'b' love 'c' love 'd'", "d{c{b{a}b}c}d" },
				{ "10 * (10 + 1) + 2 * (3 + 5) * 2", "142" },
				{ "( 2  属于 (4,3,5)) or isVIP(\"qhlhl2010@gmail.com\") or  isVIP(\"qhlhl2010@gmail.com\")", "false" },
				{" 1!=1 and 2==2 and 1 == 2","false"},
				{" 1==1 or 2==2 and 1 == 2","true"},
				{ "abc == 1", "true" },
				{ "testLong(abc)", "toString:1" },
				};
		IExpressContext expressContext = new ExpressContextExample(null);
		expressContext.put("b", new Integer(200));
		expressContext.put("c", new Integer(300));
		expressContext.put("d", new Integer(400));
		expressContext.put("bean", new BeanExample());
		expressContext.put("abc",1l);
		
		for (int point = 0; point < expressTest.length; point++) {
			String expressStr = expressTest[point][0];
			List errorList = new ArrayList();
			 Object result =runner.execute(expressStr,errorList,true,expressContext);
			if (expressTest[point][1].equalsIgnoreCase("null")
					&& result != null
					|| result != null
					&& expressTest[point][1].equalsIgnoreCase(result							
							.toString()) == false) {
				throw new Exception("处理错误,计算结果与预期的不匹配:" + expressStr + " = " + result + "但是期望值是：" + expressTest[point][1]);
			}
			System.out.println("Example " + point + " : " + expressStr + " =  " + result);
			if(errorList.size() > 0){
			   System.out.println("\t\t系统输出的错误提示信息:" + errorList);
			}
		}
		System.out.println(expressContext);
	 }
	
	@org.junit.Test
	public void testReadme() throws Exception{		
		System.out.println("------------------------------");
		System.out.println("------------------------------");
		System.out.println("请阅读   QLExpressReadme.xml");
		System.out.println("请阅读   QLExpressReadme.xml");
		System.out.println("请阅读   QLExpressReadme.xml");
		System.out.println("请阅读   QLExpressReadme.xml");
		System.out.println("------------------------------");
		System.out.println("------------------------------");
	
	}
}
