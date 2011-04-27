package com.ql.util.express;

import java.io.FileOutputStream;
import java.io.IOException;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

public class AsmUtil {
	public static void writeClass(byte[] code, String className) {
		try {
			FileOutputStream fos = new FileOutputStream(className + ".class");
			fos.write(code);
			fos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
    public static String getInnerClassDesc(Class<?> aClass){
    	return "L" +  aClass.getName().replaceAll("\\.","\\/") +";";
    }
    public static String getInnerClassName(Class<?> aClass){
    	return aClass.getName().replaceAll("\\.","\\/");
    }
	public static void transferCode(GeneratorAdapter method, Object value) {
		if (value instanceof String) {
			method.visitLdcInsn((String)value);
		}else if(value instanceof Number){
			method.newInstance(Type.getType(value.getClass()));
			method.dup();
			method.visitLdcInsn(value.toString());
			method.invokeConstructor(Type.getType(value.getClass()),
					Method.getMethod("void <init>(String)"));
		}else if(value instanceof Class){
			method.visitLdcInsn(((Class<?>)value).getName());
			method.invokeStatic(Type.getType(Class.class),Method.getMethod("Class forName(String)"));
		}else {		
			throw new RuntimeException("不支持的数据类型：" + value.getClass().getName());
		}
	}
}
