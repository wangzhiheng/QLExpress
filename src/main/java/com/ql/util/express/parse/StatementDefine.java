package com.ql.util.express.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

enum StatementType{
	AND,OR,DETAIL
}
/**
 * @author xuannan
 *
 */
class StatementDefine{

	private static final Log log = LogFactory.getLog(StatementDefine.class);
	/**
	 * 儿子节点
	 */
	private StatementDefine[] children;
	/**
	 * 转换后新增的虚根节点，例如  (OPDATA*$;)#{}
	 */
	private NodeType rootNodeType;
	
	private StatementType type;
	private NodeTypeManager manager;
	/**
	 * 原始
	 */
	NodeType sourceNodeType;
	/**
	 * 目标
	 */
	NodeType targetNodeType;

	/**
	 * 是否根节点
	 */
	private boolean isTreeRoot;
	
	/**
	 * 是否1..n个
	 */
	private boolean isMutil;

	public static void main2(String[] args) throws Exception{
		NodeTypeManager manager = new NodeTypeManager();
		String define = "if^$(()|(OPDATA|OP_LIST*)#())$then$({}|(OPDATA*$;)#{})$else$({}|(OPDATA*$;)#{})";
		StatementDefine state = createStatementDefine(manager,define);
		System.out.println(state);
	}
	public static void main(String[] args) throws Exception {
		String condition = "if 1+2-4+8 then 4+8;3+2 else {1+3;}3+9;";
		NodeTypeManager manager = new NodeTypeManager();
		ExpressParse parse = new ExpressParse(manager);
		String[] words = WordSplit.parse(manager, condition);
		log.debug("执行的表达式:" + condition);
		log.debug("单词分解结果:" + WordSplit.getPrintInfo(words, ","));
		List<ExpressNode> tempList = parse.transferWord2ExpressNode(null,words);
		log.debug("单词分析结果:" + ExpressParse.printInfo(tempList, ","));
		ExpressNode root = parse.splitExpressBlock(tempList);
		ExpressParse.printTreeNode(root, 1);
		
		//String define = "OPDATA|OP_LIST|else|{}*#if";
		String define = "(if^)$(()|(((OPDATA|OP_LIST)*)#()))$then$({}|(((OPDATA|OP_LIST|;)*)#{}))$else$({}|(((OPDATA|OP_LIST)*)$;#{}))";
		
		StatementDefine state = createStatementDefine(manager,define);	
		System.out.println("语法定义：" + state);
		MatchResult match = state.findMatchStatement(manager,root.getLeftChildren(),0);
		if(match != null){
			StatementDefine.builderStatementTree(root.getLeftChildren(),0,match);	
		}else{
			throw new Exception("没有匹配的语句");
		}
		//System.out.println("匹配结果：\n" + match);
		ExpressParse.printTreeNode(root, 1);
	}
	public static ExpressNode builderStatementTree(List<ExpressNode> nodes,int point,MatchResult match) throws Exception{
		if(match != null && match.matchs.size() != 1){
			throw new Exception("语法定义错误，必须有一个根节点");
		}		
		match.matchs.get(0).buildExpressNodeTree();
		for(int i = match.matchLastIndex - 1; i > point;i--){
			nodes.remove(i);
		}
		nodes.set(point, match.matchs.get(0).ref);
		return match.matchs.get(0).ref;
		
	}
	public MatchResult findMatchStatement(NodeTypeManager aManager,List<ExpressNode> nodes,int point) throws Exception{
		MatchResult result = this.findMatchStatementWithAddRoot(nodes,point);
		if(result != null && result.matchs.size() != 1){
			throw new Exception("语法定义错误，必须有一个根节点：" + this);
		}
		return result;
	}
	private MatchResult findMatchStatementWithAddRoot(List<ExpressNode> nodes,int point) throws Exception{
		MatchResult result = this.findMatchStatementPrivate(nodes, point);
		if(result != null && result.matchs.size() >0 && this.rootNodeType != null){
			MatchResultTree tempTree = new MatchResultTree(this.rootNodeType,new ExpressNode(this.rootNodeType,null));
			tempTree.addLeftAll(result.matchs);
			result.matchs.clear();
			result.matchs.add(tempTree);
		}
		return result;
	}
		
