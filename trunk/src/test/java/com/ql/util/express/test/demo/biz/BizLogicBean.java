package com.ql.util.express.test.demo.biz;

public class BizLogicBean {
	
	
	/**
	 * ע�����û�
	 * @param nick
	 * @return
	 */
	public UserDO signUser(String nick){
 		UserDO user = new UserDO();
		user.setNick(nick);
		user.setScore(0L);
		user.setShopOpen(false);
		user.setShopType("no_shop");
		System.out.println("�����û��ɹ���"+user);
		return user;
	}
	/**
	 * �û����̼���
	 * @param user
	 */
	public void openShop(UserDO user){
		user.setShopOpen(true);
		user.setShopType("c2c");
		System.out.println("�����Ѿ����"+user);
		user.setShopOpen(true);
	}
	
	/**
	 * �û����̹ر�
	 * @param user
	 */
	public void closeShop(UserDO user){
		user.setShopOpen(false);
		System.out.println("�����Ѿ��رգ�"+user);
		user.setShopOpen(false);
	}
	
	/**
	 * �û��Ǽ�+1
	 * @param user
	 */
	public void addScore(UserDO user){
		user.setScore(user.getScore()+1);
		System.out.println("�û��Ǽ� +1 ��"+user);
	}
	
	public boolean isShopOpening(UserDO user){
		if(user.isShopOpen()){
			System.out.println("��ǰ����ΪӪҵ״̬.");
			return true;
		}
		System.out.println("��ǰ����Ϊ�ر�״̬.");
		return false;
	}
	
	/**
	 * ��������
	 * @param user
	 * @return
	 */
	public boolean upgradeShop(UserDO user){
		if(user.getShopType().equals("b2c")){
			System.out.println("���Ѿ���B�̼ң�����������.");
			return false;
		}
		if(user.getScore().longValue()>5L){
			user.setShopType("b2c");
			System.out.println("�ɹ�����ΪB�̼ң�"+user);
			return true;
		}else{
			System.out.println("��Ҫ5�Ǽ��������ң������ڲ�"+user.getScore()+"�Ǽ����ٽ�����Ŷ!");
			return false;
		}
	}
	
	public void showShop(UserDO user){
		System.out.println(user);
	}

}
