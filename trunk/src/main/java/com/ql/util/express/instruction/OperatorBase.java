/**
 *
 * <p>Title:Operator </p>
 * <p>Description:表达式计算的运算符号 </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author 墙辉
 * @version 1.0
 */

package com.ql.util.express.instruction;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.ql.util.express.ExpressUtil;
import com.ql.util.express.FuncitonCacheManager;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.Operator;
import com.ql.util.express.OperatorOfNumber;

/**
 * 操作符号定义
 * 
 * @author qhlhl2010@gmail.com
 * 
 */

public abstract class OperatorBase {
	protected String aliasName;

	protected String name;

	protected String errorInfo;
	/**
	 * 是否需要高精度计算
	 */
	protected boolean isPrecise = false;
	/**
	 * 决定此操作是否能够被缓存
	 */
	protected boolean isCanCache = false;
	/**
	 * 操作数描述
	 */
	protected String[] operDataDesc;
	/**
	 * 操作数的其它定义
	 */
	protected String[] operDataAnnotation;
	
    public void setIsCanCache(boolean value){
    	this.isCanCache = value;
    }
	public Object[] toObjectList(InstructionSetContext<String,Object> parent, OperateData[] list)
			throws Exception {
		if (list == null) {
			return new Object[0];
		}
		Object[] result = new Object[list.length];
		for (int i = 0; i < list.length; i++) {
			result[i] = list[i].getObject(parent);
		}
		return result;
	}	
	public OperateData execute(InstructionSetContext<String,Object> context,
			OperateData[] list, List<String> errorList) throws Exception {
		OperateData result = null;
		if (context.isStartFunctionCallCache()&& this.isCanCache == true) {
			//缓存处理
			try {
				Object[] tmpList = new Object[list.length];
				for (int i = 0; i < tmpList.length; i++) {
					tmpList[i] = list[i].getObject(context);
				}
				String key = FuncitonCacheManager.genKey(this.getAliasName(),
						tmpList);
				if (context.getFunctionCachManagerWithCreate().containsKey(key)) {
					result = (OperateData) context.getFunctionCachManagerWithCreate().get(key);
				}else{
					result = this.executeInner(context, list);
					context.getFunctionCachManagerWithCreate().put(key, result);
				}
			} finally {
				context.stopStartFunctionCallCache();
			}
		}else{
			result = this.executeInner(context, list);
		}
		//输出错误信息
		if (errorList != null && this.errorInfo != null && result != null) {
			Object obj = result.getObject(context);
			if (    obj != null
					&& obj instanceof Boolean
					&& ((Boolean) obj).booleanValue() == false) {
				String tmpStr = ExpressUtil.replaceString(this.errorInfo,
						toObjectList(context, list));
				if(errorList.contains(tmpStr) == false){
				    errorList.add(tmpStr);
				}
			}
		}
		return result;
	}
    public String toString(){
    	if(this.aliasName != null){
    		return this.aliasName;
    	}else{
    		return this.name;
    	}
    }
	public abstract OperateData executeInner(InstructionSetContext<String,Object> parent, OperateData[] list) throws Exception;

	public String[] getOperDataDesc(){
		return this.operDataDesc;
	}
	public String[] getOperDataAnnotaion(){
		return this.operDataAnnotation;
	}
	public void setName(String aName) {
		this.name = aName;
	}

	public String getName() {
		return this.name;
	}

	public String getAliasName() {
		if(this.aliasName != null){
			return this.aliasName;
		}else{
			return this.name;
		}
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public boolean isPrecise() {
		return isPrecise;
	}
	public void setPrecise(boolean isPrecise) {
		this.isPrecise = isPrecise;
	}
	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}	
}



