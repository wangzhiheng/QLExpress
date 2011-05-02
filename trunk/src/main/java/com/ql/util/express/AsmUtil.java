package com.ql.util.express;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.ql.util.express.instruction.opdata.OperateClass;

public class AsmUtil {
	public static String TEMP_DIR = "QLExpressTempClass";
	static{
		File parentDir = new File(System.getProperty("user.dir") + "/" + TEMP_DIR);
		if(parentDir.exists()){
			parentDir.delete();
		}
		parentDir.mkdir();
	}
	public static void writeClass(byte[] code, String className) {
		try {
			FileOutputStream fos = new FileOutputStream(TEMP_DIR+"/" + className.replaceAll("\\.","\\_") + ".class");
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
			method.visitLdcInsn(value);
		}else if(value instanceof Number){
			method.newInstance(Type.getType(value.getClass()));
			method.dup();
			method.visitLdcInsn(value.toString());
			method.invokeConstructor(Type.getType(value.getClass()),
					Method.getMethod("void <init>(String)"));
		}else if(value instanceof Character){
			method.visitLdcInsn(value);
			method.invokeStatic(Type.getType(Character.class),Method.getMethod("Character valueOf(char)"));
		}else if(value instanceof Boolean){
			method.visitLdcInsn(value.toString());
			method.invokeStatic(Type.getType(value.getClass()),Method.getMethod("Boolean valueOf(String)"));
		}else if(value instanceof Class){
			Class<?> tempClass = (Class<?>)value;
			if (tempClass.equals(byte.class)) {
				method.getStatic(Type.getType(Byte.class), "TYPE",Type.getType(Class.class));
			} else if (tempClass.equals(char.class)) {
				method.getStatic(Type.getType(Character.class), "TYPE",Type.getType(Class.class));
			} else if (tempClass.equals(short.class)) {
				method.getStatic(Type.getType(Short.class), "TYPE",Type.getType(Class.class));
			} else if (tempClass.equals(int.class)) {
				method.getStatic(Type.getType(Integer.class), "TYPE",Type.getType(Class.class));
			} else if (tempClass.equals(long.class)) {
				method.getStatic(Type.getType(Long.class), "TYPE",Type.getType(Class.class));
			} else if (tempClass.equals(float.class)) {
				method.getStatic(Type.getType(Float.class), "TYPE",Type.getType(Class.class));
			} else if (tempClass.equals(double.class)) {
				method.getStatic(Type.getType(Double.class), "TYPE",Type.getType(Class.class));
			} else {
				method.visitLdcInsn(tempClass.getName());
				method.invokeStatic(Type.getType(Class.class),
						Method.getMethod("Class forName(String)"));
			}
		}else {
			throw new RuntimeException("不支持的数据类型：" + value.getClass().getName());
		}
	}
	public static void transferOperatorData(GeneratorAdapter staticInitialMethod, OperateData value) {
		Class<?> realClass = value.getClass();
		staticInitialMethod.newInstance(Type.getType(realClass));
		staticInitialMethod.dup();
		if(OperateData.class.equals(realClass)){
			AsmUtil.transferCode(staticInitialMethod,value.getObjectInner(null));
			AsmUtil.transferCode(staticInitialMethod,value.type);
			staticInitialMethod.invokeConstructor(
					Type.getType(OperateData.class),
					Method.getMethod("void <init>(Object,Class)"));
		}else if(OperateClass.class.equals(realClass)){
			AsmUtil.transferCode(staticInitialMethod,((OperateClass)value).getName());
			AsmUtil.transferCode(staticInitialMethod,((OperateClass)value).getVarClass());
			staticInitialMethod.invokeConstructor(
					Type.getType(realClass),
					Method.getMethod("void <init>(String,Class)"));			
		}else{
			throw new RuntimeException("不支持的数据类型：" +realClass.getName());
		}
		
	}
}
