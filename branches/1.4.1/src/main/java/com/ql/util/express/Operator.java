/**
 *
 * <p>Title:Operator </p>
 * <p>Description:���ʽ������������ </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author ǽ��
 * @version 1.0
 */

package com.ql.util.express;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * �������Ŷ���
 * 
 * @author qhlhl2010@gmail.com
 * 
 */
@SuppressWarnings("unchecked")

abstract class OperatorBase {
	protected String aliasName;

	protected String name;

	protected String errorInfo;
	
	/**
	 * �����˲����Ƿ��ܹ�������
	 */
	protected boolean isCanCache = false;
    public void setIsCanCache(boolean value){
    	this.isCanCache = value;
    }
	public Object[] toObjectList(InstructionSetContext parent, OperateData[] list)
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
	public OperateData execute(InstructionSetContext context,
			OperateData[] list, List errorList) throws Exception {
		OperateData result = null;
		if (context.isStartFunctionCallCache()&& this.isCanCache == true) {
			//���洦��
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
		//���������Ϣ
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
	public abstract OperateData executeInner(InstructionSetContext parent, OperateData[] list) throws Exception;

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
	public OperateData executeInner(InstructionSetContext context, OperateData[] list) throws Exception{
		Object[] parameters = new Object[list.length];
		for(int i = 0;i <list.length;i++){			
			parameters[i] = list[i].getObject(context);
		}
		Object result = this.executeInner(parameters);
		if(result != null && result.getClass().equals(OperateData.class)){
			throw new Exception("�������Ŷ���ķ������ʹ���" + this.getAliasName());
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
	public OperateData executeInner(InstructionSetContext parent, OperateData[] list) throws Exception {
		return executeInner(parent, list[0], list[1]);
	}

	public OperateData executeInner(InstructionSetContext parent,
			OperateData op1, OperateData op2) throws Exception {
		 op1.setObject(parent, op2.getObject(parent));
		return op2;
	}

}

/**
 * 
 * <p>
 * Title: OperatorAdd
 * </p>
 * <p>
 * Description:���� + �������
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

	public OperateData executeInner(InstructionSetContext parent, OperateData[] list) throws Exception {
		Class obj = (Class) list[0].getObject(parent);
		if (obj.isArray()) {
			Class tmpClass = obj;
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
			// "û���ҵ�" + obj.getName() + "�Ĺ��췽����"
			StringBuilder  s = new StringBuilder();
			s.append("û���ҵ�" + obj.getName() + "�Ĺ��췽����" + obj.getName() + "(");
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

	public OperateData executeInner(InstructionSetContext parent, OperateData[] list) throws Exception {
		Class tmpClass = (Class) list[0].getObject(parent);
		OperateData result = new OperateData(list[1].getObject(parent),
				tmpClass);
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
	@SuppressWarnings("unchecked")
	public OperateData executeInner(InstructionSetContext context, OperateData[] list) throws Exception {
		if(list[0] == null || list[0].getObject(context) == null){
			throw new Exception("����Ϊnull,����ִ��������ز���");
		}
		Object tmpObject = list[0].getObject(context);
	    if( tmpObject.getClass().isArray() == false){
			throw new Exception("����:"+ tmpObject.getClass() +"��������,����ִ����ز���" );
		}
	    int index = ((Number)list[1].getObject(context)).intValue();		
	    OperateData result  = new OperateDataArrayItem((OperateDataAttr)list[0],index);
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
	@SuppressWarnings("unchecked")
	public OperateData executeInner(InstructionSetContext context, OperateData[] list) throws Exception {
		Class tmpClass = (Class) list[0].getObject(context);
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
	@SuppressWarnings("unchecked")
	public OperateData executeInner(InstructionSetContext context, OperateData[] list) throws Exception {
		Class tmpClass = (Class) list[0].getObject(context);
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
	@SuppressWarnings("unchecked")
	public OperateData executeInner(InstructionSetContext context, OperateData[] list) throws Exception {
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
	@SuppressWarnings("unchecked")
	public OperateData executeInner(InstructionSetContext context, OperateData[] list) throws Exception {
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
	@SuppressWarnings("unchecked")
	public OperateData executeInner(InstructionSetContext context, OperateData[] list) throws Exception {
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
	@SuppressWarnings("unchecked")
	public OperateData executeInner(InstructionSetContext context, OperateData[] list) throws Exception {
		throw new Exception("��û��ʵ��");
	}
}
class OperatorCache extends OperatorBase {
	public OperatorCache(String aName) {
		this.name = aName;
	}

	@SuppressWarnings("unchecked")
	public OperateData executeInner(InstructionSetContext context, OperateData[] list) throws Exception {
		throw new Exception("cache ��ͨ������ָ����ʵ�ֵģ�����֧�ִ˷���");
	}
}
@SuppressWarnings("unchecked")
class OperatorMethod extends OperatorBase {
	String methodName;

	public OperatorMethod(String aName, String aMethodName) {
		this.name = aName;
		this.methodName = aMethodName;
		this.isCanCache = true;
	}

	public OperateData executeInner(InstructionSetContext parent, OperateData[] list) throws Exception {
		Object obj = list[0].getObject(parent);
		if (obj == null) {
			// ����Ϊ�գ�����ִ�з���
			String msg = "����Ϊ�գ�����ִ�з���:";
			throw new Exception(msg + this.methodName);
		} else {
			Class<?>[] types = new Class[list.length - 1];
			Object[] objs = new Object[list.length - 1];
			Object tmpObj;
			for (int i = 0; i < types.length; i++) {
				tmpObj = list[i + 1].getObject(parent);
				types[i] = list[i + 1].getType(parent);
				objs[i] = tmpObj;
			}
			Method m = null;
			if (list[0] instanceof OperateClass) {// ���þ�̬����
				m = ExpressUtil.findMethod((Class) obj, this.methodName,
						types, true, true);
			} else {
				m = ExpressUtil.findMethod(obj.getClass(), this.methodName,
						types, true, false);
			}

			if (m == null) {
				StringBuilder  s = new StringBuilder();
				s.append("û���ҵ�" + obj.getClass().getName() + "�ķ�����"
						+ this.methodName + "(");
				for (int i = 0; i < types.length; i++) {
					if (i > 0)
						s.append(",");
					s.append(types[i].getName());
				}
				s.append(")");
				throw new Exception(s.toString());
			}

			if (list[0] instanceof OperateClass) {// ���þ�̬����
				tmpObj = m.invoke(null,ExpressUtil.transferArray(objs,m.getParameterTypes()));
			} else {
				tmpObj = m.invoke(obj, ExpressUtil.transferArray(objs,m.getParameterTypes()));
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

	public OperateData executeInner(InstructionSetContext parent, OperateData[] list) throws Exception {
		Object obj = list[0].getObject(parent);
		return new OperateDataField(obj,this.fieldName);
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
 * ���� * /
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
			obj = OperatorOfNumber.Multiply.execute(op1, op2);
		else if (this.getName().equals("/"))
			obj = OperatorOfNumber.Divide.execute(op1, op2);
		else if (this.getName().equals("%"))
			obj = OperatorOfNumber.Modulo.execute(op1, op2);
		else if (this.getName().equals("mod"))
			obj = OperatorOfNumber.Modulo.execute(op1, op2);

		return obj;

	}
}

/**
 * ���� =,==,>,>=,<,<=,!=,<>
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
				throw new Exception("�����ղ���������ִ�����������" + opStr);
			}
		} else if (obj1 == null || obj2 == null) {
			if (opStr.equals("==")) {
				return false;
			} else if (opStr.equals("!=") || opStr.equals("<>")) {
				return true;
			} else {
				throw new Exception("�ղ���������ִ�����������" + opStr);
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
 * ���� not,! ����
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
        	throw new Exception("null ����ִ�в�����" + this.getAliasName());
        }
		if (Boolean.class.equals(op.getClass()) == true) {
			boolean r = !((Boolean) op).booleanValue();
			result = Boolean.valueOf(r);
		} else {
			//
			String msg = "û�ж�������" + op.getClass().getName() + " �� " + this.name
					+ "����";
			throw new Exception(msg);
		}
		return result;
	}
}

/**
 * ���� And,Or,&&,||����
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
			String msg = "û�ж�������" + o1 + "��" + o2 + " �� " + this.name + "����";
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
			String msg = "û�ж�������" + o1 + "��" + o2 + " �� " + this.name + "����";
			throw new Exception(msg);
		}
		return  Boolean.valueOf(result);

	}
}
/**
 * ���� ",","(",")",";"
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
	public OperateData executeInner(InstructionSetContext parent, OperateData[] list) throws Exception {
		return executeInner(parent);
	}

	public OperateData executeInner(IExpressContext parent) throws Exception {
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
	public OperateData executeInner(InstructionSetContext parent, OperateData[] list) throws Exception {
		return executeInner(parent);
	}

	public OperateData executeInner(InstructionSetContext parent) throws Exception {
		throw new Exception("return ��ͨ������ָ����ʵ�ֵģ�����֧�ִ˷���");
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
	public OperateData executeInner(InstructionSetContext parent, OperateData[] list) throws Exception {
		throw new Exception("return ��ͨ������ָ����ʵ�ֵģ�����֧�ִ˷���");
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
	public OperateData executeInner(InstructionSetContext parent, OperateData[] list) throws Exception {
		throw new Exception("OperatorBreak ��ͨ������ָ����ʵ�ֵģ�����֧�ִ˷���");
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
	public OperateData executeInner(InstructionSetContext parent, OperateData[] list) throws Exception {
		throw new Exception("OperatorContinue ��ͨ������ָ����ʵ�ֵģ�����֧�ִ˷���");
	}	
}

class OperatorMinMax extends Operator {
	public OperatorMinMax(String name) {
		this.name = name;
	}

	public Object executeInner(Object[] list) throws Exception {
		if (list.length == 0){
			throw new Exception("�������쳣");
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
			// ����Ϊ�գ�����ִ�з���
			String msg = "����Ϊ�գ�����ִ�з���:";
			throw new Exception(msg + this.name);
		} else if (((obj instanceof Number) || (obj instanceof String)) == false) {
			String msg = "�������Ͳ�ƥ�䣬ֻ�����ֺ��ַ������ͲŲ���ִ�� in ����,��ǰ����������:";
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
 * if����
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
	public  OperateData executeInner(InstructionSetContext parent, OperateData[] list) throws Exception {
		if(list.length <2){
			throw new Exception("\"" + this.aliasName + "\"��������Ҫ����������");
		}
		Object obj = list[0].getObject(parent);
		if (obj == null) {
			String msg ="\"" + this.aliasName + "\"���ж���������Ϊ��";
			throw new Exception(msg);
		} else if ((obj instanceof Boolean) == false) {
			String msg = "\"" + this.aliasName + "\"���ж����� ������ Boolean,�����ǣ�";
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
	
	@SuppressWarnings("unchecked")
	public  OperateData executeInner(InstructionSetContext parent, OperateData[] list) throws Exception {
		throw new Exception("cache ��ͨ������ָ����ʵ�ֵģ�����֧�ִ˷���");
	}

}