class OperatorEvaluate extends OperatorBase {
	public OperatorEvaluate(String name) {
		this.name = name;
	}
	public OperatorEvaluate(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	public OperateData executeInner(InstructionSetContext<String,Object> parent, OperateData[] list) throws Exception {
		return executeInner(parent, list[0], list[1]);
	}

	public OperateData executeInner(InstructionSetContext<String,Object> parent,
			OperateData op1, OperateData op2) throws Exception {
		Class<?> targetType = op1.getDefineType();
		Class<?> sourceType = op2.getType(parent);
		if (targetType != null) {
			if (ExpressUtil.isAssignable(targetType, sourceType) == false) {
				if (targetType.isArray()) {
					if (ExpressUtil.isAssignable(targetType.getComponentType(),
							sourceType) == false) {
						throw new Exception("赋值时候，类型转换错误："
								+ ExpressUtil.getClassName(sourceType)
								+ " 不能转换为 "
								+ ExpressUtil.getClassName(targetType
										.getComponentType()));
					} else {
						// 什么都不做
					}
				} else {
					throw new Exception("赋值时候，类型转换错误："
							+ ExpressUtil.getClassName(sourceType) + " 不能转换为 "
							+ ExpressUtil.getClassName(targetType));
				}
			}

		}
		 Object result = op2.getObject(parent);
		 if(targetType != null){
			 result = ExpressUtil.castObject(result,targetType,false);
		 }
		 op1.setObject(parent,result);
		return op1;
	}

}

class OperatorNew extends OperatorBase {
	public OperatorNew(String aName) {
		this.name = aName;
	}

	public OperateData executeInner(InstructionSetContext<String,Object> parent, OperateData[] list) throws Exception {
		Class<?> obj = (Class<?>) list[0].getObject(parent);
		if (obj.isArray()) {
			Class<?> tmpClass = obj;
			int dim = 0;
			while (tmpClass.isArray()) {
				tmpClass = tmpClass.getComponentType();
				dim = dim + 1;
			}
			int[] dimLength = new int[dim];
			for (int index = 0; index < dim; index++) {
				dimLength[index] = ((Number) (list[index + 1].getObject(parent)))
						.intValue();
			}
			return new OperateData(Array.newInstance(tmpClass, dimLength), obj);
		}
		Class<?>[] types = new Class[list.length - 1];
		Object[] objs = new Object[list.length - 1];
		Object tmpObj;
		for (int i = 0; i < types.length; i++) {
			tmpObj = list[i + 1].getObject(parent);
			types[i] = list[i + 1].getType(parent);
			objs[i] = tmpObj;
		}
		Constructor<?> c = ExpressUtil.findConstructor(obj, types);

		if (c == null) {
			// "没有找到" + obj.getName() + "的构造方法："
			StringBuilder  s = new StringBuilder();
			s.append("没有找到" + obj.getName() + "的构造方法：" + obj.getName() + "(");
			for (int i = 0; i < types.length; i++) {
				if (i > 0){
					s.append(",");
				}	
				s.append(types[i].getName());
			}
			s.append(")");
			throw new Exception(s.toString());
		}

		tmpObj = c.newInstance(objs);
		return new OperateData(tmpObj, obj);
	}
}

class OperatorCast extends OperatorBase {
	public OperatorCast(String aName) {
		this.name = aName;
	}

	public OperateData executeInner(InstructionSetContext<String,Object> parent, OperateData[] list) throws Exception {
		Class<?> tmpClass = (Class<?>) list[0].getObject(parent);
		Object castObj = ExpressUtil.castObject(list[1].getObject(parent), tmpClass,true);
		OperateData result = new OperateData(castObj,tmpClass);
		return result;
	}
}
class OperatorArray extends OperatorBase {
	public OperatorArray(String aName) {
		this.name = aName;
	}
	public OperatorArray(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}

