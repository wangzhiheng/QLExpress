package com.ql.util.express;

import java.util.HashMap;
import java.util.Map;

import com.ql.util.express.instruction.OperateDataAttr;

@SuppressWarnings("serial")
public class InstructionSetContext<K,V> extends HashMap<K,V> implements IExpressContext<K,V> {
	 private FuncitonCacheManager functionCachManager;
	/**
	 * 函数调用开启标志，在Method执行完毕后清除
	 */
    private boolean isStartCachFunctionCall = false;

	private IExpressContext<K,V> parent = null;
	/**
	 * 符号表
	 */
	private Map<String,Object> symbolTable;
	
	private ExpressLoader expressLoader;
		
	public InstructionSetContext(IExpressContext<K,V> aParent,ExpressLoader aExpressLoader, FuncitonCacheManager aFunctionCachManager){
		parent = aParent;
		this.functionCachManager =  aFunctionCachManager;
		this.expressLoader = aExpressLoader;
	}

	public void clearFuncitonCacheManager(){
		if(this.functionCachManager != null){
			this.functionCachManager.clearCache();
		}
	}
	public FuncitonCacheManager getFunctionCachManagerNoCreate() {
		return this.functionCachManager;
	}
	public FuncitonCacheManager getFunctionCachManagerWithCreate() {
		if(this.functionCachManager == null){
			this.functionCachManager = new FuncitonCacheManager();
		}
		return functionCachManager;
	}

	public void exportSymbol(String varName,Object aliasNameObject) throws Exception{
		if( this.parent != null && this.parent instanceof InstructionSetContext){
			((InstructionSetContext<K,V>)this.parent).exportSymbol(varName, aliasNameObject);
		}else{
		    this.addSymbol(varName, aliasNameObject);
		}
	}
	public void addSymbol(String varName,Object aliasNameObject) throws Exception{
		if(this.symbolTable == null){
			this.symbolTable = new HashMap<String,Object>();
		}
		if(this.symbolTable.containsKey(varName)){
			throw new Exception("变量" + varName + "已经存在，不能重复定义，也不能再从函数内部 exprot ");
		}
		this.symbolTable.put(varName,aliasNameObject);
	}
	public Object findAliasOrDefSymbol(String varName)throws Exception{
		Object result = null;
		if(this.symbolTable != null){
			result = this.symbolTable.get(varName);
		}
		if(result == null){
			if( this.parent != null && this.parent instanceof InstructionSetContext){
			    result = ((InstructionSetContext<K,V>)this.parent).findAliasOrDefSymbol(varName);
			}else{
			    result = null;
			}
		}	
		return result;		
	}
	public Object getSymbol(String varName) throws Exception{
		Object result = null;
		if(this.symbolTable != null){
			result = this.symbolTable.get(varName);
		}
		if( result == null && this.expressLoader != null){
			result = this.expressLoader.getInstructionSet(varName);
		}
		if(result == null){
			if( this.parent != null && this.parent instanceof InstructionSetContext){
			    result = ((InstructionSetContext<K,V>)this.parent).getSymbol(varName);
			}else{
			    result = new OperateDataAttr(varName,null);
			    this.addSymbol(varName, result);
			}
		}	
		return result;
	}
	
	public ExpressLoader getExpressLoader() {
		return expressLoader;
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
