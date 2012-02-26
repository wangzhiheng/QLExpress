package com.ql.util.express.parse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ql.util.express.match.INodeType;
import com.ql.util.express.match.QLPattern;
import com.ql.util.express.match.QLPatternNode;

enum NodeTypeKind {
	KEYWORD,CONST,BLOCK,EXPRESS,OPERATOR,WORDDEF,GROUP,STATEMENT
}
public class NodeType implements INodeType {
		NodeTypeManager manager;
		private String defineStr;
		private NodeTypeKind kind;
		private String name;
		private String tag;
		private NodeType startTag;
		private NodeType endTag;
		private String operatorName;
		private String instructionFactory;
		private NodeType targetStatementRoot;
		private QLPatternNode qlPatternNode;
		//private StatementDefine statementDefine;
		private NodeType realNodeType;
		private NodeType[] children;
		
		//---缓存执行
		Map<NodeType,NodeType> childrenCache ;
		
		public String toString() {
			StringBuilder result = new StringBuilder();
			result.append(name + ":TYPE=" + this.kind);
			if(this.tag != null){
				result.append(",TAG="+ this.tag);
			}
			if(this.startTag != null){
				result.append(",STARTTAG="+ this.startTag.getTag());
			}		
			if(this.endTag != null){
				result.append(",ENDTAG="+ this.endTag.getTag());
			}
			if(this.instructionFactory != null){
				result.append(",FACTORY=" + this.instructionFactory);
			}
			if(this.children != null && this.children.length >0){
				result.append(",CHILDREN=");
				for(int i = 0;i < this.children.length;i++){
					if(i >0){
						result.append("|");
					}
					result.append(this.children[i].getTag());
				}
			}
			if(this.targetStatementRoot == null){
				result.append(",TARGETROOT=" + this.targetStatementRoot);
			}
			if(this.qlPatternNode != null){
				result.append(",DEFINE=").append(this.qlPatternNode);
			}
			return result.toString();
		}
		protected NodeType(NodeTypeManager aManager,String aName,String aDefineStr){
			this.manager = aManager;
			this.defineStr = aDefineStr;
			this.name =aName;
		}
		
		public static String[][] splitProperties(String str){
			Pattern p = Pattern.compile("(,|:)\\s*(([A-Z]|-|_)*)\\s*=");
			Matcher matcher = p.matcher(str);
			List<String[]> list = new ArrayList<String[]>();
			int endIndex = 0;
			while(matcher.find()){
				if(list.size() >0){
					list.get(list.size() -1)[1] = str.substring(endIndex, matcher.start()).trim();
				}
				list.add(new String[2]);
				list.get(list.size() -1)[0] = str.substring(matcher.start() + 1, matcher.end() - 1 ).trim();
				endIndex = matcher.end();
			}
			if(list.size() > 0){
				list.get(list.size() -1)[1] = str.substring(endIndex).trim();
			}
			return (String[][])list.toArray(new String[0][2]);
		}		
		public void initial(){
			try{
			int index = this.defineStr.indexOf(":",1);
			String[][] properties = splitProperties(this.defineStr.substring(index));
			for(String[] tempList:properties){			
				if(tempList[0].equalsIgnoreCase("type")){
					this.setKind(NodeTypeKind.valueOf(tempList[1]));
				}else if(tempList[0].equalsIgnoreCase("tag")){
					this.setTag(tempList[1]);
				}else if(tempList[0].equalsIgnoreCase("startTag")){
					this.setStartTag(manager.findNodeType(tempList[1]));
				}else if(tempList[0].equalsIgnoreCase("endTag")){
					this.setEndTag(manager.findNodeType(tempList[1]));
				}else if(tempList[0].equalsIgnoreCase("targetRoot")){
					this.targetStatementRoot = manager.findNodeType(tempList[1]);
				}else if(tempList[0].equalsIgnoreCase("real")){
					this.realNodeType = manager.findNodeType(tempList[1]);
				}else if(tempList[0].equalsIgnoreCase("operator")){
					this.operatorName = tempList[1];
				}else if(tempList[0].equalsIgnoreCase("factory")){
					this.instructionFactory = tempList[1];
				}else if(tempList[0].equalsIgnoreCase("children")){
					String[] childrenStrs = NodeTypeManager.splist(tempList[1],'|',false);
					this.children = new NodeType[childrenStrs.length];
					for(int i=0;i<children.length;i++){
						children[i] = manager.findNodeType(childrenStrs[i].trim());
					}
				}else if(tempList[0].equalsIgnoreCase("define")){
					this.qlPatternNode = QLPattern.createPattern(this.manager,tempList[1]);
				}else{
					throw new RuntimeException("不能识别\""+ this.name + "\"的属性类型："+ tempList[0] + " 定义：" + this.defineStr);
				}
			}		
			}catch(Exception e){
				throw new RuntimeException("节点类型\"" + this.name + "\"初始化失败,定义：" + this.defineStr,e);
			}
		}
		public static void initialChildCache(Map<NodeType,NodeType> cache,NodeType node){
			cache.put(node, node);
			NodeType[] tempList = node.getChildren();
			if (tempList != null) {
				for (NodeType item : tempList) {
					if(cache.containsKey(item) == false){
						initialChildCache(cache,item);
					}
				}
			}			
		}
		public synchronized void initialChildCache(){
			if(this.childrenCache == null){
				Map<NodeType,NodeType> tempMap = new HashMap<NodeType,NodeType>();
				initialChildCache(tempMap,this);
				this.childrenCache = tempMap;
			}			
		}
		public boolean isEqualsOrChild(String parent){
			return this.isEqualsOrChildAndReturn(this.manager.findNodeType(parent)) != null;
		}
		public boolean isEqualsOrChild(NodeType parent){
			return this.isEqualsOrChildAndReturn(parent) != null;
		}
		