	public OperateData executeInner(InstructionSetContext<String,Object> context, OperateData[] list) throws Exception {
		if(list[0] == null || list[0].getObject(context) == null){
			throw new Exception("对象为null,不能执行数组相关操作");
		}
		Object tmpObject = list[0].getObject(context);
	    if( tmpObject.getClass().isArray() == false){
			throw new Exception("对象:"+ tmpObject.getClass() +"不是数组,不能执行相关操作" );
		}
	    int index = ((Number)list[1].getObject(context)).intValue();		
	    OperateData result  = new OperateDataArrayItem((OperateData)list[0],index);
		return result;
	}
}
class OperatorDef extends OperatorBase {
	public OperatorDef(String aName) {
		this.name = aName;
	}
	public OperatorDef(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	
	public OperateData executeInner(InstructionSetContext<String,Object> context, OperateData[] list) throws Exception {
		Class<?> tmpClass = (Class<?>) list[0].getObject(context);
		String varName = (String)list[1].getObject(context);		
		OperateDataLocalVar result = new OperateDataLocalVar(varName,tmpClass);
		context.addSymbol(varName, result);
		return result;
	}
}

class OperatorExportDef extends OperatorBase {
	public OperatorExportDef(String aName) {
		this.name = aName;
	}
	public OperatorExportDef(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}

	public OperateData executeInner(InstructionSetContext<String,Object> context, OperateData[] list) throws Exception {
		Class<?> tmpClass = (Class<?>) list[0].getObject(context);
		String varName = (String)list[1].getObject(context);		
		//OperateDataLocalVar result = new OperateDataLocalVar(varName,tmpClass);
		//context.exportSymbol(varName, result);
		OperateDataAttr result = (OperateDataAttr)context.getSymbol(varName);
		result.type = tmpClass;
		return result;
	}
}

class OperatorAlias extends OperatorBase {
	public OperatorAlias(String aName) {
		this.name = aName;
	}
	public OperatorAlias(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	
	public OperateData executeInner(InstructionSetContext<String,Object> context, OperateData[] list) throws Exception {
		String varName = (String)list[0].getObjectInner(context);	
		OperateDataAttr realAttr = (OperateDataAttr)list[1];
		OperateDataAttr result = new OperateDataAlias(varName,realAttr);
		context.addSymbol(varName, result);
		return result;
	}
}
class OperatorExportAlias extends OperatorBase {
	public OperatorExportAlias(String aName) {
		this.name = aName;
	}
	public OperatorExportAlias(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}

	public OperateData executeInner(InstructionSetContext<String,Object> context, OperateData[] list) throws Exception {
		String varName = (String)list[0].getObjectInner(context);	
		OperateDataAttr realAttr = (OperateDataAttr)list[1];
		OperateDataAttr result = new OperateDataAlias(varName,realAttr);
		context.exportSymbol(varName, result);
		return result;
	}
}
class OperatorMacro extends OperatorBase {
	public OperatorMacro(String aName) {
		this.name = aName;
	}
	public OperatorMacro(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}

	public OperateData executeInner(InstructionSetContext<String,Object> context, OperateData[] list) throws Exception {
		String varName = (String)list[0].getObjectInner(context);	
		OperateDataAttr realAttr = (OperateDataAttr)list[1];
		OperateDataAttr result = new OperateDataAlias(varName,realAttr);
		context.addSymbol(varName, result);
		return result;
	}
}
class OperatorFunction extends OperatorBase {
	public OperatorFunction(String aName) {
		this.name = aName;
	}
	public OperatorFunction(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}

	public OperateData executeInner(InstructionSetContext<String,Object> context, OperateData[] list) throws Exception {
		throw new Exception("还没有实现");
	}
}
class OperatorCache extends OperatorBase {
	public OperatorCache(String aName) {
		this.name = aName;
	}

	public OperateData executeInner(InstructionSetContext<String,Object> context, OperateData[] list) throws Exception {
		throw new Exception("cache 是通过特殊指令来实现的，不能支持此方法");
	}
}

class OperatorMethod extends OperatorBase {
	public OperatorMethod() {
		this.name ="MethodCall";
		this.isCanCache = true;
	}

	static Class<?> ArrayClass = (new Object[]{}).getClass();
	
