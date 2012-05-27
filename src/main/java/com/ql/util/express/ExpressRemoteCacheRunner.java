package com.ql.util.express;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;

/**
 * Զ�̻������
 * @author tianqiao
 *
 */
public abstract class ExpressRemoteCacheRunner {
	
	

	public void loadCache(String expressName,String text){
		InstructionSet instructionSet;
		try {
			instructionSet = getExpressRunner().parseInstructionSet(text);
			CacheObject cache = new CacheObject();
			cache.setExpressName(expressName);
			cache.setText(text);
			cache.setInstructionSet(instructionSet);
			this.putCache(expressName, cache);
		} catch (Exception e) {
			throw new RuntimeException("����ָ�������̳��ִ���.",e);
		}		
	}
	
	
	public Object execute(String name,IExpressContext<String,Object> context, List<String> errorList,
			boolean isTrace,boolean isCatchException, Log aLog){
		try {
			CacheObject cache = (CacheObject) this.getCache(name);
			if(cache==null){
				throw new RuntimeException("δ��ȡ���������.");
			}
			return getExpressRunner().execute(new InstructionSet[] {cache.getInstructionSet()}, context, errorList, isTrace, isCatchException, aLog);
		} catch (Exception e) {
			throw new RuntimeException("��ȡ������Ϣ������ִ��ָ����ִ���.",e);
		}		
	}
	
	/**
	 * ��ȡִ����ExpressRunner
	 * @return
	 */
	public  abstract ExpressRunner getExpressRunner();
	/**
	 * ��ȡ�������
	 * @param key
	 * @return
	 */
	public abstract Object getCache(String key);
	/**
	 * ���û���Ķ���
	 * @param key
	 * @param object
	 */
	public abstract void putCache(String key,Object object );

}


class CacheObject implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -145121001676214513L;

	private String expressName;
	
	private String text;
	
	private InstructionSet instructionSet;

	public String getExpressName() {
		return expressName;
	}

	public void setExpressName(String name) {
		this.expressName = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public InstructionSet getInstructionSet() {
		return instructionSet;
	}

	public void setInstructionSet(InstructionSet instructionSet) {
		this.instructionSet = instructionSet;
	}

}
