package com.ql.util.express;

import java.math.BigDecimal;

/**
 * �������к�������
 * @author qhlhl2010@gmail.com
 *
 */
public class OperatorOfNumber {
	
	public static void main(String[] args) throws Exception{
		Object o1 = new Integer(1000);
		Object o2 = new Long(3);
		Object o;
		o = OperatorOfNumber.Add.execute(o1,o2);
		System.out.println(o.getClass() + ":" + o);
		o = OperatorOfNumber.Subtract.execute(o1,o2);
		System.out.println(o.getClass() + ":" + o);
		o = OperatorOfNumber.Multiply.execute(o1,o2);
		System.out.println(o.getClass() + ":" + o);
		o = OperatorOfNumber.Divide.execute(o1,o2);
		System.out.println(o.getClass() + ":" + o);
		o = OperatorOfNumber.Modulo.execute(o1,o2);
		System.out.println(o.getClass() + ":" + o);
		
	}
	/**
	 * ������������ת��
	 * @param value
	 * @param type
	 * @return
	 */
	public static Object transfer(Number value,Class<?> type){
		int type1 = getSeq(value.getClass());
		int type2 = getSeq(type);
		if(type1 == type2){
			return value;
		}
		if(type1 < type2){
			if(type2 == 1) return new Byte(value.byteValue()); 
			if(type2 == 2) return new Short(value.shortValue()); 
			if(type2 == 3) return new Integer(value.intValue()); 
			if(type2 == 4) return new Long(value.longValue()); 
			if(type2 == 5) return new Float(value.floatValue()); 
			if(type2 == 6) return new Double(value.doubleValue()); 
		}
       throw new RuntimeException(value.getClass().getName() +" ����ת��Ϊ���ͣ�" + type.getName());
	}
	public static int compareNumber(Number op1, Number op2){
		int type1 = getSeq(op1.getClass());
		int type2 = getSeq(op2.getClass());
		int type = type1 >  type2 ? type1:type2;
		if(type == 1)  {
			byte o1 =	((Number)op1).byteValue();
			byte o2 =  ((Number)op2).byteValue();
			if(o1 == o2) return 0;
			if(o1 < o2) return -1;
			return 1;
		}
		if(type == 2) {
			short o1 =	((Number)op1).shortValue();
			short o2 =  ((Number)op2).shortValue();
			if(o1 == o2) return 0;
			if(o1 < o2) return -1;
			return 1;
		}
		if(type == 3) {
			int o1 =	((Number)op1).intValue();
			int o2 =  ((Number)op2).intValue();
			if(o1 == o2) return 0;
			if(o1 < o2) return -1;
			return 1;
		}
		if(type == 4) {
			long o1 =	((Number)op1).longValue();
			long o2 =  ((Number)op2).longValue();
			if(o1 == o2) return 0;
			if(o1 < o2) return -1;
			return 1;
		}
		if(type == 5) {
			float o1 =	((Number)op1).floatValue();
			float o2 =  ((Number)op2).floatValue();
			if(o1 == o2) return 0;
			if(o1 < o2) return -1;
			return 1;
		}
		if(type == 6){
			double o1 =	((Number)op1).doubleValue();
			double o2 =  ((Number)op2).doubleValue();
			if(o1 == o2) return 0;
			if(o1 < o2) return -1;
			return 1;
		}
		throw new RuntimeException("�Ƚϲ�������:op1=" + op1.toString() +",op2=" + op2.toString());

	}
    public static int getSeq(Class<?> aClass){
    	if(aClass == Byte.class || aClass == byte.class) return 1;
    	if(aClass == Short.class || aClass == byte.class) return 2;
    	if(aClass == Integer.class || aClass == int.class) return 3;
    	if(aClass == Long.class || aClass == long.class) return 4;
    	if(aClass == Float.class || aClass == float.class) return 5;
    	if(aClass == Double.class || aClass == double.class) return 6;
         return -1;
    }
	public static final class Add {
		public static Object execute(Object op1, Object op2) throws Exception {
			if(op1 == null){
				op1 = "null";
			}
			if(op2 == null){
				op2 = "null";
			}
			if (op1 instanceof String || op2 instanceof String) {				
				return op1.toString() + op2.toString();
			}
			if(op1 instanceof Number == false){
				throw new Exception("�������ʹ���:" + op1.getClass().getName() + "����ִ�� \"+\"����");
			}
			if(op2 instanceof Number == false){
				throw new Exception("�������ʹ���:" + op2.getClass().getName() + "����ִ�� \"+\"����");
			}
			int type1 = getSeq(op1.getClass());
			int type2 = getSeq(op2.getClass());
			int type = type1 >  type2 ? type1:type2;
			if(type == 1) return ((Number)op1).byteValue() + ((Number)op2).byteValue();
			if(type == 2) return ((Number)op1).shortValue() + ((Number)op2).shortValue();
			if(type == 3) return ((Number)op1).intValue() + ((Number)op2).intValue();
			if(type == 4) return ((Number)op1).longValue() + ((Number)op2).longValue();
			if(type == 5) return ((Number)op1).floatValue() + ((Number)op2).floatValue();
			if(type == 6) return ((Number)op1).doubleValue() + ((Number)op2).doubleValue();
			throw new Exception("��֧�ֵĶ���ִ����\"+\"����");
		}
	}