	public OperateData executeInner(InstructionSetContext<String,Object> parent, OperateData[] list) throws Exception {
		Object obj = list[0].getObject(parent);
		String methodName = list[1].getObject(parent).toString();
		if (obj == null) {
			// 对象为空，不能执行方法
			String msg = "对象为空，不能执行方法:";
			throw new Exception(msg + methodName);
		} else {
			Class<?>[] types = new Class[list.length - 2];
			Class<?>[] orgiTypes = new Class[list.length - 2];
			
			Object[] objs = new Object[list.length - 2];
			Object tmpObj;
			for (int i = 0; i < types.length; i++) {
				tmpObj = list[i + 2].getObject(parent);
				types[i] = list[i + 2].getType(parent);
				if(types[i] == null) {
					types[i] = Object.class;
				}
				orgiTypes[i] = list[i + 2].getType(parent);
				objs[i] = tmpObj;
			}
			Method m = null;
			if (list[0] instanceof OperateClass) {// 调用静态方法
				m = ExpressUtil.findMethodWithCache((Class<?>) obj, methodName,
						types, true, true);
			} else {
				m = ExpressUtil.findMethodWithCache(obj.getClass(), methodName,
						types, true, false);
			}
			if(m == null){
				types = new Class[]{ArrayClass};
				if (list[0] instanceof OperateClass) {// 调用静态方法
					m = ExpressUtil.findMethodWithCache((Class<?>) obj, methodName,
							types, true, true);
				} else {
					m = ExpressUtil.findMethodWithCache(obj.getClass(), methodName,
							types, true, false);
				}
				objs = new Object[]{objs};				
			}
			if (m == null) {
				StringBuilder  s = new StringBuilder();
				s.append("没有找到" + obj.getClass().getName() + "的方法："
						+ methodName + "(");
				for (int i = 0; i < orgiTypes.length; i++) {
					if (i > 0)
						s.append(",");
					s.append(orgiTypes[i].getName());
				}
				s.append(")");
				throw new Exception(s.toString());
			}
			
			if (list[0] instanceof OperateClass) {// 调用静态方法
				boolean oldA = m.isAccessible();
				m.setAccessible(true);
				tmpObj = m.invoke(null,ExpressUtil.transferArray(objs,m.getParameterTypes()));
				m.setAccessible(oldA);
			} else {
				boolean oldA = m.isAccessible();
				m.setAccessible(true);
				tmpObj = m.invoke(obj, ExpressUtil.transferArray(objs,m.getParameterTypes()));
				m.setAccessible(oldA);
			}
			return new OperateData(tmpObj, m.getReturnType());
		}
	}
    public String toString(){
    	return this.name;
    }
}


class OperatorField extends OperatorBase {
	
	public OperatorField() {
		this.name = "FieldCall";
	}

	public OperateData executeInner(InstructionSetContext<String,Object> parent, OperateData[] list) throws Exception {
		Object obj = list[0].getObject(parent);
		String fieldName = list[1].getObject(parent).toString();
		return new OperateDataField(obj,fieldName);
	}
}

   class OperatorAddReduce extends Operator {
	public OperatorAddReduce(String name) {
		this.name = name;
	}
	public OperatorAddReduce(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	public Object executeInner(Object[] list) throws Exception {
		return executeInner(list[0], list[1]);
	}

	public Object executeInner(Object op1,
			Object op2) throws Exception {
		Object obj = null;
		if (this.getName().equals("+")) {
			obj = OperatorOfNumber.add(op1, op2,this.isPrecise);
		} else if (this.getName().equals("-")) {
			obj = OperatorOfNumber.subtract(op1, op2,this.isPrecise);
		}
		return obj;
	}
}
   class OperatorDoubleAddReduce extends OperatorBase {
		public OperatorDoubleAddReduce(String name) {
			this.name = name;
		}

		public OperateData executeInner(InstructionSetContext<String,Object> parent,
				OperateData[] list) throws Exception {
			Object obj = list[0].getObject(parent);
			Object result = null;
			if (this.getName().equals("++")) {
				result = OperatorOfNumber.add(obj, 1,this.isPrecise);
			} else if (this.getName().equals("--")) {
				result = OperatorOfNumber.subtract(obj, 1,this.isPrecise);
			}
			((OperateData)list[0]).setObject(parent, result);
			
			if(result == null){
				return new OperateData(null,null);
			}else{
				return new OperateData(result,ExpressUtil.getSimpleDataType(result.getClass()));
			}
		}
	}

class OperatorRound extends Operator {
	public OperatorRound(String name) {
		this.name = name;
	}

