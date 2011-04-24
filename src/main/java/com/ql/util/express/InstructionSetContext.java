package com.ql.util.express;

import java.util.HashMap;
import java.util.Map;

import com.ql.util.express.instruction.OperateDataAttr;

public class InstructionSetContext<K,V>  implements IExpressContext<K,V> {
	private IExpressContext<K,V> parent = null;
	private Map<K,V> content;
	/**
	 * 符号表
	 */
	private Map<String,Object> symbolTable;
	
	private ExpressLoader expressLoader;
	
	private boolean isSupportDynamicFieldName = false;
		
	public InstructionSetContext(IExpressContext<K,V> aParent,ExpressLoader aExpressLoader,boolean aIsSupportDynamicFieldName){
		parent = aParent;
		this.expressLoader = aExpressLoader;
		this.isSupportDynamicFieldName = aIsSupportDynamicFieldName;
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
	
	public void setSupportDynamicFieldName(boolean isSupportDynamicFieldName) {
		this.isSupportDynamicFieldName = isSupportDynamicFieldName;
	}
	public boolean isSupportDynamicFieldName(){
		 return this.isSupportDynamicFieldName;
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

	public IExpressContext<K,V> getParent(){
		return  this.parent;
	}
	public V get(Object key){
		if(this.content != null && this.content.containsKey(key)){
			return this.content.get(key);
		}else if(this.parent != null){
			return this.parent.get(key);
		}
		return null;
	}
	public void putKeyDefine(K key){
		if(this.content == null){
			this.content = new HashMap<K,V>();
		}
		this.content.put(key,null);
	}
	public V put(K key, V value){
		if(this.content != null && this.content.containsKey(key) ){
			return this.content.put(key,value);
		}else if(this.parent != null){
			return this.parent.put(key,value);
		}else{
			throw new RuntimeException("没有定义局部变量：" + key +",而且没有全局上下文");
		}
	}
}
