/**
 *
 * <p>Title:Operator </p>
 * <p>Description:���ʽ������������ </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author ǽ��
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
 * �������Ŷ���
 * 
 * @author qhlhl2010@gmail.com
 * 
 */

public abstract class OperatorBase {
	protected String aliasName;

	protected String name;

	protected String errorInfo;
	
	/**
	 * �����˲����Ƿ��ܹ�������
	 */
	protected boolean isCanCache = false;
	/**
	 * ����������
	 */
	protected String[] operDataDesc;
	/**
	 * ����������������
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
		Class<?> targetType = op1.getType(parent);
		Class<?> sourceType = op2.getType(parent);
		if (targetType != null) {
			if (ExpressUtil.isAssignable(targetType, sourceType) == false) {
				if (targetType.isArray()) {
					if (ExpressUtil.isAssignable(targetType.getComponentType(),
							sourceType) == false) {
						throw new Exception("��ֵʱ������ת������"
								+ ExpressUtil.getClassName(sourceType)
								+ " ����ת��Ϊ "
								+ ExpressUtil.getClassName(targetType
										.getComponentType()));
					} else {
						// ʲô������
					}
				} else {
					throw new Exception("��ֵʱ������ת������"
							+ ExpressUtil.getClassName(sourceType) + " ����ת��Ϊ "
							+ ExpressUtil.getClassName(targetType));
				}
			}

		}
		 Object result = op2.getObject(parent);
		 if(op1.getType(parent)!= null){
			 result = ExpressUtil.castObject(result,op1.getType(parent));
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

class OperatorCast extends OperatorBase {
	public OperatorCast(String aName) {
		this.name = aName;
	}

	public OperateData executeInner(InstructionSetContext<String,Object> parent, OperateData[] list) throws Exception {
		Class<?> tmpClass = (Class<?>) list[0].getObject(parent);
		Object castObj = ExpressUtil.castObject(list[1].getObject(parent), tmpClass);
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
			throw new Exception("����Ϊnull,����ִ��������ز���");
		}
		Object tmpObject = list[0].getObject(context);
	    if( tmpObject.getClass().isArray() == false){
			throw new Exception("����:"+ tmpObject.getClass() +"��������,����ִ����ز���" );
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
		throw new Exception("��û��ʵ��");
	}
}
class OperatorCache extends OperatorBase {
	public OperatorCache(String aName) {
		this.name = aName;
	}

	public OperateData executeInner(InstructionSetContext<String,Object> context, OperateData[] list) throws Exception {
		throw new Exception("cache ��ͨ������ָ����ʵ�ֵģ�����֧�ִ˷���");
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
			// ����Ϊ�գ�����ִ�з���
			String msg = "����Ϊ�գ�����ִ�з���:";
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
			if (list[0] instanceof OperateClass) {// ���þ�̬����
				m = ExpressUtil.findMethodWithCache((Class<?>) obj, methodName,
						types, true, true);
			} else {
				m = ExpressUtil.findMethodWithCache(obj.getClass(), methodName,
						types, true, false);
			}
			if(m == null){
				types = new Class[]{ArrayClass};
				if (list[0] instanceof OperateClass) {// ���þ�̬����
					m = ExpressUtil.findMethod((Class<?>) obj, methodName,
							types, true, true);
				} else {
					m = ExpressUtil.findMethod(obj.getClass(), methodName,
							types, true, false);
				}
				objs = new Object[]{objs};				
			}
			if (m == null) {
				StringBuilder  s = new StringBuilder();
				s.append("û���ҵ�" + obj.getClass().getName() + "�ķ�����"
						+ methodName + "(");
				for (int i = 0; i < orgiTypes.length; i++) {
					if (i > 0)
						s.append(",");
					s.append(orgiTypes[i].getName());
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
			obj = OperatorOfNumber.Add.execute(op1, op2);
		} else if (this.getName().equals("-")) {
			obj = OperatorOfNumber.Subtract.execute(op1, op2);
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
				result = OperatorOfNumber.Add.execute(obj, 1);
			} else if (this.getName().equals("--")) {
				result = OperatorOfNumber.Subtract.execute(obj, 1);
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
		double r = OperatorOfNumber.Arith.round(
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
	public OperateData executeInner(InstructionSetContext<String,Object> parent, OperateData[] list) throws Exception {
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
	public OperateData executeInner(InstructionSetContext<String,Object> parent, OperateData[] list) throws Exception {
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
	public OperateData executeInner(InstructionSetContext<String,Object> parent, OperateData[] list) throws Exception {
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
	
	public  OperateData executeInner(InstructionSetContext<String,Object> parent, OperateData[] list) throws Exception {
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
	
	public  OperateData executeInner(InstructionSetContext<String,Object> parent, OperateData[] list) throws Exception {
		throw new Exception("cache ��ͨ������ָ����ʵ�ֵģ�����֧�ִ˷���");
	}

}
