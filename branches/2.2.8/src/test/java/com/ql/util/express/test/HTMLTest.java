package com.ql.util.express.test;

import org.junit.Assert;
import org.junit.Test;

import com.ql.util.express.ExpressRunner;

public class HTMLTest {
	@Test
	public void testABC() throws Exception {
		//String express ="\"<div style=\\\"font-family:����;font-size:12px;line-height:25px;\\\">�������루\"";
		ExpressRunner runner = new ExpressRunner(false,true);
		String express ="\"��\\\"����\\\"��\\\"aaa-\" + 100";
		Object r = runner.execute(express, null, null, false, true);
		System.out.println(r);
		System.out.println("��\"����\"��\"aaa-100");
		Assert.assertTrue("�ַ�����������",r.equals("��\"����\"��\"aaa-100"));		
	}
}
