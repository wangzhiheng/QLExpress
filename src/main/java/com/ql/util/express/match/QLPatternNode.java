package com.ql.util.express.match;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

enum MatchMode{
	AND,OR,DETAIL,NULL
}
public class QLPatternNode{
	private static final Log log = LogFactory.getLog(QLPatternNode.class);
	INodeTypeManager nodeTypeManager;
	/**
	 * ԭʼ���ַ���
	 */
	String orgiContent;
	/**
	 * ƥ��ģʽ
	 */
	MatchMode matchMode =MatchMode.NULL ;
	/**
	 * �Ƿ�һ����ƥ��ģʽ
	 */
	boolean isChildMode = false;
	/**
	 * ���
	 */
	int level =0;
	/**
	 * �Ƿ���ڵ�,���磺if^
	 */
	protected boolean isTreeRoot;
	
	/**
	 * ��Сƥ�������0..n
	 */
	protected int minMatchNum =1;
	
	/**
	 * ���ƥ�����
	 */
	protected int maxMatchNum =1;
	
	
	/**
	 * ƥ�����ͣ����� ID,if,SELECT
	 */
	protected INodeType nodeType;
	
	/**
	 * ƥ�䵽�Ľڵ���Ҫת���ɵ����ͣ����� ID->CONST_STRING
	 */
	protected INodeType targetNodeType;
	
	/**
	 * ��ҪתΪ���������ͣ����磺(ID$(,$ID)*)#COL_LIST
	 */
	protected INodeType rootNodeType;
	
	/**
	 * ��ƥ��ģʽ
	 */
	List<QLPatternNode> children = new ArrayList<QLPatternNode>();
	
