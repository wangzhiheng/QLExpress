package com.ql.util.express.test;

import org.junit.Assert;
import org.junit.Test;

import com.ql.util.express.ExpressRunner;

public class NumberComputerTest {
	@Test
	public void testMod() throws Exception {
		ExpressRunner runner = new ExpressRunner(true);
		Assert.assertTrue("%º∆À„¥ÌŒÛ",	runner.execute("20 % 5", null, null, true, true).toString()
				.equalsIgnoreCase("0"));
		Assert.assertTrue("%º∆À„¥ÌŒÛ",	runner.execute("20 % 3", null, null, true, true).toString()
				.equalsIgnoreCase("2"));
		Assert.assertTrue("Modº∆À„¥ÌŒÛ",	runner.execute("20 mod 5", null, null, true, true).toString()
				.equalsIgnoreCase("4"));
		Assert.assertTrue("Modº∆À„¥ÌŒÛ",	runner.execute("20 mod 3", null, null, true, true).toString()
				.equalsIgnoreCase("6"));
		Assert.assertTrue("Modº∆À„¥ÌŒÛ",	runner.execute("20 mod 1", null, null, true, true).toString()
				.equalsIgnoreCase("20"));
		}
}
