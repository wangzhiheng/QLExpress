package com.ql.util.express;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
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
	private String loadFromFile(String fileName) throws Exception{
		fileName = fileName.replace('.', '/') +".ql";
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
		if(in  == null){
			throw new Exception("不能找到表达是文件：" + fileName);
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder builder = new  StringBuilder();
		String tmpStr = null;
		while( ( tmpStr = reader.readLine()) != null){
			builder.append(tmpStr);
		}
		System.out.println(builder.toString());
		return builder.toString();
	}

	public InstructionSet loadExpressFromFile(
			String fileName)
			throws Exception {
		return parseInstructionSet(fileName,loadFromFile(fileName));
		
	}

	public void addInstructionSet(String expressName, InstructionSet set)
			throws Exception {
		synchronized (expressInstructionSetCache) {
			if (expressInstructionSetCache.containsKey(expressName)) {
				throw new Exception("表达是定义重复：" + expressName);
			}
			expressInstructionSetCache.put(expressName, set);
		}
	}

	public InstructionSet parseInstructionSet(String expressName,
			String expressString) throws Exception {
		InstructionSet parseResult = null;
		if (expressInstructionSetCache.containsKey(expressName)) {
			throw new Exception("表达是定义重复：" + expressName);
		}
		synchronized (expressInstructionSetCache) {
			parseResult = this.runner.parseInstructionSet(expressString);
			// 需要将函数和宏定义都提取出来
			for (FunctionInstructionSet item : parseResult
					.getFunctionInstructionSets()) {
				this.addInstructionSet(item.name, item.instructionSet);
			}
			this.addInstructionSet(expressName, parseResult);
		}
		return parseResult;
	}
   public void clear(){
	   this.expressInstructionSetCache.clear();
   }
	public InstructionSet getInstructionSet(String expressName) {
		synchronized (expressInstructionSetCache) {
			return expressInstructionSetCache.get(expressName);
		}
	}
}
