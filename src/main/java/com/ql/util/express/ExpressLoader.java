package com.ql.util.express;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表达式装载器
 * 
 * @author xuannan
 * 
 */
public class ExpressLoader {
	private Map<String, InstructionSet> expressInstructionSetCache = new HashMap<String, InstructionSet>();
	ExpressRunner runner;
	public ExpressLoader(ExpressRunner aRunner){
		this.runner = aRunner;
	}
	public InstructionSet parseInstructionSet(
			String expressName, String expressString, boolean isCache)
			throws Exception {
		InstructionSet parseResult = null;
		if (isCache == true) {
			parseResult = expressInstructionSetCache.get(expressName);
			if (parseResult == null) {
				synchronized (expressInstructionSetCache) {
					parseResult = expressInstructionSetCache.get(expressName);
					if (parseResult == null) {
						parseResult = runner.parseInstructionSet(expressString);
						expressInstructionSetCache
								.put(expressName, parseResult);
					}
				}
			}
		} else {
			parseResult = runner.parseInstructionSet(expressString);
		}
		return parseResult;
	}

	public InstructionSet getInstructionSet(String expressName) {
		synchronized (expressInstructionSetCache) {
			return expressInstructionSetCache.get(expressName);
		}
	}
}
