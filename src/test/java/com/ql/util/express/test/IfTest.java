package com.ql.util.express.test;

import java.util.HashMap;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.FuncitonCacheManager;

public class IfTest extends ExpressRunner {
	@org.junit.Test
	public void testDemo() throws Exception{
		//String express =" cache isVIP(\"qh\") ;  cache isVIP(\"xuannan\") cache isVIP(\"qh\") ;";
		
		String express =" cache new com.ql.util.express.test.BeanExample(\"张三\").unionName(\"李四\") ;" +
				"cache new com.ql.util.express.test.BeanExample(\"张三\").unionName(\"李四\") ;"
		+ " cache example.unionName(\"李四\") ;" +
				" cache example.unionName(\"李四\") ;" +
				" example.child.a = \"ssssssss\";" +
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
		System.out.println(r);
		System.out.println(context);
		System.out.println(((BeanExample)context.get("example")).child.a);		
		System.out.println(mananger.functionCallCache);
	}
}
