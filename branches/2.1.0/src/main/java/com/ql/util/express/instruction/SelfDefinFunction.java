package com.ql.util.express.instruction;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.ql.util.express.ExpressUtil;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;

/**
 * 用户自定义的函数操作
 * @author qhlhl2010@gmail.com
 *
 */
class SelfDefineClassFunctionOperator extends OperatorBase{
  String functionName;
  String[] parameterTypes;
  Class<?>[] parameterClasses ;
  Class<?> operClass;
  Method method;
  
  public SelfDefineClassFunctionOperator(String aOperName,String aClassName, String aFunctionName,
          Class<?>[] aParameterClassTypes,String[] aParameterDesc,String[] aParameterAnnotation,String aErrorInfo) throws Exception {
	    this.name = aOperName;
	    this.errorInfo = aErrorInfo;
	    this.functionName = aFunctionName;
	    this.parameterClasses = aParameterClassTypes;
	    this.parameterTypes = new String[aParameterClassTypes.length];
	    this.operDataDesc = aParameterDesc;
	    this.operDataAnnotation = aParameterAnnotation;
	    for(int i=0;i<this.parameterClasses.length;i++){
	      this.parameterTypes[i] = this.parameterClasses[i].getName();
	    }
	    operClass = ExpressUtil.getJavaClass(aClassName);
	    method = operClass.getMethod(functionName,parameterClasses);	  
  }

  public SelfDefineClassFunctionOperator(String aOperName,String aClassName, String aFunctionName,
                         String[] aParameterTypes,String[] aParameterDesc,String[] aParameterAnnotation,String aErrorInfo) throws Exception {
    this.name = aOperName;
    this.errorInfo = aErrorInfo;
    this.functionName = aFunctionName;
    this.parameterTypes = aParameterTypes;
    this.operDataDesc = aParameterDesc;
    this.operDataAnnotation = aParameterAnnotation;
    this.parameterClasses = new Class[this.parameterTypes.length];
    for(int i=0;i<this.parameterClasses.length;i++){
      this.parameterClasses[i] = ExpressUtil.getJavaClass(this.parameterTypes[i]);
    }
    operClass = ExpressUtil.getJavaClass(aClassName);
    method = operClass.getMethod(functionName,parameterClasses);
  }

  public OperateData executeInner(InstructionSetContext<String,Object> context, OperateData[] list) throws
      Exception {
      if(this.parameterClasses.length != list.length){
        throw new Exception("定义的参数长度与运行期传入的参数长度不一致");
      }
      Object[] parameres = new Object[list.length];
      for(int i=0;i<list.length;i++){
        parameres[i] = list[i].getObject(context);
      }
      Object obj = null;
      if( Modifier.isStatic(this.method.getModifiers())){
         obj = this.method.invoke(null,ExpressUtil.transferArray(parameres,parameterClasses));
      }else{
    	 obj = this.method.invoke(this.operClass.newInstance(),ExpressUtil.transferArray(parameres,parameterClasses));
      }

      if(obj != null){
          return new OperateData(obj,obj.getClass());
       }
       return null;
  }
}

/**
 * 用户自定义的服务函数操作
 * @author qhlhl2010@gmail.com
 *
 */
class SelfDefineServiceFunctionOperator extends OperatorBase{
  Object serviceObject;
  String functionName;
  String[] parameterTypes;
  Class<?>[] parameterClasses ;
  Method method;
  
	public SelfDefineServiceFunctionOperator(String aOperName,
			Object aServiceObject, String aFunctionName,
			Class<?>[] aParameterClassTypes,String[] aParameterDesc,String[] aParameterAnnotation, String aErrorInfo) throws Exception {
		this.name = aOperName;
		this.errorInfo = aErrorInfo;
		this.serviceObject = aServiceObject;
		this.functionName = aFunctionName;
		this.parameterClasses = aParameterClassTypes;
	    this.operDataDesc = aParameterDesc;
	    this.operDataAnnotation = aParameterAnnotation;
		this.parameterTypes = new String[this.parameterClasses.length];
		for (int i = 0; i < this.parameterClasses.length; i++) {
			this.parameterTypes[i] = this.parameterClasses[i].getName();
		}
		Class<?> operClass = serviceObject.getClass();
		method = operClass.getMethod(functionName, parameterClasses);

	}
  
  public SelfDefineServiceFunctionOperator(String aOperName,Object aServiceObject, String aFunctionName,
                         String[] aParameterTypes,String[] aParameterDesc,String[] aParameterAnnotation,String aErrorInfo) throws Exception {
    this.name = aOperName;
    this.errorInfo = aErrorInfo;
    this.serviceObject = aServiceObject;
    this.functionName = aFunctionName;
    this.parameterTypes = aParameterTypes;
    this.operDataDesc = aParameterDesc;
    this.operDataAnnotation = aParameterAnnotation;
    this.parameterClasses = new Class[this.parameterTypes.length];
    for(int i=0;i<this.parameterClasses.length;i++){
      this.parameterClasses[i] =ExpressUtil.getJavaClass(this.parameterTypes[i]);
    }
    Class<?> operClass = serviceObject.getClass();
    method = operClass.getMethod(functionName,parameterClasses);
   
  }

  public OperateData executeInner(InstructionSetContext<String,Object> context, OperateData[] list) throws
      Exception {
      if(this.parameterClasses.length != list.length){
        throw new Exception("定义的参数长度与运行期传入的参数长度不一致");
      }
      Object[] parameres = new Object[list.length];
      for(int i=0;i<list.length;i++){
        parameres[i] = list[i].getObject(context);
      }

      Object obj = this.method.invoke(this.serviceObject,ExpressUtil.transferArray(parameres,parameterClasses));
      if(obj != null){
         return new OperateData(obj,obj.getClass());
      }
      return null;
  }
}
