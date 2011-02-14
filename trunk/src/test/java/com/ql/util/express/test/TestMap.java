package com.ql.util.express.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;

public class TestMap {

	@Test
	public void testmain() throws Exception {
		IExpressContext<String,Object> expressContext = new IExpressContext<String,Object> () {
			Map<String, Object> map = new HashMap<String, Object>();
			public Object put(String name, Object object) {
				return map.put(name, object);
			}
			public Object get(Object key) {
				return map.get(key);
			}
		};
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("key1", 1);
		expressContext.put("map", map);

		String expressionstr = "map.key1";
		ExpressRunner runner = new ExpressRunner(true);
		Object r =runner.execute(expressionstr, expressContext, null,
				true, true);
		Assert.assertTrue("Map��ȡ����", r.toString().equalsIgnoreCase("1"));

	}
}
