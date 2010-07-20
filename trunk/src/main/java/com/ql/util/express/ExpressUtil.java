package com.ql.util.express;

import java.util.Vector;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表达式工具类
 * 
 * @author qhlhl2010@gmail.com
 * 
 */
@SuppressWarnings("unchecked")
public class ExpressUtil {
	public static final String DT_STRING = "String";
	public static final String DT_SHORT = "Short";
	public static final String DT_INTEGER = "Integer";
	public static final String DT_LONG = "Long";
	public static final String DT_DOUBLE = "Double";
	public static final String DT_FLOAT = "Float";
	public static final String DT_BYTE = "Byte";
	public static final String DT_CHAR = "Char";
	public static final String DT_BOOLEAN = "Boolean";
	public static final String DT_DATE = "Date";
	public static final String DT_TIME = "Time";
	public static final String DT_DATETIME = "DateTime";
	public static final String DT_OBJECT = "Object";

	public static final String DT_short = "short";
	public static final String DT_int = "int";
	public static final String DT_long = "long";
	public static final String DT_double = "double";
	public static final String DT_float = "float";
	public static final String DT_byte = "byte";
	public static final String DT_char = "char";
	public static final String DT_boolean = "boolean";

	public static Class getSimpleDataType(Class aClass) {
		if (Integer.class.equals(aClass))
			return Integer.TYPE;
		if (Short.class.equals(aClass))
			return Short.TYPE;
		if (Long.class.equals(aClass))
			return Long.TYPE;
		if (Double.class.equals(aClass))
			return Double.TYPE;
		if (Float.class.equals(aClass))
			return Float.TYPE;
		if (Byte.class.equals(aClass))
			return Byte.TYPE;
		if (Character.class.equals(aClass))
			return Character.TYPE;
		if (Boolean.class.equals(aClass))
			return Boolean.TYPE;
		return aClass;
	}

	public static boolean isAssignable(Class lhsType, Class rhsType) {
		if (lhsType == null)
			return false;
		if (rhsType == null)
			return !lhsType.isPrimitive();

		if (lhsType.isPrimitive() && rhsType.isPrimitive()) {
			if (lhsType == rhsType)
				return true;

			if ((rhsType == Byte.TYPE)
					&& (lhsType == Short.TYPE || lhsType == Integer.TYPE
							|| lhsType == Long.TYPE || lhsType == Float.TYPE || lhsType == Double.TYPE))
				return true;

			if ((rhsType == Short.TYPE)
					&& (lhsType == Integer.TYPE || lhsType == Long.TYPE
							|| lhsType == Float.TYPE || lhsType == Double.TYPE))
				return true;

			if ((rhsType == Character.TYPE)
					&& (lhsType == Integer.TYPE || lhsType == Long.TYPE
							|| lhsType == Float.TYPE || lhsType == Double.TYPE))
				return true;

			if ((rhsType == Integer.TYPE)
					&& (lhsType == Long.TYPE || lhsType == Float.TYPE || lhsType == Double.TYPE))
				return true;

			if ((rhsType == Long.TYPE)
					&& (lhsType == Float.TYPE || lhsType == Double.TYPE))
				return true;

			if ((rhsType == Float.TYPE) && (lhsType == Double.TYPE))
				return true;
		} else if (lhsType.isAssignableFrom(rhsType))
			return true;

		return false;
	}

	public static boolean isSignatureAssignable(Class[] from, Class[] to) {
		for (int i = 0; i < from.length; i++)
			if (!isAssignable(to[i], from[i]))
				return false;
		return true;
	}

	public static int findMostSpecificSignature(Class[] idealMatch,
			Class[][] candidates) {
		Class[] bestMatch = null;
		int bestMatchIndex = -1;

		for (int i = candidates.length - 1; i >= 0; i--) {// 先从基类开始查找 墙辉
			Class[] targetMatch = candidates[i];
			if (ExpressUtil.isSignatureAssignable(idealMatch, targetMatch)
					&& ((bestMatch == null) || ExpressUtil
							.isSignatureAssignable(targetMatch, bestMatch))) {
				bestMatch = targetMatch;
				bestMatchIndex = i;
			}
		}

		if (bestMatch != null)
			return bestMatchIndex;
		else
			return -1;
	}

	public static Method findMethod(Class baseClass, String methodName,
			Class[] types, boolean publicOnly, boolean isStatic) {

		Vector candidates = gatherMethodsRecursive(baseClass, methodName,
				types.length, publicOnly, isStatic, null /* candidates */);
		Method method = findMostSpecificMethod(types, (Method[]) candidates
				.toArray(new Method[0]));

		return method;
	}

	public static Constructor findConstructor(Class baseClass, Class[] types) {
		Constructor[] constructors = baseClass.getConstructors();
		List listClass = new ArrayList();
		List<Constructor> constructorList = new ArrayList();
		for (int i = 0; i < constructors.length; i++) {
			if (constructors[i].getParameterTypes().length == types.length) {
				listClass.add(constructors[i].getParameterTypes());
				constructorList.add(constructors[i]);
			}
		}
        
		int match = findMostSpecificSignature(types, (Class[][]) listClass
				.toArray(new Class[0][]));
		return match == -1 ? null : constructorList.get(match);
	}