	private MatchResult findMatchStatementPrivate(List<ExpressNode> nodes,int point) throws Exception{
		if (this.type == StatementType.DETAIL) {
			NodeType tempNodeType = null;
			ExpressNode tempNode = null;
			if(point == nodes.size() && this.sourceNodeType == manager.findNodeType("EOF") ){
				tempNodeType = manager.findNodeType(";");
				tempNode = new ExpressNode(tempNodeType,null);
				return new MatchResult(new ArrayList<MatchResultTree>(), point);
			}else if( point < nodes.size()){
				tempNodeType = nodes.get(point).isEqualsOrChildAndReturn(
					this.sourceNodeType);
				tempNode = nodes.get(point);
				point = point + 1;
			}else{
				return null;
			}
			if (tempNodeType != null) {
				List<MatchResultTree> tempList = new ArrayList<MatchResultTree>();
				tempList.add(new MatchResultTree(tempNodeType,tempNode,this.targetNodeType));
				return new MatchResult(tempList, point);
			}else{
				return null;
			}
		}else if (this.type == StatementType.OR) {
			for (StatementDefine item : this.children) {
				MatchResult tempResult = item.findMatchStatementWithAddRoot(nodes,
						point);
				if (tempResult != null) {
					return tempResult;
				}
			}
			return null;
		}else if(this.type ==StatementType.AND ){
    		MatchResultTree root = null;
    		List<MatchResultTree> tempList = new ArrayList<MatchResultTree>();
			for (StatementDefine item : this.children) {
				if(point > nodes.size()){
					return null;
				}
				MatchResult tempResult = item.findMatchStatementWithAddRoot(nodes,
						point);
				if (tempResult != null) {
					while (tempResult != null) {
						point = tempResult.matchLastIndex;
						if (item.isTreeRoot == true) {
							if (tempResult.matchs.size() > 1)
								throw new Exception("根节点的数量必须是1");
							if (root == null) {
								tempResult.matchs.get(0).addLeftAll(tempList);
								tempList.clear();
							} else {
								tempResult.matchs.get(0).addLeft(root);
							}
							root = tempResult.matchs.get(0);
						} else if (root != null) {
							root.addRightAll(tempResult.matchs);
						} else {
							tempList.addAll(tempResult.matchs);
						}
						if(point < nodes.size() && item.isMutil){
						   tempResult = item.findMatchStatementWithAddRoot(nodes,point);
						}else{
							break;
						}
					}
				}else{
					return null;
				}
			}	
			if(root != null){
				tempList.add(root);
			}
			if(tempList.size() > 0){
				return new MatchResult(tempList,point);
			}else{
				return null;
			}
		}else{
    		throw new Exception("还不支持的类型");
    	}
	}

    public static StatementDefine createStatementDefine(NodeTypeManager aManager,String stateDefine) throws Exception{
		List<String> words = new ArrayList<String>();
		words.add(stateDefine);		
		splitWord(words,'$');
		splitWord(words,'|');
		splitWord(words,'(');
		splitWord(words,')');
		splitWord(words,'^');
		splitWord(words,'*');
		splitWord(words,'#');		
		//log.debug(words);
		ExpressNode root = splitExpressBlock(words);
		//log.debug(root,1);
		StatementDefine result = new StatementDefine(aManager,root.getLeftChildren());
		if(stateDefine.equalsIgnoreCase(result.toString()) == false){
			throw new Exception("语法定义解析后的结果与原始值不一致，原始值:"+ stateDefine + " 解析结果:" + result.toString());
		}
		//log.debug(result);
		return new StatementDefine(aManager,result);
    }
    public StatementDefine(NodeTypeManager aManager,StatementDefine child){
    	this.children = new StatementDefine[]{child};
    	this.type = StatementType.AND;
    	this.manager = aManager;
    }
	public StatementDefine(NodeTypeManager aManager,NodeType aSource,NodeType aTarget){
		this.sourceNodeType = aSource;
		this.targetNodeType = aTarget;
		this.type = StatementType.DETAIL;
		this.manager = aManager;
	}
	public StatementDefine(NodeTypeManager aManager,List<ExpressNode> list) throws Exception{
		this.manager = aManager;
		List<ExpressNode> orgiList = list;
		//System.out.println(orgiList);
		if(list.size() == 1 && list.get(0).getValue() == "()" && list.get(0).getLeftChildren().size() >0 ){
			list = list.get(0).getLeftChildren();
		}	
		//寻找targetRoot
		if(list.size() >2 && list.get(list.size()-2).getValue().equalsIgnoreCase("#")){
				this.rootNodeType = aManager.findNodeType(list.get(list.size()-1).getValue());
				list = list.subList(0, list.size() -2);
		}
		//是否根
		if(list.size() >1 && list.get(list.size() -1).getValue().equals("^") ){
			this.isTreeRoot = true;
			list = list.subList(0, list.size() -1);
		}
		//是否多匹配
		if(list.size() >1 && list.get(list.size() -1).getValue().equals("*") ){
			this.isMutil = true;
			list = list.subList(0, list.size() -1);
		}
		if(list.size() == 1 && list.get(0).getValue() == "()" && list.get(0).getLeftChildren().size() >0 ){
			list = list.get(0).getLeftChildren();
			this.type = StatementType.AND;
			this.children   =  new StatementDefine[1];
			this.children[0] = new StatementDefine(aManager,list);
			return;
		}
		
		int startPoint =0;
		List<List<ExpressNode>> tempChildren = new ArrayList<List<ExpressNode>>();
		for(int i= 0; i< list.size();i++){
			if(list.get(i).getValue().equalsIgnoreCase("$")){
				tempChildren.add(list.subList(startPoint, i));
				startPoint = i + 1;
			}
		}
		if(tempChildren.size() > 0 && startPoint < list.size()){
			tempChildren.add(list.subList(startPoint, list.size()));
		}

		
		if(tempChildren.size() ==0){//没有AND儿子
			List<ExpressNode> tags = new ArrayList<ExpressNode>();
			int index = 0;
			while(index < list.size()){				
				tags.add(list.get(index));
				if(index + 1 < list.size() && list.get(index+1).getValue().equals("|")==false){
					throw new Exception("语法定义错误：" + list);
				}
				index = index + 2;
			}			
			//System.out.println("tags:" + tags);
			if (tags.size() > 1) {
				this.type = StatementType.OR;
				this.children = new StatementDefine[tags.size()];
				for (int m = 0; m < tags.size(); m++) {
						this.children[m] = new StatementDefine(aManager,tags.subList(m, m+1));
				}
			} else if(tags.size() ==1) {
				int tempIndex = tags.get(0).getValue().indexOf("->");
				if (tempIndex > 0) {
					this.sourceNodeType = aManager.findNodeType(tags.get(0).getValue().substring(0, tempIndex));
					this.targetNodeType = aManager.findNodeType(tags.get(0).getValue().substring(tempIndex + 2));
				} else {
					this.sourceNodeType = aManager.findNodeType(tags.get(0).getValue());
					this.targetNodeType = null;
				}
				this.type = StatementType.DETAIL;
			}else{
				System.out.println(orgiList);
				throw new Exception("语法定义错误");
			}
		}else{
			this.type = StatementType.AND;
			this.children = new StatementDefine[tempChildren.size()];
			for(int i=0;i<this.children.length;i++){
				this.children[i] =new StatementDefine(aManager,tempChildren.get(i));
			}
		}
	}

