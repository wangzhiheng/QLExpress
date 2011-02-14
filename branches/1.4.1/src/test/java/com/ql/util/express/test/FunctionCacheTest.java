package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.FuncitonCacheManager;

public class FunctionCacheTest {
	@org.junit.Test
	public void testFunctionCallCache() throws Exception {
		String express = " cache isVIP(\"qh\") ;  cache isVIP(\"xuannan\"); cache isVIP(\"qh\") ;"
				+ " cache example.unionName(\"����\") ;"
				+ " return cache example.unionName(\"����\") ;";
		ExpressRunner runner = new ExpressRunner();
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		context.put("example", new BeanExample("����"));
		runner.addFunctionOfClassMethod("isVIP", BeanExample.class.getName(),
				"isVIP", new String[] { "String" }, "$1����VIP�û�");
		FuncitonCacheManager mananger = new FuncitonCacheManager();
		Object r = runner.execute(express,context, null, false,false);
		System.out.println(r);
		System.out.println(context);
		System.out.println(((BeanExample) context.get("example")).child.a);
		System.out.println(mananger.functionCallCache);
	}
}