	public Object executeInner(Object[] list) throws Exception {
		return executeInner(list[0], list[1]);
	}

	public Object executeInner(Object op1,Object op2) throws Exception {
		double r = OperatorOfNumber.round(
				((Number) op1).doubleValue(), ((Number) op2).intValue());
		return new Double(r);
	}
}


class OperatorLike extends Operator {
	public OperatorLike(String name) {
		this.name = name;
	}
	public OperatorLike(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	public Object executeInner(Object[] list) throws Exception {
		return executeInner(list[0], list[1]);
	}

	public Object executeInner(Object op1,Object op2) throws Exception {
		boolean result = true;
		String s1 = op1.toString();
		String s2 = op2.toString();
		if (s2.indexOf("%") >= 0) {
			String[] list = split(s2, "%");
			int index = 0;
			for (int i = 0; i < list.length; i++) {
				if (index >= s1.length()) {
					result = false;
					break;
				}
				index = s1.indexOf(list[i], index);
				if (index < 0) {
					result = false;
					break;
				}
				index = index + 1;
			}
		} else if (s1.equals(s2))
			result = true;
		else
			result = false;

		return Boolean.valueOf(result);
	}

	public String[] split(String str, String s) {
		int start = 0;
		int end = -1;
		String tmpStr = "";
		ArrayList<String> list = new ArrayList<String>();
		do {
			end = str.indexOf(s, start);
			if (end < 0)
				tmpStr = str.substring(start);
			else
				tmpStr = str.substring(start, end);
			if (tmpStr.length() > 0)
				list.add(tmpStr);
			start = end + 1;
			if (start >= str.length())
				break;
		} while (end >= 0);
		return (String[]) list.toArray(new String[0]);
	}
}

/**
 * 处理 * /
 **/
class OperatorMultiDiv extends Operator {
	public OperatorMultiDiv(String name) {
		this.name = name;
	}

	public Object executeInner(Object[] list) throws Exception {
		return executeInner( list[0], list[1]);
	}

	public Object executeInner(Object op1,
			Object op2) throws Exception {
		Object obj = null;
		if (this.getName().equals("*"))
			obj = OperatorOfNumber.multiply(op1, op2,this.isPrecise);
		else if (this.getName().equals("/"))
			obj = OperatorOfNumber.divide(op1, op2,this.isPrecise);
		else if (this.getName().equals("%"))
			obj = OperatorOfNumber.modulo(op1, op2);
		else if (this.getName().equals("mod"))
			obj = OperatorOfNumber.modulo(op1, op2);

		return obj;

	}
}

/**
 * 处理 =,==,>,>=,<,<=,!=,<>
 */
class OperatorEqualsLessMore extends Operator {
	public OperatorEqualsLessMore(String aName) {
		this.name = aName;
	}

	public OperatorEqualsLessMore(String aAliasName, String aName,
			String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}

	public Object executeInner(Object[] list) throws Exception {
		return executeInner(list[0], list[1]);
	}

	public Object executeInner(Object op1,Object op2) throws Exception {
		boolean result = executeInner(this.name, op1, op2);
		return result;
	}

