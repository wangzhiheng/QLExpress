package com.ql.util.express.test.rating;

/**
 * ��Ŀ����
 * @author xuannan
 *
 */
public class SubjectValue {
  public Object userId;
  public Object subjectId;
  public double value;
  public String toString(){
	  return "��Ŀ[" + userId + "," + subjectId +"] = " + value;
  }
}
