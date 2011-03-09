package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.instruction.OperatorBase;

public class FunctionDescTest {
	@org.junit.Test
	public void testFunctionDesc() throws Exception{
		String express ="System.out.println(isVIP(\"qianghui\"))";
		ExpressRunner runner = new ExpressRunner();
		DefaultContext<String, Object>  context = new DefaultContext<String, Object>();
		runner.addFunctionOfClassMethod("isVIP", BeanExample.class.getName(),
				"isVIP", new String[] { "String" },new String[]{"�û�����"},new String[]{"UserName"},"$1����VIP�û�");
		OperatorBase op = runner.getFunciton("isVIP");
		System.out.println(op.getOperDataDesc());
		System.out.println(op.getOperDataAnnotaion());
		
		Object r = runner.execute(express,context, null, false,false);
//		Assert.assertTrue("���Բ�������", r.toString().equalsIgnoreCase("ffff"));
//		Assert.assertTrue("���Բ�������", ((BeanExample)context.get("example")).child.a.toString().equalsIgnoreCase("ssssssss"));		
	}	
}
