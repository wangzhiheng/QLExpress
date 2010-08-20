/**
 *
 * <p>Title:Operator </p>
 * <p>Description:表达式计算的运算符号 </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author 墙辉
 * @version 1.0
 */

package com.ql.util.express;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 操作符号定义
 * 
 * @author qhlhl2010@gmail.com
 * 
 */
@SuppressWarnings("unchecked")

abstract class OperatorBase {
	protected String aliasName;

	protected String name;

	protected String errorInfo;

	public Object[] toObjectList(IExpressContext parent, OperateData[] list)
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
	public OperateData execute(IExpressContext parent, OperateData[] list,
			List errorList) throws Exception {
		OperateData result = this.executeInner(parent, list);
		if (errorList != null && this.errorInfo != null && result != null) {
			Object obj = result.getObject(parent);
			if (    obj != null
					&& obj instanceof Boolean
					&& ((Boolean) obj).booleanValue() == false) {
				String tmpStr = ExpressUtil.replaceString(this.errorInfo,
						toObjectList(parent, list));
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
	public abstract OperateData executeInner(IExpressContext parent, OperateData[] list) throws Exception;

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

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}	
}

public abstract class Operator extends  OperatorBase{
	@SuppressWarnings("unchecked")
	public OperateData executeInner(IExpressContext context, OperateData[] list) throws Exception{
		Object[] parameters = new Object[list.length];
		for(int i = 0;i <list.length;i++){			
			parameters[i] = list[i].getObject(context);
		}
		Object result = this.executeInner(parameters);
		if(result != null && result.getClass().equals(OperateData.class)){
			throw new Exception("操作符号定义的返回类型错误：" + this.getAliasName());
		}
		if(result == null){
			return new OperateData(null,null);
		}else{
			return new OperateData(result,ExpressUtil.getSimpleDataType(result.getClass()));
		}
	}
	public abstract Object executeInner(Object[] list) throws Exception;
	
}

@SuppressWarnings("unchecked")
class OperatorEvaluate extends OperatorBase {
	public OperatorEvaluate(String name) {
		this.name = name;
	}
	public OperatorEvaluate(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	public OperateData executeInner(IExpressContext parent, OperateData[] list) throws Exception {
		return executeInner(parent, list[0], list[1]);
	}

	public OperateData executeInner(IExpressContext parent, OperateData op1,
			OperateData op2) throws Exception {
		if(op1 instanceof OperateDataAttr){
		  ((OperateDataAttr)op1).setObject(parent, op2.getObject(parent));
		}else{
		  throw new Exception("只有属性才能在表达式设置值");	
		}
		return op2;
	}

}

/**
 * 
 * <p>
 * Title: OperatorAdd
 * </p>
 * <p>
 * Description:处理 + 运算符号
 * </p>
 * <p>
 * Copyright: Copyright (c) 2001
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author qhlhl2010@gmail.com
 * @version 1.0
 */
@SuppressWarnings("unchecked")
class OperatorNew extends OperatorBase {
	public OperatorNew(String aName) {
		this.name = aName;
	}

	public OperateData executeInner(IExpressContext parent, OperateData[] list) throws Exception {
		Class obj = (Class) list[0].getObject(parent);
		Class[] types = new Class[list.length - 1];
		Object[] objs = new Object[list.length - 1];
		Object tmpObj;
		for (int i = 0; i < types.length; i++) {
			tmpObj = list[i + 1].getObject(parent);
			types[i] = list[i + 1].getType(parent);
			objs[i] = tmpObj;
		}
		Constructor c = ExpressUtil.findConstructor(obj, types);

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

@SuppressWarnings("unchecked")
class OperatorCast extends OperatorBase {
	public OperatorCast(String aName) {
		this.name = aName;
	}

	public OperateData executeInner(IExpressContext parent, OperateData[] list) throws Exception {
		Class tmpClass = (Class) list[0].getObject(parent);
		OperateData result = new OperateData(list[1].getObject(parent),
				tmpClass);
		return result;
	}
}

@SuppressWarnings("unchecked")
class OperatorMethod extends OperatorBase {
	String methodName;

	public OperatorMethod(String aName, String aMethodName) {
		this.name = aName;
		this.methodName = aMethodName;
	}

	public OperateData executeInner(IExpressContext parent, OperateData[] list) throws Exception {
		Object obj = list[0].getObject(parent);
		if (obj == null) {
			// 对象为空，不能执行方法
			String msg = "对象为空，不能执行方法:";
			throw new Exception(msg + this.methodName);
		} else {
			Class[] types = new Class[list.length - 1];
			Object[] objs = new Object[list.length - 1];
			Object tmpObj;
			for (int i = 0; i < types.length; i++) {
				tmpObj = list[i + 1].getObject(parent);
				types[i] = list[i + 1].getType(parent);
				objs[i] = tmpObj;
			}
			Method m = null;
			if (list[0] instanceof OperatorClass) {// 调用静态方法
				m = ExpressUtil.findMethod((Class) obj, this.methodName,
						types, true, true);
			} else {
				m = ExpressUtil.findMethod(obj.getClass(), this.methodName,
						types, true, false);
			}

			if (m == null) {
				StringBuilder  s = new StringBuilder();
				s.append("没有找到" + obj.getClass().getName() + "的方法："
						+ this.methodName + "(");
				for (int i = 0; i < types.length; i++) {
					if (i > 0)
						s.append(",");
					s.append(types[i].getName());
				}
				s.append(")");
				throw new Exception(s.toString());
			}

			if (list[0] instanceof OperatorClass) {// 调用静态方法
				tmpObj = m.invoke(null, objs);
			} else {
				tmpObj = m.invoke(obj, objs);
			}
			return new OperateData(tmpObj, m.getReturnType());
		}
	}
    public String toString(){
    	return this.name + " " + this.methodName + "()";
    }
}

@SuppressWarnings("unchecked")
class OperatorField extends OperatorBase {
	String fieldName;

	public OperatorField(String aName, String aFieldName) {
		this.name = aName;
		this.fieldName = aFieldName;
	}

	public OperateData executeInner(IExpressContext parent, OperateData[] list) throws Exception {
		Object obj = list[0].getObject(parent);
		if (list[0] instanceof OperatorClass) {
			Field f = ((Class) obj).getField(this.fieldName);
			return new OperateData(f.get(null), f.getType());
		} else {
			if (obj == null) {
				String msg = "对象为空，不能获取属性：";
				throw new Exception(msg + this.fieldName);
			} else {
				Field f = obj.getClass().getField(this.fieldName);
				return new OperateData(f.get(obj), f.getType());
			}
		}
	}
}

class OperatorAddReduce extends Operator {
	public OperatorAddReduce(String name) {
		this.name = name;
	}
	
	public Object executeInner(Object[] list) throws Exception {
		return executeInner(list[0], list[1]);
	}

	public Object executeInner(Object op1,
			Object op2) throws Exception {
		Object obj = null;
		if (this.getName() == "+") {
			obj = OperatorOfNumber.Add.execute(op1, op2);
		} else if (this.getName() == "-") {
			obj = OperatorOfNumber.Subtract.execute(op1, op2);
		}
		return obj;
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
		double r = OperatorOfNumber.Arith.round(
				((Number) op1).doubleValue(), ((Number) op2).intValue());
		return new Double(r);
	}
}

@SuppressWarnings("unchecked")
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
		ArrayList list = new ArrayList();
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
		if (this.getName() == "*")
			obj = OperatorOfNumber.Multiply.execute(op1, op2);
		else if (this.getName() == "/")
			obj = OperatorOfNumber.Divide.execute(op1, op2);
		else if (this.getName() == "%")
			obj = OperatorOfNumber.Modulo.execute(op1, op2);

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

		int i = OperatorManager.compareData(obj1, obj2);
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
@SuppressWarnings("unchecked")
class OperatorNullOp extends OperatorBase {
	public OperatorNullOp(String name) {
		this.name = name;
	}
	public OperatorNullOp(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	public OperateData executeInner(IExpressContext parent, OperateData[] list) throws Exception {
		return executeInner(parent);
	}

	public OperateData executeInner(IExpressContext parent) throws Exception {
		return null;
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
		int compareResult = OperatorManager.compareData(op1,op2);
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
	
	@SuppressWarnings("unchecked")
	public  OperateData executeInner(IExpressContext parent, OperateData[] list) throws Exception {
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