	public static Method findMostSpecificMethod(Class[] idealMatch,
			Method[] methods) {
		Class[][] candidateSigs = new Class[methods.length][];
		for (int i = 0; i < methods.length; i++)
			candidateSigs[i] = methods[i].getParameterTypes();

		int match = findMostSpecificSignature(idealMatch, candidateSigs);
		return match == -1 ? null : methods[match];

	}

	private static Vector gatherMethodsRecursive(Class baseClass,
			String methodName, int numArgs, boolean publicOnly,
			boolean isStatic, Vector candidates) {
		if (candidates == null)
			candidates = new Vector();

		addCandidates(baseClass.getDeclaredMethods(), methodName, numArgs,
				publicOnly, isStatic, candidates);

		Class[] intfs = baseClass.getInterfaces();
		for (int i = 0; i < intfs.length; i++)
			gatherMethodsRecursive(intfs[i], methodName, numArgs, publicOnly,
					isStatic, candidates);

		Class superclass = baseClass.getSuperclass();
		if (superclass != null)
			gatherMethodsRecursive(superclass, methodName, numArgs, publicOnly,
					isStatic, candidates);

		return candidates;
	}

	private static Vector addCandidates(Method[] methods, String methodName,
			int numArgs, boolean publicOnly, boolean isStatic, Vector candidates) {
		for (int i = 0; i < methods.length; i++) {
			Method m = methods[i];
			if (m.getName().equals(methodName)
					&& (m.getParameterTypes().length == numArgs)
					&& (publicOnly == false || isPublic(m)
							&& (isStatic == false || isStatic(m))))
				candidates.add(m);
		}
		return candidates;
	}

	public static boolean isPublic(Class c) {
		return Modifier.isPublic(c.getModifiers());
	}

	public static boolean isPublic(Method m) {
		return Modifier.isPublic(m.getModifiers());
	}

	public static boolean isStatic(Method m) {
		return Modifier.isStatic(m.getModifiers());
	}

	public static Class getJavaClass(String type) {
		int index = type.indexOf("[]");
		if (index < 0)
			return getJavaClassInner(type);

		String arrayString = "[";
		String baseType = type.substring(0, index);
		while ((index = type.indexOf("[]", index + 2)) >= 0) {
			arrayString = arrayString + "[";
		}
		Class baseClass = getJavaClassInner(baseType);

		try {
			String baseName = "";
			if (baseClass.isPrimitive() == false) {
				return loadClass(arrayString + "L" + baseClass.getName() + ";");
			} else {
				if (baseClass.equals(boolean.class)) {
					baseName = "Z";
				} else if (baseClass.equals(byte.class)) {
					baseName = "B";
				} else if (baseClass.equals(char.class)) {
					baseName = "C";
				} else if (baseClass.equals(double.class)) {
					baseName = "D";
				} else if (baseClass.equals(float.class)) {
					baseName = "F";
				} else if (baseClass.equals(int.class)) {
					baseName = "I";
				} else if (baseClass.equals(long.class)) {
					baseName = "J";
				} else if (baseClass.equals(short.class)) {
					baseName = "S";
				}
				return loadClass(arrayString + baseName);
			}
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}

	}

	public static Class getJavaClassInner(String type) {

		if (type.equals(DT_STRING))
			return String.class;
		if (type.equals(DT_SHORT))
			return Short.class;
		if (type.equals(DT_INTEGER))
			return Integer.class;
		if (type.equals(DT_LONG))
			return Long.class;
		if (type.equals(DT_DOUBLE))
			return Double.class;
		if (type.equals(DT_FLOAT))
			return Float.class;
		if (type.equals(DT_BYTE))
			return Byte.class;
		if (type.equals(DT_CHAR) || type.equals("Character"))
			return Character.class;
		if (type.equals(DT_BOOLEAN))
			return Boolean.class;
		if (type.equals(DT_DATE))
			return java.sql.Date.class;
		if (type.equals(DT_TIME))
			return java.sql.Time.class;
		if (type.equals(DT_DATETIME))
			return java.sql.Timestamp.class;
		if (type.equals(DT_OBJECT))
			return Object.class;
		if (type.equals(DT_short))
			return short.class;
		if (type.equals(DT_int))
			return int.class;
		if (type.equals(DT_long))
			return long.class;
		if (type.equals(DT_double))
			return double.class;
		if (type.equals(DT_float))
			return float.class;
		if (type.equals(DT_byte))
			return byte.class;
		if (type.equals(DT_char))
			return char.class;
		if (type.equals(DT_boolean))
			return boolean.class;
		try {
			return loadClass(type);
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static Class loadClass(String name) throws ClassNotFoundException {
		return Class.forName(name);
	}

	/**
	 * 替换字符串中的参数 replaceString("$1强化$2实施$2",new String[]{"qq","ff"})
	 * ="qq 强化 ff 实施 ff"
	 * 
	 * @param str
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	public static String replaceString(String str, Object[] parameters)
			throws Exception {
		if (str == null || parameters == null || parameters.length == 0) {
			return str;
		}
		Pattern p = Pattern.compile("\\$\\d+");
		Matcher m = p.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			int index = Integer.parseInt(m.group().substring(1)) - 1;
			if (index < 0 || index >= parameters.length) {
				throw new Exception("设置的参数位置$" + (index + 1) + "超过了范围 "
						+ parameters.length);
			}
			m.appendReplacement(sb, " " + parameters[index].toString() + " ");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		System.out.println(replaceString("$1强化$2实施$2", new String[] { "qq",
				"ff" }));
	}

}