	protected QLPatternNode(INodeTypeManager aManager,String aOrgiContent) throws Exception{
		this(aManager,aOrgiContent,false,1);
		if(this.toString().equals(aOrgiContent)==false){
				throw new Exception("�﷨���������Ľ����ԭʼֵ��һ�£�ԭʼֵ:"+ aOrgiContent + " �������:" + this.toString());
		}
	}
	private QLPatternNode(INodeTypeManager aManager,String aOrgiContent,boolean aIsChildMode,int aLevel) throws Exception{
		this.nodeTypeManager = aManager;
		this.orgiContent = aOrgiContent;
		this.isChildMode = aIsChildMode;
		this.level = aLevel;
		this.splitChild();
	}
	/**
	 * �����'('��')'�ָ�����ƥ��ģʽ���Լ�$,|�ָ���ƥ�䴮
	 * @throws Exception
	 */
	public void splitChild() throws Exception{
		if(log.isTraceEnabled()){
			String str ="";
			for(int i=0;i<this.level;i++){
				str = str + "  ";
			}
			//log.trace("�ֽ�ƥ��ģʽ[LEVEL="+ this.level +"]START:" + str + this.orgiContent);
		}
		String orgStr = this.orgiContent;
		
		String tempStr ="";
		int count =0;
		if(orgStr.equals("()")){
			tempStr = orgStr;
		}else{
			for(int i=0;i<orgStr.length();i++){
				if (orgStr.charAt(i) == '\\') {
					if(count >0){
					   tempStr = tempStr + orgStr.charAt(i);
					}
				   tempStr = tempStr + orgStr.charAt(i + 1);
				   i = i + 1;
				} else if (orgStr.charAt(i) == '(') {
					if (count > 0) {
						tempStr = tempStr + orgStr.charAt(i);
					} else if (count == 0 && tempStr.length() > 0) {
						children.add(new QLPatternNode(this.nodeTypeManager,tempStr, false,this.level + 1));
						tempStr = "";
					}
					count = count + 1;
				}else if(orgStr.charAt(i) == ')'){
				    count = count -1;
					if(count == 0){//�Ƕ�Ӧ��()ƥ��
						if(tempStr.length() > 0){
							children.add(new QLPatternNode(this.nodeTypeManager,tempStr,true,this.level + 1));
						  tempStr ="";
						}else{
							children.add(new QLPatternNode(this.nodeTypeManager,"()",false,this.level + 1));
							tempStr ="";
							//throw new Exception("����ȷ��ģʽ��:" + orgStr);
						}
					}else{
						tempStr = tempStr + orgStr.charAt(i);
					}
				}else if(orgStr.charAt(i) == '$'){
					if(count > 0){
						tempStr = tempStr + orgStr.charAt(i);
					}else{
						if (this.matchMode != MatchMode.NULL
								&& this.matchMode != MatchMode.AND) {
							throw new Exception("����ȷ��ģʽ��,��һ��ƥ��ģʽ�в���|,$����,��ʹ���ִ�ģʽ:"
									+ orgStr);
						}
						if (tempStr.length() > 0) {
							if(tempStr.equals("^") && children.size() >0){
								//(FUNCTION_NAME$ID)^$(OPDATA*)�����
								children.get(children.size() -1).isTreeRoot = true;
							}else{
								children.add(new QLPatternNode(this.nodeTypeManager,tempStr, false,
									this.level + 1));
							}
							tempStr = "";
						}
						this.matchMode = MatchMode.AND;
					}
				}else if(orgStr.charAt(i) == '|'){
					if(count > 0){
						tempStr = tempStr + orgStr.charAt(i);
					}else{
						if (this.matchMode != MatchMode.NULL
								&& this.matchMode != MatchMode.OR) {
							throw new Exception("����ȷ��ģʽ��,��һ��ƥ��ģʽ�в���|,$����,��ʹ���ִ�ģʽ:"
									+ orgStr);
						}
						
						if (tempStr.length() > 0) {
							if(tempStr.equals("^") && children.size() >0){
								//(FUNCTION_NAME|ID)^|(OPDATA*)�����
								children.get(children.size() -1).isTreeRoot = true;
							}else{
								children.add(new QLPatternNode(this.nodeTypeManager,tempStr, false,
									this.level + 1));
							}
							tempStr = "";
						}
						this.matchMode = MatchMode.OR;
					}
			    
				}else if(orgStr.charAt(i) == '#'){
					if(count > 0){
						tempStr = tempStr + orgStr.charAt(i);
					}else{
						if (tempStr.length() > 0){
							children.add(new QLPatternNode(this.nodeTypeManager,tempStr, false,
									this.level + 1));
							tempStr = "";
						}
						tempStr = orgStr.substring(i);
						break;//�˳�
					}
				}else {
					tempStr = tempStr + orgStr.charAt(i);
				}
			}
		}
		if(count >0){
			throw new Exception("����ȷ��ģʽ��,(û���ҵ���Ӧ��):" + orgStr);
		}
		if(tempStr.length() >0){
			if(tempStr.equals("^*") && this.children.size() == 1){
				 this.isTreeRoot = true;
				 this.minMatchNum = 0;
			     this.maxMatchNum = Integer.MAX_VALUE;				
			}else if(tempStr.equals("^*") == true && this.children.size() > 1){
	    		 this.children.get(this.children.size() - 1).isTreeRoot = true;
	    		 this.children.get(this.children.size() - 1).minMatchNum = 0;
	    		 this.children.get(this.children.size() - 1).maxMatchNum = Integer.MAX_VALUE;
	    		 
			}else if(      tempStr.equals("^") == true && this.children.size() == 1){
	    		 this.isTreeRoot = true;
		    }else if(tempStr.equals("^") == true && this.children.size() > 1){
		    		 this.children.get(this.children.size() - 1).isTreeRoot = true;
			}else if(tempStr.charAt(0)=='#'){
		    	this.rootNodeType = this.nodeTypeManager.findNodeType(tempStr.substring(1));
		    }else if(tempStr.equals("*")){
		    	this.minMatchNum = 0;
		    	this.maxMatchNum = Integer.MAX_VALUE;
		    }else if(tempStr.startsWith("{") && tempStr.endsWith("}") && tempStr.length() > 2){
				String numStr = tempStr.substring(1,tempStr.length() - 1);
				int index2 = numStr.indexOf(':');
				if (index2 > 0) {
					this.minMatchNum = Integer.parseInt(numStr.substring(0, index2));
					this.maxMatchNum = Integer.parseInt(numStr.substring(index2 + 1));
				} else {
					this.minMatchNum = Integer.parseInt(numStr);
					this.maxMatchNum = Integer.parseInt(numStr);
				}
		    }else if(this.children.size() > 0){
		    	this.children.add(new QLPatternNode(this.nodeTypeManager,tempStr,false,this.level + 1));
		    }else{
		    	if(tempStr.indexOf('#')>0){
		    		this.rootNodeType = this.nodeTypeManager.findNodeType(tempStr.substring(tempStr.indexOf('#') + 1));
		    		tempStr = tempStr.substring(0,tempStr.indexOf('#'));
		    	}
				if(tempStr.endsWith("^")==true && tempStr.length() > 1){
						this.isTreeRoot = true;
						tempStr = tempStr.substring(0,tempStr.length() -1);
		    	}
		    	if(tempStr.endsWith("*")){
			    	this.minMatchNum = 0;
			    	this.maxMatchNum = Integer.MAX_VALUE;
		    		tempStr = tempStr.substring(0,tempStr.length() -1);
				}
		    	
		    	if(tempStr.endsWith("}")){
		    		int index = tempStr.lastIndexOf("{");
		    		if(index > 0){
						String numStr = tempStr.substring(index + 1,tempStr.length() - 1);
						int index2 = numStr.indexOf(':');
						if (index2 > 0) {
							this.minMatchNum = Integer.parseInt(numStr.substring(0, index2));
							this.maxMatchNum = Integer.parseInt(numStr.substring(index2 + 1));
						} else {
							this.minMatchNum = Integer.parseInt(numStr);
							this.maxMatchNum = Integer.parseInt(numStr);
						}
						tempStr = tempStr.substring(0,index);
		    		}
		    	}
		    	
		    	int index = tempStr.indexOf("->");
		    	if(index >0){
		    		this.targetNodeType = this.nodeTypeManager.findNodeType(tempStr.substring(index + 2));
		    		tempStr = tempStr.substring(0,index);
		    	}
		    	
		    	if(tempStr.length() >0){
		    		this.matchMode = MatchMode.DETAIL;
		    		this.nodeType = this.nodeTypeManager.findNodeType(tempStr);
		    	}
			}
		}
		if(this.matchMode == MatchMode.NULL && this.children.size() == 1){
			this.matchMode = MatchMode.AND;
		}
		if(log.isTraceEnabled()){
			String str ="";
			for(int i=0;i<this.level;i++){
				str = str + "  ";
			}
			//log.trace("�ֽ�ƥ��ģʽ[LEVEL="+ this.level +"]  END:" + str + this.toString());
		}
	}
	public String getPrintName(INodeType nodeType){
		return nodeType.getTag();	
	}
	public String toString(){
		String result ="";
		if(this.matchMode == MatchMode.AND){
			result = this.joinStringList(this.children,"$");
		}else if(this.matchMode ==MatchMode.OR){
			result = this.joinStringList(this.children,"|");
		}else{
			result = getPrintName(this.nodeType);
		}
		if(this.targetNodeType != null){
			result = result +"->" + getPrintName(this.targetNodeType);
		}
		
		if(this.minMatchNum == 0 && this.maxMatchNum == Integer.MAX_VALUE){
			result = result +'*';
		}else if(this.minMatchNum == this.maxMatchNum && this.maxMatchNum > 1) {
			result = result + "{" + this.maxMatchNum +"}";
		}else if(this.minMatchNum != this.maxMatchNum){
			result = result + "{" + this.minMatchNum +":" + this.maxMatchNum +"}";
		}
		
		if(this.rootNodeType != null){
			result = result + '#'+ getPrintName(this.rootNodeType);
		}
		if(this.isChildMode == true){
			result ="("+ result + ")";
		}
		if(this.isTreeRoot){
			result = result +'^';	
		}
		
		return result;
	}
	public String joinStringList(List<QLPatternNode> list,String splitChar){
		StringBuffer buffer = new StringBuffer();
		for(int i=0;i<list.size();i++){
			if(i>0){buffer.append(splitChar);}
			buffer.append(list.get(i));
		}
		return buffer.toString();
	}
}

