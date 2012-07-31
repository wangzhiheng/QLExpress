package com.ql.util.express.test.demo;

import java.util.HashMap;
import java.util.Map;

import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

@SpringApplicationContext("classpath:spring-express-config.xml")
public class TestQlExpress  extends UnitilsJUnit4{
	
	@SpringBeanByName
	QlExpressUtil qlExpressUtil;
	
	
	/**
	 * ʹ��qlExpressUtil��չ��QlExpressRunner��expressContext������
	 * ʹ�ű��п���ֱ�ӵ���spring�е�bean
	 * 
	 * ����ҵ���߼����£�
	 * ******************************************************************
	 * 
	 * �û�qlExpressע��һ���˺�
	 * �û�qlExpress���˸��Ա���
     * ͨ���Լ��Ŀ��ľ�Ӫ���Ǽ��������ߣ�qlExpress���ϵ��������ܹ��ص�������Ϊ�̳��û�
     * ������һ�����ɹ��ˡ�
     * 
	 * ******************************************************************
	 * @throws Exception
	 */
	@org.junit.Test
	public void testScript() throws Exception{

		Map<String, Object> context = new HashMap<String, Object>();
		context.put("nick", "qlExpress");
		qlExpressUtil.execute("�û�A = bizLogicBean.signUser(nick);" +
							  "bizLogicBean.openShop(�û�A );" +
				              "for(;; bizLogicBean.isShopOpening(�û�A ) && !bizLogicBean.upgradeShop(�û�A )){bizLogicBean.addScore(�û�A );}", 
				              context);
	}
	
	
	/**
	 * 
	 * ʹ��Ԥ�ȶ���ĺ������ű����߼���
	 * ******************************************************************
	 * 
	 * "�û�A = ע���û�(nick);" +
	 * "����(�û�A);" +
     * "for(;;����Ӫҵ��(�û�A) && !��������(�û�A)){�Ǽ�����(�û�A);}
     *
     * ******************************************************************
	 * @throws Exception
	 */
	
	@org.junit.Test
	public void testDeclareMethodScript() throws Exception{

		Map<String, Object> context = new HashMap<String, Object>();
		context.put("nick", "qlExpress");
		qlExpressUtil.execute("�û�A = ע���û�(nick);" +
							  "����(�û�A);" +
				              "for(;;����Ӫҵ��(�û�A) && !��������(�û�A)){�Ǽ�����(�û�A);}", 
				              context);
	}
	

}
