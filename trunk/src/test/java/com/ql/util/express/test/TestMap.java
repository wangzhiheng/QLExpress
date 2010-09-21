package com.ql.util.express.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;

public class TestMap {
	
	@Test
	public  void testmain() {
		IExpressContext expressContext = new IExpressContext() {
			Map map = new HashMap<Object, Object>();

			public Object get(Object key) {

				return map.get(key);
			}

			public Object put(Object name, Object object) {
				return map.put(name, object);
			}
		};
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("key1", 1);
		expressContext.put("map", map);
		
		String expressionstr="map.key1";
		ExpressRunner runner = new ExpressRunner();
		try {
			
			System.out.println(runner.execute(expressionstr, null, null, true, true));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			   
	}
}
