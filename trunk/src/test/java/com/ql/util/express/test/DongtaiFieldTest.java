package com.ql.util.express.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;

public class DongtaiFieldTest {
	@Test
	public void testField() throws Exception{
		String express ="String 用户 = \"张三\";" +
				"费用.用户  = 100;" +
				"用户 = \"李四\";" +
				"费用.用户  = 200;";
		
		ExpressRunner runner = new ExpressRunner(true);
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		Map<String,Object> fee = new HashMap<String,Object>();
		context.put("费用",fee);
		runner.execute(express, context, null, false, true);
		System.out.println(context.get("费用"));
		Assert.assertTrue("动态属性错误",fee.get("张三").toString().equals("100"));
		Assert.assertTrue("动态属性错误",fee.get("李四").toString().equals("200"));
	}
	
}
