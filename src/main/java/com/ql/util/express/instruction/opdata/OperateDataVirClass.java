package com.ql.util.express.instruction.opdata;

import java.util.List;

import org.apache.commons.logging.Log;

import com.ql.util.express.InstructionSet;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.InstructionSetRunner;
import com.ql.util.express.OperateData;


/**
 * 虚拟Class的内存对象
 * @author xuannan
 *
 */
public class OperateDataVirClass extends OperateDataAttr{
    /**
     * 虚拟Class的数据上下文
     */
    InstructionSetContext<String, Object> context;     
    /**
     * 虚拟类的指令集合
     */
    InstructionSet virClassInstructionSet;
    
    boolean isTrace;
    Log log;    
    public OperateDataVirClass(String name){
    	super(name);
    }
	public void initialInstance(InstructionSetContext<String,Object> parent,OperateData[] parameters, 
			List<String> errorList,boolean aIsTrace,Log aLog) throws Exception {		
		this.isTrace = aIsTrace;
		this.log = aLog;
		this.context = new InstructionSetContext<String, Object>(false,
				parent.getExpressRunner(),parent,parent.getExpressLoader(),parent.isSupportDynamicFieldName());
		Object functionSet = parent.getSymbol(this.name);		
		if (functionSet == null || functionSet instanceof InstructionSet == false) {
			throw new Exception("没有找到自定义对象\"" + this.name + "\"");
		}
		this.virClassInstructionSet = (InstructionSet)functionSet;
		
		OperateDataLocalVar[] vars = virClassInstructionSet.getParameters();
		for(int i=0;i<vars.length;i++){
			//注意此处必须new 一个新的对象，否则就会在多次调用的时候导致数据冲突
			OperateDataLocalVar var = new OperateDataLocalVar(vars[i].getName(),vars[i].type);
			this.context.addSymbol(var.getName(), var);
			var.setObject(context, parameters[i].getObject(parent));
		}
		InstructionSetRunner.execute(new InstructionSet[]{(InstructionSet)virClassInstructionSet},
				context,errorList,aIsTrace,false,false,log);
	}
	public OperateData callSelfFunction(String functionName,OperateData[] parameters) throws Exception{
		Object function = this.context.getSymbol(functionName);
		if (function == null || function instanceof InstructionSet == false) {
			throw new Exception("在VClass:"+ this.name +"中没有定义函数\"" + functionName + "\"");
		}
		InstructionSet functionSet = (InstructionSet)function;
		
		InstructionSetContext<String, Object> tempContext = new InstructionSetContext<String, Object>(
				this.context.getExpressRunner(),this.context,this.context.getExpressLoader(),
				this.context.isSupportDynamicFieldName());
		OperateDataLocalVar[] vars = functionSet.getParameters();
		for(int i=0;i<vars.length;i++){
			//注意此处必须new 一个新的对象，否则就会在多次调用的时候导致数据冲突
			OperateDataLocalVar var = new OperateDataLocalVar(vars[i].getName(),vars[i].type);
			tempContext.addSymbol(var.getName(), var);
			var.setObject(tempContext, parameters[i].getObject(this.context));
		}
		Object result =InstructionSetRunner.execute(new InstructionSet[]{(InstructionSet)functionSet},
				tempContext,null,this.isTrace,false,true,this.log);
		return new OperateData(result,null);
	}
	public Object getValue(Object name) throws Exception{
		Object o = this.context.findAliasOrDefSymbol(name.toString());
		if(o == null){
			return null;
		}else if(o instanceof OperateData){//变量定义
			return ((OperateData)o).getObject(context);
		}else if( o instanceof InstructionSet){//宏定义
			InstructionSetContext<String, Object> tempContext = new InstructionSetContext<String, Object>(
					this.context.getExpressRunner(),this.context,this.context.getExpressLoader(),
					this.context.isSupportDynamicFieldName());
			Object result =InstructionSetRunner.execute(
					this.context.getExpressRunner(),
					new InstructionSet[]{(InstructionSet)o},
					this.context.getExpressLoader(),
					tempContext, 
					null,
					this.isTrace,
					false,false,this.log,
					this.context.isSupportDynamicFieldName());
			if(result instanceof OperateData){
				return ((OperateData)result).getObject(this.context);
			}else{
			    return result;
			}			
		}else{
			throw new Exception("不支持的数据类型:" + o.getClass().getName());
		}
	}
	public void setValue(String name,Object value) throws Exception{
		Object o = this.context.findAliasOrDefSymbol(name.toString());
		if(o instanceof OperateData){
			((OperateData)o).setObject(context,value);
		}else{
			throw new Exception("不支持的数据类型:" + o.getClass().getName());
		}
	}
	public Class<?> getValueType(Object name) throws Exception{
		Object o = this.context.findAliasOrDefSymbol(name.toString());
		if(o instanceof OperateData){
			return ((OperateData)o).getType(context);
		}else{
			throw new Exception("不支持的数据类型:" + o.getClass().getName());
		}
	}
	public Object getObjectInner(InstructionSetContext<String,Object> context) {
		 return this;
	}
    
	public Class<?> getType(InstructionSetContext<String,Object> context) throws Exception {
		return this.getClass();
	}

	public void setObject(InstructionSetContext<String,Object> parent, Object object) {
			throw new RuntimeException("不支持的方法");
	}
	
    public String toString(){
    	return "VClass[" + this.name+"]";
    }
}
