package com.ql.util.express.test;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.ApplicationContext;

import com.ql.util.express.IExpressContext;

@SuppressWarnings("serial")
public class ExpressContextExample extends HashMap<String,Object> implements IExpressContext<String,Object>{
	
	private ApplicationContext context;

    public ExpressContextExample(ApplicationContext aContext){
    	this.context = aContext;
    }
    public ExpressContextExample( Map<String,Object> aProperties,ApplicationContext aContext){
    	super(aProperties);
    	this.context = aContext;
    }
    
	/**
	 * ���󷽷����������ƴ������б�����ȡ����ֵ
	 */
	public Object get(Object name) {
		Object result = null;
		if(((String)name).equalsIgnoreCase("��������")){
			result = Boolean.valueOf(true);
		} else if (((String)name).equalsIgnoreCase("�����û�")) {
			result = Boolean.valueOf(true);
		} else {
			result = super.get(name);
		}
		try{
		if (result == null &&this.context!= null && this.context.containsBean((String)name)) {
			//�����Spring�����а���bean���򷵻�String��Bean
			result = this.context.getBean((String)name);
		}
		}catch(Exception  e){
		  throw new RuntimeException(e);	
		}
		return result;
	}

	public Object  put(String name, Object object) {
	   if(name.equalsIgnoreCase("myDbData")){
		   throw new RuntimeException("û��ʵ��");
	   }
       return  super.put(name,object);
	}


}
