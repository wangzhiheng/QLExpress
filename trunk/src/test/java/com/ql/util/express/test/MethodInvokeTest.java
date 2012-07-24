package com.ql.util.express.test;

import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;

/**
 * @author tianqiao
 */
public class MethodInvokeTest {
	
	
	@Test
	public void testNullParameter() throws Exception {
		ExpressRunner runner = new ExpressRunner(false,false);
		
		runner.addFunctionOfClassMethod("getSearchResult", 
				MethodInvokeTest.class.getName(), 
				"getSearchResult", 
				new Class[] {PersonalShopInfo.class}, 
				null);
		
		IExpressContext<String,Object> expressContext = new DefaultContext<String,Object>();		
		try{
			Object r = runner.execute("getSearchResult(null)", 
					expressContext, null, false, false);
			System.out.print("r=" + r);
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	@Test
	public void testNullParameter2() throws Exception {
		ExpressRunner runner = new ExpressRunner(false,false);
		
		runner.addFunctionOfClassMethod("getOnlinePersonalShopInfo", 
				MethodInvokeTest.class.getName(), 
				"getOnlinePersonalShopInfo", 
				new Class[] {long.class}, 
				null);
		
		runner.addFunctionOfClassMethod("getSearchResult", 
				MethodInvokeTest.class.getName(), 
				"getSearchResult", 
				new Class[] {PersonalShopInfo.class}, 
				null);
		
		IExpressContext<String,Object> expressContext = new DefaultContext<String,Object>();		
		try{
			Object r = runner.execute("getSearchResult(getOnlinePersonalShopInfo(123L))", 
					expressContext, null, false, false);
			System.out.print("r=" + r);
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e);
		}
	}
	
	//查找在线的店铺信息
	public PersonalShopInfo getOnlinePersonalShopInfo(long userId) {
		return null;
//		return new PersonalShopInfo();
	}
	
	//搜索引擎返回是否存在改店铺信息
	public boolean getSearchResult(PersonalShopInfo personalInfo) {
		if(personalInfo == null) {
			return false;
		} else {
			return true;
		}
	}
}

class PersonalShopInfo {
	
}


