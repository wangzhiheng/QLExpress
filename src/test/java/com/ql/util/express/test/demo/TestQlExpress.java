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
		qlExpressUtil.execute("用户A = bizLogicBean.signUser(nick);" +
							  "bizLogicBean.openShop(用户A );" +
				              "for(;; bizLogicBean.isShopOpening(用户A ) && !bizLogicBean.upgradeShop(用户A )){bizLogicBean.addScore(用户A );}", 
				              context);
	}
	
	@org.junit.Test
	public void testDeclareMethodScript() throws Exception{

		Map<String, Object> context = new HashMap<String, Object>();
		context.put("nick", "qlExpress");
		qlExpressUtil.execute("用户A = 注册用户(nick);" +
							  "开店(用户A);" +
				              "for(;;店铺营业中(用户A) && !店铺升级(用户A)){星级自增(用户A);}", 
				              context);
	}
	

}
