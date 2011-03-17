package com.ql.util.express.test;

import org.apache.commons.logging.Log;
import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExportItem;
import com.ql.util.express.ExpressLoader;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;

public class LoadExpressFromFileTest {
	@Test
	public void testLoadFromFile() throws Exception {
		ExpressRunner runner = new ExpressRunner(true);
		ExpressLoader loader = new ExpressLoader(runner);
		loader.loadExpressFromFile("functiondef");
		loader.loadExpressFromFile("main");
		ExportItem[] exports = loader.getExportInfo();
		for (ExportItem item : exports) {
			System.out.println(item);
		}
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		Log log = new MyLog("���Ѳ���");
		Object r = runner.execute(new InstructionSet[] { loader
				.getInstructionSet("main") }, loader, context, null, null,
				true, false, log);
		System.out.println("���н��" + r);
		System.out.println("context:" + context);

		context = new DefaultContext<String, Object>();
		r = runner.execute(new InstructionSet[] { runner
				.parseInstructionSet("initial;�ۼ�;�ۼ�;return qh;") }, loader,
				context, null, null, true, false, log);

		System.out.println("���н��" + r);
		System.out.println("context:" + context);
	}

}