	public static boolean executeInner(String opStr, Object obj1, Object obj2)
			throws Exception {

		if (obj1 == null && obj2 == null) {
			if (opStr.equals("==")) {
				return true;
			} else if (opStr.equals("!=") || opStr.equals("<>")) {
				return false;
			} else {
				throw new Exception("两个空操作数不能执行这个操作：" + opStr);
			}
		} else if (obj1 == null || obj2 == null) {
			if (opStr.equals("==")) {
				return false;
			} else if (opStr.equals("!=") || opStr.equals("<>")) {
				return true;
			} else {
				throw new Exception("空操作数不能执行这个操作：" + opStr);
			}
		}

		int i = Operator.compareData(obj1, obj2);
		boolean result = false;
		if (i > 0) {
			if (opStr.equals(">") || opStr.equals(">=") || opStr.equals("!=")
					|| opStr.equals("<>"))
				result = true;
			else
				result = false;
		} else if (i == 0) {
			if (opStr.equals("=") || opStr.equals("==") || opStr.equals(">=")
					|| opStr.equals("<="))
				result = true;
			else
				result = false;
		} else if (i < 0) {
			if (opStr.equals("<") || opStr.equals("<=") || opStr.equals("!=")
					|| opStr.equals("<>"))
				result = true;
			else
				result = false;
		}
		return result;
	}
}

/**
 * 处理 not,! 操作
 */

class OperatorNot extends Operator {
	public OperatorNot(String name) {
		this.name = name;
	}
	public OperatorNot(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	public Object executeInner(Object[] list) throws Exception {
		return executeInner(list[0]);
	}

	public Object executeInner(Object op)
			throws Exception {
		Object result = null;
        if (op == null){
        	throw new Exception("null 不能执行操作：" + this.getAliasName());
        }
		if (Boolean.class.equals(op.getClass()) == true) {
			boolean r = !((Boolean) op).booleanValue();
			result = Boolean.valueOf(r);
		} else {
			//
			String msg = "没有定义类型" + op.getClass().getName() + " 的 " + this.name
					+ "操作";
			throw new Exception(msg);
		}
		return result;
	}
}

/**
 * 处理 And,Or,&&,||操作
 */

class OperatorAnd extends Operator {
	public OperatorAnd(String name) {
		this.name = name;
	}
	public OperatorAnd(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	public Object executeInner(Object[] list) throws Exception {
		return executeInner(list[0], list[1]);
	}

	public Object executeInner(Object op1,
			Object op2) throws Exception {
		boolean result = false;
		Object o1 = op1;
		Object o2 = op2;

		if ((o1 instanceof Boolean) && (o2 instanceof Boolean)) {
			result = ((Boolean) o1).booleanValue()
						&& ((Boolean) o2).booleanValue();
		} else {
			String msg = "没有定义类型" + o1 + "和" + o2 + " 的 " + this.name + "操作";
			throw new Exception(msg);
		}
		return  Boolean.valueOf(result);

	}
}
class OperatorOr extends Operator {
	public OperatorOr(String name) {
		this.name = name;
	}
	public OperatorOr(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	public Object executeInner(Object[] list) throws Exception {
		return executeInner(list[0], list[1]);
	}

	public Object executeInner(Object op1,
			Object op2) throws Exception {
		boolean result = false;
		Object o1 = op1;
		Object o2 = op2;

		if ((o1 instanceof Boolean) && (o2 instanceof Boolean)) {
			result = ((Boolean) o1).booleanValue()
						|| ((Boolean) o2).booleanValue();
		} else {
			String msg = "没有定义类型" + o1 + "和" + o2 + " 的 " + this.name + "操作";
			throw new Exception(msg);
		}
		return  Boolean.valueOf(result);

	}
}
/**
 * 处理 ",","(",")",";"
 */

class OperatorNullOp extends OperatorBase {
	public OperatorNullOp(String name) {
		this.name = name;
	}
	public OperatorNullOp(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	public OperateData executeInner(InstructionSetContext<String,Object> parent, OperateData[] list) throws Exception {
		return executeInner(parent);
	}

	public OperateData executeInner(IExpressContext<String,Object> parent) throws Exception {
		return null;
	}
}

class OperatorReturn extends OperatorBase{
	public OperatorReturn(String name) {
		this.name = name;
	}
	public OperatorReturn(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	public OperateData executeInner(InstructionSetContext<String,Object> parent, OperateData[] list) throws Exception {
		return executeInner(parent);
	}

	public OperateData executeInner(InstructionSetContext<String,Object> parent) throws Exception {
		throw new Exception("return 是通过特殊指令来实现的，不能支持此方法");
	}	
}
class OperatorCall extends OperatorBase{
	public OperatorCall(String name) {
		this.name = name;
	}
	public OperatorCall(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	public OperateData executeInner(InstructionSetContext<String,Object> parent, OperateData[] list) throws Exception {
		throw new Exception("return 是通过特殊指令来实现的，不能支持此方法");
	}	
}

class OperatorBreak extends OperatorBase{
	public OperatorBreak(String name) {
		this.name = name;
	}
	public OperatorBreak(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	public OperateData executeInner(InstructionSetContext<String,Object> parent, OperateData[] list) throws Exception {
		throw new Exception("OperatorBreak 是通过特殊指令来实现的，不能支持此方法");
	}	
}
class OperatorContinue extends OperatorBase{
	public OperatorContinue(String name) {
		this.name = name;
	}
	public OperatorContinue(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	public OperateData executeInner(InstructionSetContext<String,Object> parent, OperateData[] list) throws Exception {
		throw new Exception("OperatorContinue 是通过特殊指令来实现的，不能支持此方法");
	}	
}

class OperatorMinMax extends Operator {
	public OperatorMinMax(String name) {
		this.name = name;
	}

