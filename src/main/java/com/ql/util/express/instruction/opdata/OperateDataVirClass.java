package com.ql.util.express.instruction.opdata;

import java.util.List;

import org.apache.commons.logging.Log;

import com.ql.util.express.InstructionSet;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.InstructionSetRunner;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.OperateDataCacheManager;


/**
 * ����Class���ڴ����
 * @author xuannan
 *
 */
public class OperateDataVirClass extends OperateDataAttr{
    /**
     * ����Class������������
     */
    InstructionSetContext context;     
    /**
     * �������ָ���
     */
    InstructionSet virClassInstructionSet;
    
    boolean isTrace;
    Log log;    
    public OperateDataVirClass(String name){
    	super(name,null);
    }
	public void initialInstance(InstructionSetContext parent,OperateData[] parameters, 
			List<String> errorList,boolean aIsTrace,Log aLog) throws Exception {		
		this.isTrace = aIsTrace;
		this.log = aLog;
		this.context = OperateDataCacheManager.fetchInstructionSetContext(false,
				parent.getExpressRunner(),parent,parent.getExpressLoader(),parent.isSupportDynamicFieldName());
		Object functionSet = parent.getSymbol(this.name);		
		if (functionSet == null || functionSet instanceof InstructionSet == false) {
			throw new Exception("û���ҵ��Զ������\"" + this.name + "\"");
		}
		this.virClassInstructionSet = (InstructionSet)functionSet;
		
		OperateDataLocalVar[] vars = virClassInstructionSet.getParameters();
		for(int i=0;i<vars.length;i++){
			//ע��˴�����new һ���µĶ��󣬷���ͻ��ڶ�ε��õ�ʱ�������ݳ�ͻ
			OperateDataLocalVar var = OperateDataCacheManager.fetchOperateDataLocalVar(vars[i].getName(),vars[i].getOrgiType());
			this.context.addSymbol(var.getName(), var);
			var.setObject(context, parameters[i].getObject(parent));
		}
		InstructionSetRunner.execute(new InstructionSet[]{(InstructionSet)virClassInstructionSet},
				context,errorList,aIsTrace,false,false,log);
	}
	public OperateData callSelfFunction(String functionName,OperateData[] parameters) throws Exception{
		Object function = this.context.getSymbol(functionName);
		if (function == null || function instanceof InstructionSet == false) {
			throw new Exception("��VClass:"+ this.name +"��û�ж��庯��\"" + functionName + "\"");
		}
		InstructionSet functionSet = (InstructionSet)function;
		
		InstructionSetContext tempContext = OperateDataCacheManager.fetchInstructionSetContext(
				true,this.context.getExpressRunner(),this.context,this.context.getExpressLoader(),
				this.context.isSupportDynamicFieldName());
		OperateDataLocalVar[] vars = functionSet.getParameters();
		for(int i=0;i<vars.length;i++){
			//ע��˴�����new һ���µĶ��󣬷���ͻ��ڶ�ε��õ�ʱ�������ݳ�ͻ
			OperateDataLocalVar var = OperateDataCacheManager.fetchOperateDataLocalVar(vars[i].getName(),vars[i].getOrgiType());
			tempContext.addSymbol(var.getName(), var);
			var.setObject(tempContext, parameters[i].getObject(this.context));
		}
		Object result =InstructionSetRunner.execute(new InstructionSet[]{(InstructionSet)functionSet},
				tempContext,null,this.isTrace,false,true,this.log);
		return OperateDataCacheManager.fetchOperateData(result,null);
	}
	public Object getValue(Object name) throws Exception{
		Object o = this.context.findAliasOrDefSymbol(name.toString());
		if(o == null){
			return null;
		}else if(o instanceof OperateData){//��������
			return ((OperateData)o).getObject(context);
		}else if( o instanceof InstructionSet){//�궨��
			InstructionSetContext tempContext = OperateDataCacheManager.fetchInstructionSetContext(
					true,this.context.getExpressRunner(),this.context,this.context.getExpressLoader(),
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
			throw new Exception("��֧�ֵ���������:" + o.getClass().getName());
		}
	}
	public void setValue(String name,Object value) throws Exception{
		Object o = this.context.findAliasOrDefSymbol(name.toString());
		if(o instanceof OperateData){
			((OperateData)o).setObject(context,value);
		}else{
			throw new Exception("��֧�ֵ���������:" + o.getClass().getName());
		}
	}
	public Class<?> getValueType(Object name) throws Exception{
		Object o = this.context.findAliasOrDefSymbol(name.toString());
		if(o instanceof OperateData){
			return ((OperateData)o).getType(context);
		}else{
			throw new Exception("��֧�ֵ���������:" + o.getClass().getName());
		}
	}
	public Object getObjectInner(InstructionSetContext context) {
		 return this;
	}
    
	public Class<?> getType(InstructionSetContext context) throws Exception {
		return this.getClass();
	}

	public void setObject(InstructionSetContext parent, Object object) {
			throw new RuntimeException("��֧�ֵķ���");
	}
	
    public String toString(){
    	return "VClass[" + this.name+"]";
    }
}