		public INodeType isEqualsOrChildAndReturn(INodeType parent){
			if(((NodeType)parent).isContainerChild(this)){
				return this;
			}
			return null;
		}
		private boolean isContainerChild(NodeType child){
			if(this.childrenCache == null){
				initialChildCache();
			}
			return this.childrenCache.containsKey(child);
		}
		public NodeTypeKind getKind() {
			return kind;
		}

		public String getInstructionFactory() {
			return instructionFactory;
		}
		public void setInstructionFactory(String instructionFactory) {
			this.instructionFactory = instructionFactory;
		}
		public String getOperatorName() {
			return operatorName;
		}
		public void setOperatorName(String operatorName) {
			this.operatorName = operatorName;
		}
		
		public NodeTypeManager getManager() {
			return manager;
		}
		public String getDefineStr() {
			return defineStr;
		}

		public void setDefineStr(String defineStr) {
			this.defineStr = defineStr;
		}
		public void setKind(NodeTypeKind kind) {
			this.kind = kind;
		}


		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		public String getTag() {
			if(this.tag == null){
				return this.name;
			}else{
			    return tag;
			}
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		public NodeType getRealNodeType() {
			return realNodeType;
		}
		public void setRealNodeType(NodeType realNodeType) {
			this.realNodeType = realNodeType;
		}
		public NodeType getStartTag() {
			return startTag;
		}

		public void setStartTag(NodeType startTag) {
			this.startTag = startTag;
		}

		public NodeType getEndTag() {
			return endTag;
		}

		public void setEndTag(NodeType endTag) {
			this.endTag = endTag;
		}
		
		public NodeType[] getChildren(){
			return this.children;
		}
		public boolean isContainsChild(NodeType child){
			if(this.children == null){
				return false;
			}else{
				for(NodeType item : this.children){
					if(child == item){
						return true;
					}
				}
			}
			return false;
		}
		public void addChild(NodeType child){
			if(this.children == null){
				this.children = new NodeType[1];
				this.children[0] = child; 
			}else{
				NodeType[] tempChildren = new NodeType[this.children.length + 1];
				System.arraycopy(this.children,0,tempChildren,0,this.children.length);
				tempChildren[tempChildren.length - 1] = child;
				this.children = tempChildren;
			}
			//清除所有的儿子缓存
			Map<NodeType,NodeType> tempMap = this.childrenCache;
			this.childrenCache = null;
			if(tempMap != null){
			   tempMap.clear();
			}
		}

		public NodeType getTargetStatementRoot() {
			return targetStatementRoot;
		}
		public void setTargetStatementRoot(NodeType targetStatementRoot) {
			this.targetStatementRoot = targetStatementRoot;
		}
		public QLPatternNode getPatternNode() {
		   return this.qlPatternNode;
		}

	}	

class NodeTypeComparator implements Comparator<NodeType>,java.io.Serializable{

	private static final long serialVersionUID = 1L;

	public int compare(NodeType o1, NodeType o2) {
		 int r = o1.getKind().compareTo(o2.getKind());
		 if(r == 0){
			r = o1.getTag().compareTo(o2.getTag()); 
		 }
		 return r;
	}
	
}