	public String toString(boolean isLevel0){
		StringBuilder builder = new StringBuilder();
		if(this.type == StatementType.DETAIL){
			builder.append(this.sourceNodeType.getTag());
			if(this.targetNodeType != null){
				builder.append("->").append(this.targetNodeType.getTag());
			}
		}else{
			char splitChar;
			if(this.type == StatementType.OR){
				splitChar ='|';
			}else if(this.type == StatementType.AND){
				splitChar ='$';
			}else{
				throw new RuntimeException("不能识别的类型：" + this.type );
			}
			for(int i=0;i<this.children.length;i++){
				StatementDefine item = this.children[i];
				if(i >0){
					builder.append(splitChar);
				}
				builder.append(item.toString(false));
			}
		}
		if(this.isMutil == true){
			builder.append("*");
		}
		if(this.isTreeRoot == true){
			builder.append("^");
		}
		if(this.rootNodeType != null){
			builder.append("#").append(this.rootNodeType.getTag());
		}
		if (isLevel0 == false) {
			if (this.children != null && this.children.length > 1
					|| this.isMutil == true || this.isTreeRoot == true
					|| this.rootNodeType != null) {
				builder.insert(0, "(");
				builder.append(")");
			}
		}
		return builder.toString();
	}
	public String toString(){
		return toString(true);
	}

