package com.ql.util.express.test;

import org.junit.Assert;
import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.ExpressUtil;

public class IsAssignableTest {
	@Test
	public void testABC() throws Exception {
		Assert.assertTrue("��������ת���жϴ���",ExpressUtil.isAssignable(boolean.class, Boolean.class) == true);
		Assert.assertTrue("��������ת���жϴ���",ExpressUtil.isAssignable(char.class, java.lang.Character.class) == true);
		Assert.assertTrue("��������ת���жϴ���",ExpressUtil.isAssignable(long.class, int.class) == true);		
		Assert.assertTrue("��������ת���жϴ���",ExpressUtil.isAssignable(Long.class, int.class) == true);
		Assert.assertTrue("��������ת���жϴ���",ExpressUtil.isAssignable(Long.class, Integer.class) == true);
		Assert.assertTrue("��������ת���жϴ���",ExpressUtil.isAssignable(java.util.List.class,java.util.AbstractList.class) == true);
		Assert.assertTrue("��������ת���жϴ���",ExpressUtil.isAssignable(java.util.List.class,java.util.AbstractList.class) == ExpressUtil.isAssignableOld(java.util.List.class,java.util.AbstractList.class));
		Assert.assertTrue("��������ת���жϴ���",ExpressUtil.isAssignable(long.class, int.class) == ExpressUtil.isAssignableOld(long.class, int.class));

		int index = ExpressUtil.findMostSpecificSignature(new Class[]{Integer.class},
				new Class[][]{{Integer.class},{int.class}});
   
		System.out.println(index);
		
		String express = "bean.testInt(p)";
		ExpressRunner runner = new ExpressRunner(true);
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		context.put("bean",new BeanExample());
		context.put("p",100);
		
		Object r = runner.execute(express, context, null, false, true);
		System.out.println(r);
		Assert.assertTrue("��������ת������",r.toString().equalsIgnoreCase("toString-int:100"));
		
		context = new DefaultContext<String, Object>();
		express = "bean.testLong(p)";
		context.put("bean",new BeanExample());
		context.put("p",100L);
		r = runner.execute(express, context, null, false, true);
		Assert.assertTrue("��������ת������",r.toString().equalsIgnoreCase("toString-long:100"));

	}
}