	public static final class Subtract{
	 public  static  Object execute(Object op1,Object op2) throws Exception{
			if(op1 instanceof Number == false){
				throw new Exception("�������ʹ���:" + op1.getClass().getName() + "����ִ�� \"-\"����");
			}
			if(op2 instanceof Number == false){
				throw new Exception("�������ʹ���:" + op2.getClass().getName() + "����ִ�� \"-\"����");
			}
			int type1 = getSeq(op1.getClass());
			int type2 = getSeq(op2.getClass());
			int type = type1 >  type2 ? type1:type2;
			if(type == 1) return ((Number)op1).byteValue() - ((Number)op2).byteValue();
			if(type == 2) return ((Number)op1).shortValue() - ((Number)op2).shortValue();
			if(type == 3) return ((Number)op1).intValue() - ((Number)op2).intValue();
			if(type == 4) return ((Number)op1).longValue() - ((Number)op2).longValue();
			if(type == 5) return ((Number)op1).floatValue() - ((Number)op2).floatValue();
			if(type == 6) return ((Number)op1).doubleValue() - ((Number)op2).doubleValue();
			throw new Exception("��֧�ֵĶ���ִ����\"-\"����");
	    }
	}

	public static final class Multiply{

	    public static Object execute(Object op1,Object op2) throws Exception {
			if(op1 instanceof Number == false){
				throw new Exception("�������ʹ���:" + op1.getClass().getName() + "����ִ�� \"*\"����");
			}
			if(op2 instanceof Number == false){
				throw new Exception("�������ʹ���:" + op2.getClass().getName() + "����ִ�� \"*\"����");
			}
			int type1 = getSeq(op1.getClass());
			int type2 = getSeq(op2.getClass());
			int type = type1 >  type2 ? type1:type2;
			if(type == 1) return ((Number)op1).byteValue() * ((Number)op2).byteValue();
			if(type == 2) return ((Number)op1).shortValue() * ((Number)op2).shortValue();
			if(type == 3) return ((Number)op1).intValue() * ((Number)op2).intValue();
			if(type == 4) return ((Number)op1).longValue() * ((Number)op2).longValue();
			if(type == 5) return ((Number)op1).floatValue() * ((Number)op2).floatValue();
			if(type == 6) return ((Number)op1).doubleValue() * ((Number)op2).doubleValue();
			throw new Exception("��֧�ֵĶ���ִ����\"*\"����");
	    }
	}

 public static final class Divide
	{
	   public static Object execute(Object op1,Object op2) throws Exception{
		   if(op1 instanceof Number == false){
				throw new Exception("�������ʹ���:" + op1.getClass().getName() + "����ִ�� \"/\"����");
			}
			if(op2 instanceof Number == false){
				throw new Exception("�������ʹ���:" + op2.getClass().getName() + "����ִ�� \"/\"����");
			}
			int type1 = getSeq(op1.getClass());
			int type2 = getSeq(op2.getClass());
			int type = type1 >  type2 ? type1:type2;
			if(type == 1) return ((Number)op1).byteValue() / ((Number)op2).byteValue();
			if(type == 2) return ((Number)op1).shortValue() / ((Number)op2).shortValue();
			if(type == 3) return ((Number)op1).intValue() / ((Number)op2).intValue();
			if(type == 4) return ((Number)op1).longValue() / ((Number)op2).longValue();
			if(type == 5) return ((Number)op1).floatValue() / ((Number)op2).floatValue();
			if(type == 6) return ((Number)op1).doubleValue() / ((Number)op2).doubleValue();
			throw new Exception("��֧�ֵĶ���ִ����\"/\"����");
	    }
	}

