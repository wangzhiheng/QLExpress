package com.ql.util.express.example;

import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.example.operator.ApproveOperator;

/**
 * ����ģ����һ���򵥵����̴���
 * ����չʾ��ζ�����ʽ����������ʹ�������ı���
 *
 */
public class WorkflowTest {

	/**
	 * ִ��һ���ı�
	 * @throws Exception
	 */
	@Test
	public void testApprove1()throws Exception{
		//������ʽ
		String exp = "��� (����ͨ��(����,���)){" +
				         "   ���  (���  ���� 5000){ " +
				         "     ���  (����ͨ��(�ܼ�,���)){" +
				         "        ���  (����ͨ��(����,���)){" +
				         "           ��������(���)" +
				         "        }����  {" +
				         "            ����޸�(������)" +
				         "        }" +
				         "     }���� {" +
				         "        ����޸�(������)" +
				         "     }" +
				         "   }����  {" +
				         "      ���  (����ͨ��(����,���)){" +
				         "        ��������(���)" +
				         "      }���� {" +
				         "         ����޸�(������)" +
				         "      }" +
				         "   }" +
				         "}���� {" +
				         "   ����޸�(������)" +
				         "}" +
				         "��ӡ(\"���\")";
    ExpressRunner runner = new ExpressRunner();
		//�������������
		runner.addOperatorWithAlias("���", "if",null);
		runner.addOperatorWithAlias("����", "else",null);
		runner.addOperatorWithAlias("����", ">",null);
		//
		runner.addFunctionOfServiceMethod("��ӡ", System.out, "println",new String[] { "String" }, null);
		//���巽��
		runner.addFunction("����ͨ��", new ApproveOperator(1));
		runner.addFunction("��������", new ApproveOperator(2));
		runner.addFunction("����޸�", new ApproveOperator(3));
		//���������ı���
		IExpressContext<String,Object> expressContext = new DefaultContext<String,Object>();
		expressContext.put("����", "������");
		expressContext.put("�ܼ�", "���ܼ�");
		expressContext.put("����", "�Ų���");
		expressContext.put("������", "Сǿ");
		expressContext.put("���", new Integer(4000));
		//ִ�б��ʽ
		runner.execute(exp, expressContext, null,false, false);
	}

	/**
	 * ͨ���ļ����ر��ʽ
	 * @throws Exception
	 */
	@Test
	public void testApprove2()throws Exception{
		ExpressRunner runner = new ExpressRunner();
		//�������������
		runner.addOperatorWithAlias("���", "if",null);
	  runner.addOperatorWithAlias("����", "else",null);
  	runner.addOperatorWithAlias("����", ">",null);
		//
		runner.addFunctionOfServiceMethod("��ӡ", System.out, "println",new String[] { "String" }, null);
		//���巽��
		runner.addFunction("����ͨ��", new ApproveOperator(1));
		runner.addFunction("��������", new ApproveOperator(2));
		runner.addFunction("����޸�", new ApproveOperator(3));
		//�����ļ�
		runner.loadExpressFromFile("example/approve1");
		//��ָ���ļ��л�ȡ��ʾʽ����ָ�
		
		//���������ı���
		IExpressContext<String,Object> expressContext = new DefaultContext<String, Object>();
		expressContext.put("����", "������");
		expressContext.put("�ܼ�", "���ܼ�");
		expressContext.put("����", "�Ų���");
		expressContext.put("������", "Сǿ");
		expressContext.put("���", new Integer(5000));
		
		runner.executeByExpressName("example/approve1", expressContext, null, false,false,null);
	}
	
	/**
	 * ͨ���ļ����ط��������ʽ
	 * @throws Exception
	 */
	@Test
	public void testApprove3()throws Exception{
		ExpressRunner runner = new ExpressRunner();
		//�������������
		runner.addOperatorWithAlias("���", "if",null);
	  runner.addOperatorWithAlias("����", "else",null);
  	runner.addOperatorWithAlias("����", ">",null);
		//
		runner.addFunctionOfServiceMethod("��ӡ", System.out, "println",new String[] { "String" }, null);		
		//�����ļ�
		runner.loadExpressFromFile("example/approve");
		//���������ı���
		IExpressContext<String,Object> expressContext = new DefaultContext<String, Object>();
		expressContext.put("����", "������");
		expressContext.put("�ܼ�", "���ܼ�");
		expressContext.put("����", "�Ų���");
		expressContext.put("������", "Сǿ");
		expressContext.put("���", new Integer(6000));
		
		runner.executeByExpressName("example/approve", expressContext, null, false,false,null);
	}
	
	/**
	 * �Ӳ�ͬ���ļ��м��ط��������ʽ
	 * @throws Exception
	 */
	@Test
	public void testApprove4()throws Exception{
		ExpressRunner runner = new ExpressRunner();
		//�������������
		runner.addOperatorWithAlias("���", "if",null);
	  runner.addOperatorWithAlias("����", "else",null);
  	runner.addOperatorWithAlias("����", ">",null);
		//
		runner.addFunctionOfServiceMethod("��ӡ", System.out, "println",new String[] { "String" }, null);
		
		//�����ļ�
		runner.loadExpressFromFile("example/approve1");
		runner.loadExpressFromFile("example/approve2");
		//���������ı���
		IExpressContext<String,Object> expressContext = new DefaultContext<String, Object>();
		expressContext.put("����", "������");
		expressContext.put("�ܼ�", "���ܼ�");
		expressContext.put("����", "�Ų���");
		expressContext.put("������", "Сǿ");
		expressContext.put("���", new Integer(7000));
		
		runner.executeByExpressName("example/approve1", expressContext, null, false,false,null);
	}
	
}
