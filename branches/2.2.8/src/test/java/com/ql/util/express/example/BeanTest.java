package com.ql.util.express.example;

import org.junit.Assert;
import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;

public class BeanTest {

	@Test
	public void test1() throws Exception{
		String exp = "import com.ql.util.express.example.CustBean;" + 
		        "CustBean cust = new CustBean(1);" +
		        "cust.setName(\"Сǿ\");" +
		        "return cust.getName();";
		ExpressRunner runner = new ExpressRunner();
		//ִ�б��ʽ�������������r
		String r = (String)runner.execute(exp,null,null,false,false);
		System.out.println(r);
		Assert.assertTrue("������ִ�д���","Сǿ".equals(r));
	}
	
	@Test
	public void test2() throws Exception{
		String exp = "cust.setName(\"Сǿ\");" +
			     // "cust.name = \"Сǿ\";" + 
		        "return cust.getName();";
		IExpressContext<String,Object> expressContext = new DefaultContext<String,Object>();
		expressContext.put("cust", new CustBean(1));
		ExpressRunner runner = new ExpressRunner();
		//ִ�б��ʽ�������������r
		String r = (String)runner.execute(exp,expressContext,null,false,false);
		System.out.println(r);
		Assert.assertTrue("������ִ�д���","Сǿ".equals(r));
	}
	
	@Test
	public void test3() throws Exception{
		String exp = "����ĸ��д(\"abcd\")";
		ExpressRunner runner = new ExpressRunner();
		runner.addFunctionOfClassMethod("����ĸ��д", CustBean.class.getName(), "firstToUpper", new String[]{"String"},null);
		//ִ�б��ʽ�������������r
		String r = (String)runner.execute(exp,null,null,false,false);
		System.out.println(r);
		Assert.assertTrue("������ִ�д���","Abcd".equals(r));
	}
	
	/**
	 * ʹ�ñ���
	 * @throws Exception
	 */
	@Test
	public void testAlias() throws Exception{
		String exp = "cust.setName(\"Сǿ\");" +
			      "������� custName cust.name;" + 
		        "return custName;";
		IExpressContext<String,Object> expressContext = new DefaultContext<String,Object>();
		expressContext.put("cust", new CustBean(1));
		ExpressRunner runner = new ExpressRunner();
		//
		runner.addOperatorWithAlias("�������", "alias", null);
		//ִ�б��ʽ�������������r
		String r = (String)runner.execute(exp,expressContext,null,false,false);
		System.out.println(r);
		Assert.assertTrue("������ִ�д���","Сǿ".equals(r));
	}
	
	/**
	 * ʹ�ú�
	 * @throws Exception
	 */
	@Test
	public void testMacro() throws Exception{
		String exp = "cust.setName(\"Сǿ\");" +
			      "����� custName {cust.name};" + 
		        "return custName;";
		IExpressContext<String,Object> expressContext = new DefaultContext<String,Object>();
		expressContext.put("cust", new CustBean(1));
		ExpressRunner runner = new ExpressRunner();
		//
		runner.addOperatorWithAlias("�����", "macro", null);
		//ִ�б��ʽ�������������r
		String r = (String)runner.execute(exp,expressContext,null,false,false);
		System.out.println(r);
		Assert.assertTrue("������ִ�д���","Сǿ".equals(r));
	}
}
