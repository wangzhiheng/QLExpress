package com.ql.util.express.test;

public class BeanExample {
	public String name = "qhlhl2010@gmail.com";
	public BeanExampleChild child = new BeanExampleChild();
	public BeanExample() {
	}

	public BeanExample(String aName) {
		name = aName;
	}
	public String testLong(long i){
		
		return "toString:" + i;
	}
	public String unionName(String otherName) {
		System.out.println(" execute unionName("+ otherName+ ") .....");
		return name + "-" + otherName;
	}

	public static String upper(String abc) {
		return abc.toUpperCase();
	}

	public static boolean isVIP(String name) {
		System.out.println(" execute isVIP("+ name+ ") .....");
		return false;
	}
}