 public static final class Modulo
 {
    public static Object execute(Object op1,Object op2) throws Exception{
		   if(op1 instanceof Number == false){
				throw new Exception("�������ʹ���:" + op1.getClass().getName() + "����ִ�� \"mod\"����");
			}
			if(op2 instanceof Number == false){
				throw new Exception("�������ʹ���:" + op2.getClass().getName() + "����ִ�� \"mod\"����");
			}
			int type1 = getSeq(op1.getClass());
			int type2 = getSeq(op2.getClass());
			int type = type1 >  type2 ? type1:type2;
			if(type == 1) return ((Number)op1).byteValue() % ((Number)op2).byteValue();
			if(type == 2) return ((Number)op1).shortValue() % ((Number)op2).shortValue();
			if(type == 3) return ((Number)op1).intValue() % ((Number)op2).intValue();
			if(type == 4) return ((Number)op1).longValue() % ((Number)op2).longValue();
			throw new Exception("��֧�ֵĶ���ִ����\"mod\"����");
     }
 }
 public static final class Arith {

   private static final int DEF_DIV_SCALE = 10;
   private Arith() {
   }

   /**
    * �ṩ���_�ļӷ��\�㡣
    * @param v1 ���Ӕ�
    * @param v2 �Ӕ�
    * @return �ɂ������ĺ�
    */
   public static double add(double v1, double v2) {
     BigDecimal b1 = new BigDecimal(Double.toString(v1));
     BigDecimal b2 = new BigDecimal(Double.toString(v2));
     return b1.add(b2).doubleValue();
   }

   /**
    * �ṩ���_�Ĝp���\�㡣
    * @param v1 ���p��
    * @param v2 �p��
    * @return �ɂ������Ĳ�
    */
   public static double sub(double v1, double v2) {
     BigDecimal b1 = new BigDecimal(Double.toString(v1));
     BigDecimal b2 = new BigDecimal(Double.toString(v2));
     return b1.subtract(b2).doubleValue();
   }

   /**
    * �ṩ���_�ĳ˷��\�㡣
    * @param v1 ���˔�
    * @param v2 �˔�
    * @return �ɂ������ķe
    */
   public static double mul(double v1, double v2) {
     BigDecimal b1 = new BigDecimal(Double.toString(v1));
     BigDecimal b2 = new BigDecimal(Double.toString(v2));
     return b1.multiply(b2).doubleValue();
   }

   /**
    * �ṩ�����������_�ĳ����\�㣬���l�������M����r�r�����_��
    * С���c����10λԪ������Ĕ����Ē����롣
    * @param v1 ������
    * @param v2 ����
    * @return �ɂ���������
    */
   public static double div(double v1, double v2) {
     return div(v1, v2, DEF_DIV_SCALE);
   }

   /**
    * �ṩ�����������_�ĳ����\�㡣���l�������M����r�r����scale����ָ
    * �����ȣ�����Ĕ����Ē����롣
    * @param v1 ������
    * @param v2 ����
    * @param scale ��ʾ��ʾ��Ҫ���_��С���c�����λ��
    * @return �ɂ���������
    */
   public static double div(double v1, double v2, int scale) {
     if (scale < 0) {
       throw new IllegalArgumentException(
           "The scale must be a positive integer or zero");
     }
     BigDecimal b1 = new BigDecimal(Double.toString(v1));
     BigDecimal b2 = new BigDecimal(Double.toString(v2));
     return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
   }

   /**
    * �ṩ���_��С��λ�Ē�����̎��
    * @param v ��Ҫ�Ē�����Ĕ�λ
    * @param scale С���c�ᱣ���λ
    * @return �Ē�������ĽY��
    */
   public static double round(double v, int scale) {
     if (scale < 0) {
       throw new IllegalArgumentException(
           "The scale must be a positive integer or zero");
     }
     BigDecimal b = new BigDecimal(Double.toString(v));
     BigDecimal one = new BigDecimal("1");
     return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
   }
 }
}