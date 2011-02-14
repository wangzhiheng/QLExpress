package com.ql.util.express.test;

import org.junit.Assert;
import org.junit.Test;

import com.ql.util.express.ExpressRunner;

public class NumberComputerTest {
	@Test
	public void testMod() throws Exception {
		ExpressRunner runner = new ExpressRunner(true);
		Assert.assertTrue("Mod�������",	runner.execute("20 mod 5", null, null, true, true).toString()
				.equalsIgnoreCase("0"));
		Assert.assertTrue("Mod�������",	runner.execute("20 mod 3", null, null, true, true).toString()
				.equalsIgnoreCase("2"));
		Assert.assertTrue("Mod�������",	runner.execute("20 mod 1", null, null, true, true).toString()
				.equalsIgnoreCase("0"));
		}
}
