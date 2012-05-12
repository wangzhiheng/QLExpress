package com.ql.util.express.test.demo.biz;

public class UserDO {
	
	/**
	 * �û��ǳ�
	 */
	private String nick;	
	
	/**
	 * ����״̬
	 */
	private boolean shopOpen;
	
	/**
	 * �Ǽ�
	 */
	private Long score;
	
	/**
	 * ��������:c2c,b2c
	 */
	private String shopType;	

	public String getNick() {
		return nick;
	}
	
	public void setNick(String nick) {
		this.nick = nick;
	}
	
	public Long getScore() {
		return score;
	}
	
	public void setScore(Long score) {
		this.score = score;
	}
	
	public boolean isShopOpen() {
		return shopOpen;
	}
	
	public void setShopOpen(boolean shopOpen) {
		this.shopOpen = shopOpen;
	}
	
	public String getShopType() {
		return shopType;
	}
	
	public void setShopType(String shopType) {
		this.shopType = shopType;
	}
	
	
	public String toString(){
		StringBuffer sb = new StringBuffer("�û���Ϣ��");
		sb.append("\t\t�ǳ�: ").append(nick);
		if(shopOpen){
			sb.append("\t\t����״̬: Ӫҵ��");
		}else{
			sb.append("\t\t����״̬: �ر���");
		}
		sb.append("\t\t�����Ǽ�: ").append(score);
		sb.append("\t\t��������: ").append(shopType);
		return sb.toString();
	}
	

}