	public Object executeInner(Object[] list) throws Exception {
		if (list.length == 0){
			throw new Exception("操作数异常");
		}
		Object result = list[0];

		for (int i = 1; i < list.length; i++)
			result = executeInner(result, list[i]);
		return result;
	}

	public Object executeInner(Object op1,
			Object op2) throws Exception {
		Object result = null;
		int compareResult = Operator.compareData(op1,op2);
		if (this.name.equals("min")) {
			if (compareResult < 0)
				result = op1;
			else
				result = op2;
		} else if (this.name.equals("max")) {
			if (compareResult < 0)
				result = op2;
			else
				result = op1;
		}
		return result;
	}
}

class OperatorIn extends Operator {
	public OperatorIn(String aName) {
		this.name = aName;
	}

	public OperatorIn(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}

	public Object executeInner(Object[] list) throws Exception {
		Object obj = list[0];
		if (obj == null) {
			// 对象为空，不能执行方法
			String msg = "对象为空，不能执行方法:";
			throw new Exception(msg + this.name);
		} else if (((obj instanceof Number) || (obj instanceof String)) == false) {
			String msg = "对象类型不匹配，只有数字和字符串类型才才能执行 in 操作,当前数据类型是:";
			throw new Exception(msg + obj.getClass().getName());
		} else {
			for (int i = 1; i < list.length; i++) {
				boolean f = OperatorEqualsLessMore.executeInner("==", obj,list[i]);
				// System.out.println(obj + ":" + list[i].getObject(parent) +
				// ":" + f);
				if (f == true) {
					return Boolean.TRUE;
				}
			}
			return Boolean.FALSE;
		}
	}

}

/**
 * if操作
 * @author xuannan
 *
 */
class OperatorIf extends OperatorBase {
	public OperatorIf(String aName) {
		this.name = aName;
	}

	public OperatorIf(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	
	public  OperateData executeInner(InstructionSetContext<String,Object> parent, OperateData[] list) throws Exception {
		if(list.length <2){
			throw new Exception("\"" + this.aliasName + "\"操作至少要两个操作数");
		}
		Object obj = list[0].getObject(parent);
		if (obj == null) {
			String msg ="\"" + this.aliasName + "\"的判断条件不能为空";
			throw new Exception(msg);
		} else if ((obj instanceof Boolean) == false) {
			String msg = "\"" + this.aliasName + "\"的判断条件 必须是 Boolean,不能是：";
			throw new Exception(msg + obj.getClass().getName());
		} else {
			if (((Boolean)obj).booleanValue() == true){
				return list[1];
			}else{
				if(list.length == 3){
					return list[2];
				}
			}
			return null;			
		}
	}
}

class OperatorFor extends OperatorBase {
	public OperatorFor(String aName) {
		this.name = aName;
	}

	public OperatorFor(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	
	public  OperateData executeInner(InstructionSetContext<String,Object> parent, OperateData[] list) throws Exception {
		throw new Exception("cache 是通过特殊指令来实现的，不能支持此方法");
	}

}
