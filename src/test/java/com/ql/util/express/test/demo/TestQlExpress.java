package com.ql.util.express.test.demo;

import java.util.HashMap;
import java.util.Map;

import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

@SpringApplicationContext("springConfig.xml")
public class TestQlExpress  extends UnitilsJUnit4{
	
	@SpringBeanByName
	QlExpressUtil qlExpressUtil;
	
	@org.junit.Test
	public void testScript() throws Exception{

		Map<String, Object> context = new HashMap<String, Object>();
		context.put("nick", "qlExpress");
		qlExpressUtil.execute("�û�A = bizLogicBean.signUser(nick);" +
							  "bizLogicBean.openShop(�û�A );" +
				              "for(;; bizLogicBean.isShopOpening(�û�A ) && !bizLogicBean.upgradeShop(�û�A )){bizLogicBean.addScore(�û�A );}", 
				              context);
	}
	
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
