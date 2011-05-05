package com.ql.util.express.test;

import org.apache.commons.logging.Log;
import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExportItem;
import com.ql.util.express.ExpressRunner;

public class LoadExpressFromFileTest {
	@Test
	public void testLoadFromFile() throws Exception {
		ExpressRunner runner = new ExpressRunner(false,false);
		runner.loadExpressFromFile("functiondef");
		runner.loadExpressFromFile("main");
		ExportItem[] exports = runner.getExportInfo();
		for (ExportItem item : exports) {
			System.out.println(item.getGlobeName());
		}
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		Log log = new MyLog("���Ѳ���");
		Object r = runner.executeByExpressName("main", context, null,
				false, false, log);
		System.out.println("���н��" + r);
		System.out.println("context:" + context);

		context = new DefaultContext<String, Object>();
		r = runner.execute("initial;�ۼ�;�ۼ�;return qh;",
				context, null, true, false, log);

		System.out.println("���н��" + r);
		System.out.println("context:" + context);
	}

}
