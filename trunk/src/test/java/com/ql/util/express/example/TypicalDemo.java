package com.ql.util.express.example;

import java.util.ArrayList;
import java.util.List;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;

/**
 * QLExpress��һ�ֵ���Ӧ�ó���
 * @author xuannan
 *
 */
public class TypicalDemo {

	private ExpressRunner runner = new ExpressRunner();
    /**
     * �ж�һ���û�TAG�ĵ�Xλ�Ƿ�Ϊ1�������demo,��ʵ�ֺ����Բ�����
     * @param user
     * @param tagBitIndex
     * @return
     */
	public boolean userTagJudge(UserInfo user,int tagBitIndex){
    	boolean r =  (user.getUserTag() & ((long)Math.pow(2, tagBitIndex))) > 0;
    	return r;
    }
	
	/**
	 * �ж�һ���û��Ƿ񶩹���ĳ����Ʒ
	 * @param user
	 * @param goodsId
	 * @return
	 */
	public boolean hasOrderGoods(UserInfo user,long goodsId){
		//���ģ��һ��
		if(user.getUserId() % 2 == 1){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * �ж��߼�ִ�к���
	 * @param userInfo
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public String hasPermission(UserInfo userInfo,String expression) throws Exception {			
        	IExpressContext<String,Object> expressContext = new DefaultContext<String,Object>();
    		expressContext.put("userInfo",userInfo);
    		List<String> errorInfo = new ArrayList<String>();
            Boolean result = (Boolean)runner.execute(expression, expressContext, errorInfo, true, false);
            String resultStr ="";
            if(result.booleanValue() == true){
            	resultStr = "���Զ�������Ʒ";
            }else{
              for(int i=0;i<errorInfo.size();i++){
            	  if(i > 0){
            		  resultStr  = resultStr + "," ;
            	  }
            	  resultStr  = resultStr + errorInfo.get(i);
              }
              resultStr = resultStr  + ",���Բ��ܶ�������Ʒ";
            }
            return "�װ���" + userInfo.getName() + " : " + resultStr;
    }		
	public void initial() throws Exception{
		runner.addOperatorWithAlias("����","and",null);
		runner.addFunctionOfClassMethod("userTagJudge", TypicalDemo.class.getName(), "userTagJudge",new String[] {UserInfo.class.getName(),"int"}, "�㲻����������");
		runner.addFunctionOfClassMethod("hasOrderGoods", TypicalDemo.class.getName(), "hasOrderGoods",new String[] {UserInfo.class.getName(),"long"}, "��û�п�ͨ�Ա�����");
		runner.addMacro("��������", "userTagJudge(userInfo,3)");//3��ʾ�������ҵı�־λ
		runner.addMacro("�Ѿ�����", "hasOrderGoods(userInfo,100)");//100��ʾ������Ʒ��ID
	}
	public static void main(String args[]) throws Exception{
		TypicalDemo demo = new TypicalDemo();
		demo.initial();
		System.out.println(demo.hasPermission(new UserInfo(100,"xuannan",7),  "��������   ����   �Ѿ�����"));
		System.out.println(demo.hasPermission(new UserInfo(101,"qianghui",8), "��������   ����   �Ѿ�����"));
		System.out.println(demo.hasPermission(new UserInfo(100,"����",8), "�������� and �Ѿ�����"));
		System.out.println(demo.hasPermission(new UserInfo(100,"����",7), "�������� and �Ѿ�����"));
	}
}

class UserInfo {
	long id;
	long tag;
    String name;

	public UserInfo(long aId,String aName, long aUserTag) {
		this.id = aId;
		this.tag = aUserTag;
		this.name = aName;
	}
    public String getName(){
    	return name;
    }
	public long getUserId() {
		return id;
	}

	public long getUserTag() {
		return tag;
	}
}