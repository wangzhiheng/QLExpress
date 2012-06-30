package com.ql.util.express.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ql.util.express.match.INodeTypeManager;

public class NodeTypeManager implements INodeTypeManager {
	private static final Log log = LogFactory.getLog(NodeTypeManager.class);
	
		public String[] splitWord;
		private String[] keyWords;
		private String[] nodeTypeDefines;
		protected String[][] instructionFacotryMapping;
	    protected Map<String,NodeType> nodeTypes = new HashMap<String,NodeType>();	
	    
	    //���еĺ�������
	    protected Map<String,String> functions = new HashMap<String,String>();
	    
	    public NodeTypeManager() {
	    	this(new KeyWordDefine4Java());
	    }
	    public NodeTypeManager(KeyWordDefine4SQL keyWorkdDefine){
	    	this.splitWord = keyWorkdDefine.splitWord;
			this.keyWords = keyWorkdDefine.keyWords;
			this.nodeTypeDefines = keyWorkdDefine.nodeTypeDefines;
			this.initial();
	    	
	    }
	    public NodeTypeManager(KeyWordDefine4Java keyWorkdDefine){
	    	this.splitWord = keyWorkdDefine.splitWord;
			this.keyWords = keyWorkdDefine.keyWords;
			this.nodeTypeDefines = keyWorkdDefine.nodeTypeDefines;
			this.instructionFacotryMapping = keyWorkdDefine.instructionFacotryMapping;
			this.initial();
			this.addOperatorWithRealNodeType("and","&&");
			this.addOperatorWithRealNodeType("or","||");

	    }
	    
		public void initial() {
			//�������еĹؼ���
			NodeType[] tempKeyWordNodeTypes = new NodeType[splitWord.length + keyWords.length];
			for (int i = 0; i < splitWord.length; i++) {
				tempKeyWordNodeTypes[i] = this.createNodeType(splitWord[i] + ":TYPE=KEYWORD");
			}
			for (int i = 0 ; i < keyWords.length; i++) {
				tempKeyWordNodeTypes[i + splitWord.length] = this.createNodeType(keyWords[i] + ":TYPE=KEYWORD");
			}
			// ��ʼ�����е�������Ϣ��
			for (int i = 0; i < tempKeyWordNodeTypes.length; i++) {
				tempKeyWordNodeTypes[i].initial();
			}
			
			// �������е�������Ϣ�������ܳ�ʼ��
			NodeType[] nodeTypes = new NodeType[nodeTypeDefines.length];
			for (int i = 0; i < nodeTypeDefines.length; i++) {
				nodeTypes[i] = this.createNodeType(nodeTypeDefines[i]);
			}
			// ��ʼ�����е�������Ϣ��
			for (int i = 0; i < nodeTypes.length; i++) {
				nodeTypes[i].initial();
			}
			
			//��ʼ��ָ��Facotry
		if (this.instructionFacotryMapping != null) {
			for (String[] list : this.instructionFacotryMapping) {
				for (String s : list[0].split(",")) {
					this.findNodeType(s).setInstructionFactory(list[1]);
				}
			}
		}
	}
	    
	/**
	 * �����ڵ����ͣ���Ҫע����ǲ��ܳ�ʼ�����������е����Ͷ�������ɺ���ܵ��ó�ʼ������
	 * @param aDefineStr
	 * @return
	 */
	public NodeType createNodeType(String aDefineStr){		
		int index = aDefineStr.indexOf(":",1);//����Բ�������":"�Ĵ�����
		String name = aDefineStr.substring(0,index).trim();
		NodeType define = nodeTypes.get(name);
		if(define != null ){
			log.warn("�ڵ����Ͷ����ظ�:"+name+" ����1="+define.getDefineStr() + " ����2=" + aDefineStr);
			throw new RuntimeException("�ڵ����Ͷ����ظ�:"+name+" ����1="+define.getDefineStr() + " ����2=" + aDefineStr);
		}
		define = new NodeType(this,name,aDefineStr);
		nodeTypes.put(name, define);
		return define;
	}
	/**
	 * �����������Ʋ��ҽڵ�����
	 * @param name
	 * @return
	 */
	@Override
	public NodeType findNodeType(String name){		
		NodeType result = nodeTypes.get(name);
		if(result == null){
			throw new RuntimeException("û�ж���Ľڵ����ͣ�" + name);
		}
		while(result.getRealNodeType() != null){
			result = result.getRealNodeType();
		}
		return result;
	}
	
	/**
	 * ���ӹؼ��֣�������ʵ�ʵ����ʹ��棬���� :"���"->"if"
	 * @param keyWordName
	 * @param realName
	 */
	public void addOperatorWithRealNodeType(String keyWordName, String realName){
		NodeType target =  this.createNodeType(keyWordName + ":TYPE=KEYWORD,REAL=" + realName);
		target.initial();
	}
	
	/**
	 * �����µĲ������ţ������ȼ����Լ��﷨��ϵ����յĲ�������һ��
	 * @param operName
	 * @param refOperName
	 * @throws Exception 
	 */
	public void addOperatorWithLevelOfReference(String operName, String refOperName) throws Exception{
		NodeType target =  this.createNodeType(operName + ":TYPE=KEYWORD");
		target.initial();
		NodeType[] list = this.getNodeTypesByKind(NodeTypeKind.OPERATOR);
		NodeType refNodeType = this.findNodeType(refOperName);
		target.setInstructionFactory(refNodeType.getInstructionFactory());
		for(NodeType item:list){
			if(item.isContainerChild(refNodeType)){
				item.addChild(target);
				return;
			}
		}		
	}
	
	/**
	 * �ж��Ƿ���ڽڵ����Ͷ���
	 * @param name
	 * @return
	 */
	public NodeType isExistNodeTypeDefine(String name){
		NodeType result = nodeTypes.get(name);
		if(result != null && result.getRealNodeType() != null){				
		  result = result.getRealNodeType();
		}
		return result;
	}

	public NodeType[] getNodeTypesByKind(NodeTypeKind aKind){
		List<NodeType> result  = new ArrayList<NodeType>();
		for(NodeType item :this.nodeTypes.values()){
			if(item.getKind() == aKind){
				result.add(item);
			}
		}
		return result.toArray(new NodeType[0]);
	}
	public boolean isFunction(String name){
		return this.functions.containsKey(name);
	}
	public void addFunctionName(String name){
		this.functions.put(name, name);
	}
}