	public static void printTreeNode(ExpressNode node, int level) {
		StringBuilder builder = new StringBuilder();
		builder.append(level+":" );		
		for (int i = 0; i < level; i++) {
			builder.append("   ");
		}
		builder.append(node);
		if(builder.length() <100){
			for (int i = 0; i <100 - builder.length(); i++) {
				builder.append("   ");
			}
		}
		//builder.append("\t"+ node.getTreeType().getTag());
		
		System.out.println(builder);
		List<ExpressNode> leftChildren = node.getLeftChildren();
		if (leftChildren != null && leftChildren.size() > 0) {
			for (ExpressNode item : leftChildren) {
				printTreeNode(item, level + 1);
			}
		}
		List<ExpressNode> rightChildren = node.getRightChildren();
		if (rightChildren != null && rightChildren.size() > 0) {
			for (ExpressNode item : rightChildren) {
				printTreeNode(item, level + 1);
			}
		}
	}
    public static ExpressNode splitExpressBlock(List<String> define) throws Exception{
    	NodeType nullNodeType =  new NodeType(null,null,null);
    	Stack<ExpressNode> startNodeStack = new Stack<ExpressNode>();
    	Stack<List<ExpressNode>> childrenStack = new Stack<List<ExpressNode>>();
    	startNodeStack.push(new ExpressNode(nullNodeType,"root"));
    	childrenStack.push(new ArrayList<ExpressNode>());
    	boolean isMatch = false;
    	for(int i=0;i<define.size();i++){
    		String tempStr = define.get(i);
    		isMatch = false;
    		if(tempStr.equals("(")){
    			startNodeStack.push(new ExpressNode(nullNodeType,"()"));
    	    	childrenStack.push(new ArrayList<ExpressNode>());
    		}else if(tempStr.equals(")")){
    			isMatch = true;
    		}else{
    			childrenStack.peek().add(new ExpressNode(nullNodeType,tempStr+""));
    		}
    		if(isMatch == true){
    			startNodeStack.peek().setLeftChildren(childrenStack.pop());
    			childrenStack.peek().add(startNodeStack.pop());
    		}
    	}
    	if(startNodeStack.size() >1){
    		throw new Exception("\""+startNodeStack.peek().getNodeType().getStartTag().getTag() + "\"没有找到对应匹配的符号");
    	}
    	startNodeStack.peek().setLeftChildren(childrenStack.pop());    	
    	return startNodeStack.pop();
    }	
	public static void splitWord(List<String> list,char splitChar){
		for(int i= list.size()-1;i >=0;i--){
			String[] result = NodeTypeManager.splist(list.get(i), splitChar,true);
			if (result.length > 0) {
				for (int j = result.length - 1; j > 0; j--) {
					list.add(i + 1, result[j]);
				}
				list.set(i, result[0]);
			}
		}
	}
}

class MatchResult{
	protected List<MatchResultTree> matchs;
	protected int matchLastIndex;
	protected NodeType statementNodeType;
	public MatchResult(List<MatchResultTree> aList,int aIndex){
		this.matchLastIndex = aIndex;
		this.matchs = aList;
	}
	public String toString(){
		StringBuilder builder = new StringBuilder();
		for(MatchResultTree item:matchs){
		   item.printNode(builder,1);
		}
		return builder.toString();
	}
}
class MatchResultTree{
	NodeType matchNodeType;
	ExpressNode ref;	
	NodeType targetNodeType;
	private List<MatchResultTree> left;
	private List<MatchResultTree> right;	
	
	public MatchResultTree(NodeType aNodeType,ExpressNode aRef,NodeType aTargetNodeType){
		this(aNodeType,aRef);
		this.targetNodeType = aTargetNodeType;
	}
	public MatchResultTree(NodeType aNodeType,ExpressNode aRef){
		this.matchNodeType = aNodeType;
		this.ref = aRef;
	}
	public void addLeft(MatchResultTree node){
		if(this.left == null){
			this.left = new ArrayList<MatchResultTree>();
		}
		this.left.add(node);
	}	
	public void addLeftAll(List<MatchResultTree> list){
		if(this.left == null){
			this.left = new ArrayList<MatchResultTree>();
		}
		this.left.addAll(list);
	}
	public void addRight(MatchResultTree node){
		if(this.right == null){
			this.right = new ArrayList<MatchResultTree>();
		}
		this.right.add(node);
	}
	public void addRightAll(List<MatchResultTree> list){
		if(this.right == null){
			this.right = new ArrayList<MatchResultTree>();
		}
		this.right.addAll(list);
	}
    public ExpressNode transferExpressNodeType(ExpressNode sourceNode,NodeType targetType){
    	sourceNode.setNodeType(targetType);
    	if(targetType == targetType.getManager().findNodeType("CONST_STRING")){
    		sourceNode.setObjectValue(sourceNode.getValue());
    		sourceNode.setTreeType(targetType.getManager().findNodeType("CONST"));
    	}
    	return sourceNode;
    }
	public void buildExpressNodeTree(){
		if(this.targetNodeType != null){
			transferExpressNodeType(this.ref,this.targetNodeType);
		}
		if(this.left != null){
			for (MatchResultTree item : left) {
				this.ref.addLeftChild(item.ref);
				item.buildExpressNodeTree();
			}
		}
		if (this.right != null) {
			for (MatchResultTree item : right) {
				this.ref.addLeftChild(item.ref);
				item.buildExpressNodeTree();
			}
		}
	}
	public String toString(){
		StringBuilder builder = new StringBuilder();
		printNode(builder,1);
		return builder.toString();
	}

	public void printNode(StringBuilder builder, int level) {
		builder.append(level + ":");
		for (int i = 0; i < level; i++) {
			builder.append("   ");
		}
		builder.append(ref.getValue() + ":" + this.matchNodeType.getTag())
				.append("\n");
		if (this.left != null) {
			for (MatchResultTree item : this.left) {
				item.printNode(builder, level + 1);
			}
		}
		if (this.right != null) {
			for (MatchResultTree item : this.right) {
				item.printNode(builder, level + 1);
			}
		}
	}
}
