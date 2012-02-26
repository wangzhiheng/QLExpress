package com.ql.util.express.parse;

import java.util.ArrayList;
import java.util.Arrays;
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
		protected String statementDefineStrs;
		protected String[] expressDefineStrs;
		
		protected List<NodeType> statementDefine = new ArrayList<NodeType>();
		protected List<List<NodeType>> expressDefine = new ArrayList<List<NodeType>>();
	    public NodeType S_STATEMNET = null;
	    public NodeType S_ID = null;
	    
	    protected Map<String,NodeType> nodeTypes = new HashMap<String,NodeType>();	
	    
	    //所有的函数定义
	    protected Map<String,String> functions = new HashMap<String,String>();
	    
	    public NodeTypeManager() {
	    	this(new KeyWordDefine4Java());
	    }
	    public NodeTypeManager(KeyWordDefine4SQL keyWorkdDefine){
	    	this.splitWord = keyWorkdDefine.splitWord;
			this.keyWords = keyWorkdDefine.keyWords;
			this.nodeTypeDefines = keyWorkdDefine.nodeTypeDefines;
			this.statementDefineStrs = keyWorkdDefine.statementDefineStrs;
			this.expressDefineStrs = keyWorkdDefine.expressDefineStrs;
			this.initial();
	    	
	    }
	    public NodeTypeManager(KeyWordDefine4Java keyWorkdDefine){
	    	this.splitWord = keyWorkdDefine.splitWord;
			this.keyWords = keyWorkdDefine.keyWords;
			this.nodeTypeDefines = keyWorkdDefine.nodeTypeDefines;
			this.statementDefineStrs = keyWorkdDefine.statementDefineStrs;
			this.expressDefineStrs = keyWorkdDefine.expressDefineStrs;
			this.initial();
			this.addOperatorWithRealNodeType("and","&&");
			this.addOperatorWithRealNodeType("or","||");

	    }
	    
		public void initial() {
			//创建所有的关键字
			NodeType[] tempKeyWordNodeTypes = new NodeType[splitWord.length + keyWords.length];
			for (int i = 0; i < splitWord.length; i++) {
				tempKeyWordNodeTypes[i] = this.createNodeType(splitWord[i] + ":TYPE=KEYWORD");
			}
			for (int i = 0 ; i < keyWords.length; i++) {
				tempKeyWordNodeTypes[i + splitWord.length] = this.createNodeType(keyWords[i] + ":TYPE=KEYWORD");
			}
			// 初始化所有的类型信息，
			for (int i = 0; i < tempKeyWordNodeTypes.length; i++) {
				tempKeyWordNodeTypes[i].initial();
			}
			
			// 创建所有的类型信息，但不能初始化
			NodeType[] nodeTypes = new NodeType[nodeTypeDefines.length];
			for (int i = 0; i < nodeTypeDefines.length; i++) {
				nodeTypes[i] = this.createNodeType(nodeTypeDefines[i]);
			}
			// 初始化所有的类型信息，
			for (int i = 0; i < nodeTypes.length; i++) {
				nodeTypes[i].initial();
			}
			String[] tempStrList = statementDefineStrs.split("\\,");
			for (String item : tempStrList) {
				statementDefine.add(this.findNodeType(item));
			}

			for (String item : expressDefineStrs) {
				String[] tempList = item.split("\\,");
				List<NodeType> tempNodeTypeList = new ArrayList<NodeType>();
				for (String str:tempList) {
					tempNodeTypeList.add(this.findNodeType(str));
				}
				expressDefine.add(tempNodeTypeList);
			}
			S_STATEMNET = this.findNodeType("STATEMENT");
			S_ID = this.findNodeType("ID");
		}
	    
	/**
	 * 创建节点类型，需要注意的是不能初始化，必须所有的类型都创建完成后才能调用初始化方法
	 * @param aDefineStr
	 * @return
	 */
	public NodeType createNodeType(String aDefineStr){		
		int index = aDefineStr.indexOf(":",1);//避免对操作符号":"的错误处理
		String name = aDefineStr.substring(0,index).trim();
		NodeType define = nodeTypes.get(name);
		if(define != null ){
			log.warn("节点类型定义重复:"+name+" 定义1="+define.getDefineStr() + " 定义2=" + aDefineStr);
		//	throw new RuntimeException("节点类型定义重复:"+name+" 定义1="+define.getDefineStr() + " 定义2=" + aDefineStr);
		}
		define = new NodeType(this,name,aDefineStr);
		nodeTypes.put(name, define);
		return define;
	}
	/**
	 * 根据类型名称查找节点类型
	 * @param name
	 * @return
	 */
	@Override
	public NodeType findNodeType(String name){		
		NodeType result = nodeTypes.get(name);
		if(result == null){
			throw new RuntimeException("没有定义的节点类型：" + name);
		}
		while(result.getRealNodeType() != null){
			result = result.getRealNodeType();
		}
		return result;
	}
	
	/**
	 * 增加关键字，但是用实际的类型代替，例如 :"如果"->"if"
	 * @param keyWordName
	 * @param realName
	 */
	public void addOperatorWithRealNodeType(String keyWordName, String realName){
		NodeType target =  this.createNodeType(keyWordName + ":TYPE=KEYWORD,REAL=" + realName);
		target.initial();
	}
	
	/**
	 * 增加新的操作符号，其优先级别，以及语法关系与参照的操作符号一致
	 * @param operName
	 * @param refOperName
	 */
	public void addOperatorWithLevelOfReference(String operName, String refOperName){
		NodeType target =  this.createNodeType(operName + ":TYPE=KEYWORD");
		target.initial();
		NodeType[] list = this.getNodeTypesByKind(NodeTypeKind.OPERATOR);
		NodeType refNodeType = this.findNodeType(refOperName);
		target.setInstructionFactory(refNodeType.getInstructionFactory());
		for(NodeType item:list){
			if(item.isContainsChild(refNodeType)){
				item.addChild(target);
				return;
			}
		}		
	}
	
	/**
	 * 判断是否存在节点类型定义
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
	public boolean isSplitWord(String word){
		for(String s: this.splitWord){
			if(s.equals(word)){
				return true;
			}
		}
		return false;
	}
	/**
	 * 递归判断节点类型是否匹配，例如 :
	 * isNodeTypeOrChild(CONST_LONG,CONST)==true
	 * isNodeTypeOrChild(CONST_LONG,CONST_NUMBER)==true
	 * isNodeTypeOrChild(CONST_LONG,CONST_BOOLEAN)==false
	 * @param child
	 * @param parent
	 * @return
	 */
	public NodeType isEqualsOrChildAndReturn2(NodeType child,NodeType parent){
		if (child == parent)
			return child;
		NodeType[] tempList = parent.getChildren();
		if (tempList != null) {
			for (NodeType item : tempList) {
				NodeType result = isEqualsOrChildAndReturn2(child, item);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
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
	public NodeType[] getNodeTypesSortByKind(){
		NodeType[] list = nodeTypes.values().toArray(new NodeType[0]);
		Arrays.sort(list,new NodeTypeComparator());
		return list;
	}
	public boolean isFunction(String name){
		return this.functions.containsKey(name);
	}
	public void addFunctionName(String name){
		this.functions.put(name, name);
	}
	public static String[] splist(String str,char splitChar,boolean isIncludeSplitChar){
		List<String> result = new ArrayList<String>();
		String tempStr ="";
		for(int i=0;i<str.length();i++){
			if (str.charAt(i) == '\\') {
				tempStr = tempStr + str.charAt(i + 1);
				i = i + 1;
			} else if (str.charAt(i) == splitChar) {
				if(tempStr.length() >0){
				    result.add(tempStr);
				}
				if(isIncludeSplitChar == true){
					result.add(splitChar + "");
				}
				tempStr = "";
			} else {
				tempStr = tempStr + str.charAt(i);
			}
		}
		if(tempStr.length() >0){
			result.add(tempStr);
		}
		return result.toArray(new String[0]);
	}
}
