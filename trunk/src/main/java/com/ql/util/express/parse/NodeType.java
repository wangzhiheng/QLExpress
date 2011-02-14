package com.ql.util.express.parse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

enum NodeTypeKind {
	KEYWORD,CONST,BLOCK,EXPRESS,SYMBOL,DEFINE,TREETYPE,OPERATOR
}
public class NodeType {
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
		private StatementDefine statementDefine;
		private NodeType realNodeType;
		private List<NodeType> children  =  new ArrayList<NodeType>();
		
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
			if(this.children.size() > 0){
				result.append(",CHILDREN=");
				for(int i = 0;i < this.children.size();i++){
					if(i >0){
						result.append("|");
					}
					result.append(this.children.get(i).getTag());
				}
			}
			if(this.targetStatementRoot == null){
				result.append(",TARGETROOT=" + this.targetStatementRoot);
			}
			if(this.statementDefine != null){
				result.append(",DEFINE=").append(this.statementDefine);
			}
			return result.toString();
		}
		protected NodeType(NodeTypeManager aManager,String aName,String aDefineStr){
			this.manager = aManager;
			this.defineStr = aDefineStr;
			this.name =aName;
		}
		
		public void initial(){
			try{
			int index = this.defineStr.indexOf(":",1);
			String[] properties = this.defineStr.substring(index + 1).split(",");
			for(String p:properties){
				String[] tempList = new String[2];
				int point = p.indexOf("=");
				tempList[0] = p.substring(0,point).trim();
				tempList[1] = p.substring(point + 1).trim();			
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
					String[] children = NodeTypeManager.splist(tempList[1],'|',false);
					for(String child:children){
						this.addChild(manager.findNodeType(child.trim()));
					}
				}else if(tempList[0].equalsIgnoreCase("define")){
					this.statementDefine = StatementDefine.createStatementDefine(manager, tempList[1]);
				}else{
					throw new RuntimeException("不能识别\""+ this.name + "\"的属性类型："+ tempList[0] + " 定义：" + this.defineStr);
				}
			}		
			}catch(Exception e){
				throw new RuntimeException("节点类型\"" + this.name + "\"初始化失败,定义：" + this.defineStr,e);
			}
		}
		
		public boolean isEqualsOrChild(String parent){
			return this.manager.isEqualsOrChildAndReturn(this,this.manager.findNodeType(parent)) != null;
		}
		public boolean isEqualsOrChild(NodeType parent){
			return this.manager.isEqualsOrChildAndReturn(this,parent) != null;
		}
		public NodeType isEqualsOrChildAndReturn(NodeType parent){
			return this.manager.isEqualsOrChildAndReturn(this,parent);
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

		public StatementDefine getStatementDefine() {
			return statementDefine;
		}

		public void setStatementDefine(StatementDefine statementDefine) {
			this.statementDefine = statementDefine;
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

		public List<NodeType> getChildren() {
			return children;
		}

		public void setChildren(List<NodeType> children) {
			this.children = children;
		}

		public NodeType getTargetStatementRoot() {
			return targetStatementRoot;
		}
		public void setTargetStatementRoot(NodeType targetStatementRoot) {
			this.targetStatementRoot = targetStatementRoot;
		}
		public void addChild(NodeType child) {
			this.children.add(child);
		}
	}	

class NodeTypeComparator implements Comparator<NodeType>{

	public int compare(NodeType o1, NodeType o2) {
		 int r = o1.getKind().compareTo(o2.getKind());
		 if(r == 0){
			r = o1.getTag().compareTo(o2.getTag()); 
		 }
		 return r;
	}
	
}
