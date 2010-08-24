package com.ql.util.express;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
class InstructionSetContext<K,V> extends HashMap<K,V> implements IExpressContext<K,V> {
	 protected OperatorManager operatorManager;
	 RunEnvironment environmen;
	/**
	 * 函数调用开启标志，在Method执行完毕后清除
	 */
    private boolean isStartCachFunctionCall = false;

	IExpressContext<K,V> parent = null;
	/**
	 * 符号表
	 */
	private Map<String,OperateDataAttr> symbolTable;
		
	public InstructionSetContext(IExpressContext<K,V> aParent,RunEnvironment aEnvironmen ,OperatorManager aOperatorManager){
		parent = aParent;
		this.operatorManager = aOperatorManager;
		this.environmen = aEnvironmen;
	}
	public OperatorManager getOperatorManager(){
		return this.operatorManager;
	}
	
	public RunEnvironment getEnvironmen() {
		return environmen;
	}
	
	public void setEnvironmen(RunEnvironment environmen) {
		this.environmen = environmen;
	}
	public void addSymbol(String varName,OperateDataAttr aliasNameObject){
		if(this.symbolTable == null){
			this.symbolTable = new HashMap<String,OperateDataAttr>();
		}
		this.symbolTable.put(varName,aliasNameObject);
	}
	public OperateDataAttr getSymbol(String varName){
		OperateDataAttr result = null;
		if(this.symbolTable != null){
			result = this.symbolTable.get(varName);
		}
		if(result == null){
			if( this.parent != null && this.parent instanceof InstructionSetContext){
			    result = ((InstructionSetContext)this.parent).getSymbol(varName);
			}else{
			    result = new OperateDataAttr(varName,null);
			    this.addSymbol(varName, result);
			}
		}	
		return result;
	}
	
	public void  startFunctionCallCache(){
		this.isStartCachFunctionCall = true;
	}
	public boolean isStartFunctionCallCache(){
		return this.isStartCachFunctionCall;
	}
	public void stopStartFunctionCallCache(){
		this.isStartCachFunctionCall = false;
	}
	
	public IExpressContext<K,V> getParent(){
		return  this.parent;
	}
	public V get(Object key){
		if(super.containsKey(key)){
			return super.get(key);
		}else if(this.parent != null){
			return this.parent.get(key);
		}
		return null;
	}

	public void putKeyDefine(K key){
		super.put(key,null);
	}
	public V put(K key, V value){
		if(super.containsKey(key) ){
			return super.put(key,value);
		}else if(this.parent != null){
			return this.parent.put(key,value);
		}else{
			throw new RuntimeException("没有定义局部变量：" + key +",而且没有全局上下文");
		}
	}
}
