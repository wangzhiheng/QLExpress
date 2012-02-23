package com.ql.util.express.example;

import org.junit.Assert;
import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.example.operator.AddNOperator;
import com.ql.util.express.example.operator.AddTwiceOperator;

/**
 * ��������չʾ����Զ���������ͷ���
 *
 */
public class OperatorTest {

	/**
	 * ����һ���򵥵Ķ�Ԫ������
	 * @throws Exception
	 */
	@Test
	public void testAddTwice() throws Exception{
		//������ʽ���൱�� 1+(22+22)+(2+2)
		String exp = " 1 addT 22 addT 2";
		ExpressRunner runner = new ExpressRunner();
		//���������addT����ʵ��ΪAddTwiceOperator
		runner.addOperator("addT", new AddTwiceOperator());
		//ִ�б��ʽ�������������r
		int r = (Integer)runner.execute(exp,null,null,false,false);
		System.out.println(r);
		Assert.assertTrue("������ִ�д���",r==49);
	}

	/**
	 * ����һ����Ԫ������
	 * @throws Exception
	 */
	@Test
	public void testAddNByOperator() throws Exception{
		//������ʽ���൱��4+1+2+3
		String exp = "4 addN (1,2,3)";
		ExpressRunner runner = new ExpressRunner();
		//���������addN����ʵ��ΪAddNOperator���﷨��ʽ��inһ��
		runner.addOperator("addN","in",new AddNOperator());
		//ִ�б��ʽ�������������r
		int r = (Integer)runner.execute(exp,null,null,false,false);
		System.out.println(r);
		Assert.assertTrue("������ִ�д���",r==10);
	}
	
	/**
	 * ����һ������
	 * @throws Exception
	 */
	@Test
	public void testAddNByFunction() throws Exception{
		//������ʽ���൱��1+2+3+4
		String exp = "addN(1,2,3,4)";
		ExpressRunner runner = new ExpressRunner();
		//���巽��addN����ʵ��ΪAddNOperator
		runner.addFunction("addN",new AddNOperator());
		//ִ�б��ʽ�������������r
		int r = (Integer)runner.execute(exp,null,null,false,false);
		System.out.println(r);
		Assert.assertTrue("������ִ�д���",r==10);
	}
	
	/**
	 * ʹ�ò���
	 * @throws Exception
	 */
	@Test
	public void testAddTwiceWithParams() throws Exception{
		//������ʽ���൱�� i+(j+j)+(n+n)
		String exp = " i addT j addT n";
		IExpressContext<String,Object> expressContext = new DefaultContext<String,Object>();
		expressContext.put("i", Integer.valueOf(1));
		expressContext.put("j", Integer.valueOf(22));
		expressContext.put("n", Integer.valueOf(2));
		ExpressRunner runner = new ExpressRunner();
		//���������addT����ʵ��ΪAddTwiceOperator
		runner.addOperator("addT", new AddTwiceOperator());
		//ִ�б��ʽ�������������r
		int r = (Integer)runner.execute(exp,expressContext,null,false,false);
		System.out.println(r);
		Assert.assertTrue("������ִ�д���",r==49);
	}
}
