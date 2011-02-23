package com.ql.util.express.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ql.util.express.ExpressUtil;

public class ExpressParse {

	private static final Log log = LogFactory.getLog(ExpressParse.class);
	NodeTypeManager nodeTypeManager;
	
	public ExpressParse(NodeTypeManager aNodeTypeManager){
		this.nodeTypeManager = aNodeTypeManager;
	}
	
	/**
	 * 进行单词类型分析
	 * @param words
	 * @return
	 * @throws Exception
	 */
	public List<ExpressNode> transferWord2ExpressNode(ExpressPackage aRootExpressPackage,String[] words) throws Exception{
		List<ExpressNode> result = new ArrayList<ExpressNode>();
		String tempWord;
		NodeType tempType;
		ExpressPackage  tmpImportPackage = new ExpressPackage(aRootExpressPackage);  

	    //先处理import，import必须放在文件的最开始，必须以；结束
	    boolean isImport = false;
	    StringBuffer importName = new StringBuffer();
	    int point = 0;
	    while(point <words.length ){
	      if(words[point].equals("import") ==true){
	    	  isImport = true;
	    	  importName.setLength(0);
	      }else if(words[point].equals(";") ==true) {
	    	  isImport = false;
	    	  tmpImportPackage.addPackage(importName.toString());
	      }else if(isImport == true){
	    	  importName.append(words[point]);
	      }else{
	    	  break;
	      }
	      point = point + 1;
	    }
	    
		String orgiValue = null;
		Object objectValue = null;
		NodeType treeNodeType = null;
		while(point <words.length){
		  tempWord = words[point];
		  char firstChar = tempWord.charAt(0);
		  char lastChar = tempWord.substring(tempWord.length() - 1).toLowerCase().charAt(0);		  
		  if(firstChar >='0' && firstChar<='9'){
			  if(result.size() >0){//对 负号进行特殊处理
				  if(result.get(result.size() -1).getValue().equals("-")){
					  if(result.size() == 1 
						 || result.size() >=2 && result.get(result.size() - 2).isTypeEqualsOrChild("OP_LIST")){
						  result.remove(result.size() -1);
						  tempWord = "-" + tempWord;
					  }
				  }
			  }
			  if(lastChar =='d'){
				  tempType = nodeTypeManager.findNodeType("CONST_DOUBLE");
				  tempWord = tempWord.substring(0,tempWord.length() -1);
				  objectValue = Double.valueOf(tempWord);
			  }else if(lastChar =='f'){
				  tempType = nodeTypeManager.findNodeType("CONST_FLOAT");
				  tempWord = tempWord.substring(0,tempWord.length() -1);
				  objectValue = Float.valueOf(tempWord);
			  }else if(tempWord.indexOf(".") >=0){
				  tempType = nodeTypeManager.findNodeType("CONST_FLOAT");
				  objectValue = Float.valueOf(tempWord);
			  }else if(lastChar =='l'){
				  tempType = nodeTypeManager.findNodeType("CONST_LONG");
				  tempWord = tempWord.substring(0,tempWord.length() -1);
				  objectValue = Long.valueOf(tempWord);
			  }else{
				  long tempLong = Long.parseLong(tempWord);
				  if(tempLong > Integer.MAX_VALUE){
					  tempType = nodeTypeManager.findNodeType("CONST_LONG");
					  objectValue = Long.valueOf(tempLong);
				  }else{
					  tempType = nodeTypeManager.findNodeType("CONST_INTEGER");
					  objectValue = Integer.valueOf((int)tempLong);
				  }				  
			  }
			  treeNodeType = nodeTypeManager.findNodeType("CONST");
			  point = point + 1;
		  }else if(firstChar =='"'){
			  if(lastChar !='"' || tempWord.length() <2){
				  throw new Exception("没有关闭的字符串：" + tempWord);
			  }
			  tempWord = tempWord.substring(1,tempWord.length() -1);
			  tempType =nodeTypeManager.findNodeType("CONST_STRING");
			  objectValue = tempWord;
			  treeNodeType = nodeTypeManager.findNodeType("CONST");
			  point = point + 1;
		  }else if(firstChar =='\''){
			  if(lastChar !='\'' || tempWord.length() <2){
				  throw new Exception("没有关闭的字符：" + tempWord);
			  }
			  tempWord = tempWord.substring(1,tempWord.length() -1);
			  tempType =nodeTypeManager.findNodeType("CONST_CHAR");
			  treeNodeType = nodeTypeManager.findNodeType("CONST");
			  if(tempWord.length() >1){
				  throw new Exception("字符定义的长度大于1：\"" + tempWord +  "\"");
			  }
			  objectValue = tempWord.charAt(0);
			  point = point + 1;
		  }else if(tempWord.equals("true") || tempWord.equals("false")){
			  tempType = nodeTypeManager.findNodeType("CONST_BOOLEAN");
			  treeNodeType = nodeTypeManager.findNodeType("CONST");
			  objectValue = Boolean.valueOf(tempWord);
			  point = point + 1;
		  }else {
				tempType = nodeTypeManager.isExistNodeTypeDefine(tempWord);
				if (tempType == null) {
					boolean isClass = false;
					int j = point;
					Class<?> tmpClass = null;
					String tmpStr = "";
					while (j < words.length) {
						tmpStr = tmpStr + words[j];
						tmpClass = tmpImportPackage.getClass(tmpStr);
						if (tmpClass != null) {
							point = j + 1;
							isClass = true;
							break;
						}
						if (j < words.length - 1 && words[j + 1].equals(".") == true) {
							tmpStr = tmpStr + words[j + 1];
							j = j + 2;
							continue;
						} else {
							break;
						}
					}
					if (isClass == true) {
						// 处理数组问题
						String arrayStr = "";
						int tmpPoint = point;
						while (tmpPoint < words.length) {
							if (words[tmpPoint].equals("[]")) {
								arrayStr = arrayStr + "[]";
								tmpPoint = tmpPoint + 1;
							} else {
								break;
							}
						}
						if (arrayStr.length() > 0) {
							tmpStr = tmpStr + arrayStr;
							tmpClass = ExpressUtil.getJavaClass(ExpressUtil.getClassName(tmpClass) + arrayStr);
							point = tmpPoint;
						}
						tempWord = ExpressUtil.getClassName(tmpClass);
						orgiValue = tmpStr;
						tempType = nodeTypeManager.findNodeType("CONST_CLASS");
						objectValue = tmpClass;
					}else{
						tempType = nodeTypeManager.findNodeType("ID");
						point = point + 1;
					}
				}else{
					point = point + 1;
				}
		  }	  
		  //System.out.println(tempWord+":" +objectValue + ":" + (objectValue == null?"":objectValue.getClass()));
		  result.add(new ExpressNode(tempType,tempWord,orgiValue,objectValue,treeNodeType));
		  treeNodeType = null;
		  objectValue = null;
		  orgiValue = null;
		}
		return result;
	}

	
	/**
	 * 将所有的表达式，根据(),[],{},;分成子块
	 * @throws Exception 
	 */
    public ExpressNode splitExpressBlock(List<ExpressNode> nodes) throws Exception{
    	Stack<ExpressNode> startNodeStack = new Stack<ExpressNode>();
    	Stack<List<ExpressNode>> childrenStack = new Stack<List<ExpressNode>>();
    	startNodeStack.push(new ExpressNode(nodeTypeManager.findNodeType("FUNCTION_DEFINE"),"main"));
    	childrenStack.push(new ArrayList<ExpressNode>());
    	boolean isMatch = false;
    	for(int i=0;i<nodes.size();i++){
    		ExpressNode tempNode = nodes.get(i);
    		isMatch = false;
    		if(tempNode.getNodeType().getTag().equals("(")){
    			startNodeStack.push(new ExpressNode(nodeTypeManager.findNodeType("()"),null));
    	    	childrenStack.push(new ArrayList<ExpressNode>());
    		}else if(tempNode.getNodeType().getTag().equals("[")){
    			startNodeStack.push(new ExpressNode(nodeTypeManager.findNodeType("[]"),null));
    	    	childrenStack.push(new ArrayList<ExpressNode>());  
    		}else if(tempNode.getNodeType().getTag().equals("{")){
    			startNodeStack.push(new ExpressNode(nodeTypeManager.findNodeType("{}"),null));
    	    	childrenStack.push(new ArrayList<ExpressNode>());  
    		}else if(tempNode.getNodeType().getTag().equals("/**")){
    			startNodeStack.push(new ExpressNode(nodeTypeManager.findNodeType("COMMENT"),null));
    	    	childrenStack.push(new ArrayList<ExpressNode>());  
    		}else if(tempNode.getNodeType().getTag().equals(")") 
    				|| tempNode.getNodeType().getTag().equals("]")
    				||tempNode.getNodeType().getTag().equals("}")
    				||tempNode.getNodeType().getTag().equals("**/")   ){
    			if (startNodeStack.peek().getNodeType().getEndTag().getTag().equals(tempNode.getNodeType().getTag()) ==false){
					throw new Exception(
							(startNodeStack.peek().getNodeType().getStartTag() == null ? startNodeStack
									.peek().getNodeType().getTag()
									: startNodeStack.peek().getValue())
									+ " 与" + tempNode.getValue() + "不匹配");
				}
    			isMatch = true;
    		}else{
    			childrenStack.peek().add(tempNode);
    		}
    		if(isMatch == true){
    			startNodeStack.peek().setLeftChildren(childrenStack.pop());    	
    			ExpressNode  tempBlockNode = startNodeStack.pop();
    			if(tempBlockNode.getNodeType() != nodeTypeManager.findNodeType("COMMENT") ){
    		    	childrenStack.peek().add(tempBlockNode);
    			}
    		}
    	}
    	if(startNodeStack.size() >1){
    		throw new Exception("\""+startNodeStack.peek().getNodeType().getStartTag().getTag() + "\"没有找到对应匹配的符号");
    	}
    	startNodeStack.peek().setLeftChildren(childrenStack.pop());
    	
    	return startNodeStack.pop();
    }
    /**
     * 拆分语句：";" ,"for(){}","if(){}else{}"
     * @param root
     * @throws Exception 
     */
    public void splitStatement(ExpressNode root) throws Exception{
    	splitStatement(root,root.getLeftChildren());
    	splitStatement(root,root.getRightChildren());   
    	root.setSplitStatement(true);
    }
    public  void splitStatement(ExpressNode root,List<ExpressNode> children) throws Exception{    	
    	if(children == null || children.size() ==0){
    		return;
    	}
    	int startPoint = 0;
    	int i=0;
    	for(ExpressNode tempNode:children){
    		if(tempNode.isSplitStatement() == false){
    		    splitStatement(tempNode);
    		}
    	}
    	while(i<children.size()){
    		ExpressNode tempNode = children.get(i);
    		if(tempNode.getNodeType().getTag().equals(";")){
    			for(int j= startPoint;j<i;j++){    	    		
    				tempNode.addLeftChild(children.get(startPoint));
    				children.remove(startPoint);//移除原有的东东
    			}
    			tempNode.setTreeType(nodeTypeManager.findNodeType("STAT_SEMICOLON"));
    			i = startPoint + 1;
    			startPoint = i;
    		}else{
    			MatchResult matchResult = findMatchStatement(children,i,nodeTypeManager.statementDefine);
    			if(matchResult != null){
    				ExpressNode tempRoot = buildStatementTree(children,i,matchResult);
    				splitStatement(tempRoot);
    				i = i + 1;
    				startPoint = i;
    			}else{
    			  i = i+1;
    			}
    		}
    	}
    	//处理最后
    	if(root.getTreeType().isEqualsOrChild("{}")
    		 &&	children.get(children.size() - 1).getTreeType().isEqualsOrChild(nodeTypeManager.S_STATEMNET) == false){
    		ExpressNode tempNode = new ExpressNode(nodeTypeManager.findNodeType(";"),";");
    			for(int j= startPoint;j<i;j++){
    				tempNode.addLeftChild(children.get(startPoint));
    				children.remove(startPoint);//移除原有的东东
    			}
    		tempNode.setTreeType(nodeTypeManager.findNodeType("STAT_SEMICOLON_EOF"));	
    		children.add(tempNode);	    		
    	}
    }
	public void buildExpressTree(ExpressNode root) throws Exception {
		buildExpressTree(root,root.getLeftChildren());
		buildExpressTree(root,root.getRightChildren());		
	}
	public void buildExpressTree(ExpressNode root,List<ExpressNode> children) throws Exception {
		if (children == null || children.size() == 0) {
			return;
		}
		for (ExpressNode tempNode : children) {
			buildExpressTree(tempNode);
		}
		if (root.getTreeType().isEqualsOrChild("STAT_SEMICOLON")
				|| root.getTreeType().isEqualsOrChild("STAT_SEMICOLON_EOF")
				|| root.getTreeType().isEqualsOrChild("EXPRESS_CHILD")) {
			buildExpressTreeSingle(children);
		}

	}

     public void buildExpressTreeSingle(List<ExpressNode> nodes) throws Exception{
    	if(nodes == null || nodes.size() == 0){
    		return;
    	}        	
		for (List<NodeType> expressLevel : nodeTypeManager.expressDefine){
			int i = 0;
			while (i < nodes.size()) {
				MatchResult matchResult = findMatchStatement(nodes, i, expressLevel);
				if (matchResult != null ) {
					buildStatementTree(nodes, i, matchResult);
					if(i == nodes.size() -1 && nodes.get(i).getRealTreeType() != null){
						break;
					}
				}else{
				    i = i + 1;
				}
			}
		}
	}

    public ExpressNode buildStatementTree(List<ExpressNode> nodes,int point,MatchResult matchResult) throws Exception{
    	ExpressNode root = StatementDefine.builderStatementTree(nodes, point, matchResult);
    	root.setTreeType(matchResult.statementNodeType);
        nodes.set(point,root);
    	return root;
    }
    public MatchResult findMatchStatement(List<ExpressNode> nodes,int point,List<NodeType> aStatementDefines) throws Exception{
    	for(int i =0;i<aStatementDefines.size();i++){
    		StatementDefine statement = aStatementDefines.get(i).getStatementDefine();
    		if(statement == null){ 
    			throw new RuntimeException("没有为" + aStatementDefines.get(i).getTag() +"定义语法规则DEFINE,检查定义：" + aStatementDefines.get(i).getDefineStr());
    		}
    		MatchResult  result = statement.findMatchStatement(this.nodeTypeManager,nodes, point);
    		if(result != null){
    			result.statementNodeType = aStatementDefines.get(i);
    			return result;
    		}
    	}	
    	return null;
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
		builder.append("\t"+ node.getTreeType().getTag());
		
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
	public ExpressNode parse(ExpressPackage rootExpressPackage,String express,boolean isTrace) throws Exception{
		String[] words = WordSplit.parse(this.nodeTypeManager,express);
		if(isTrace == true){
			log.debug("执行的表达式:" + express);	
			log.debug("单词分解结果:" + WordSplit.getPrintInfo(words,","));  
		}
    	List<ExpressNode> tempList = this.transferWord2ExpressNode(rootExpressPackage,words);
    	if(isTrace == true){
    		log.debug("单词分析结果:" + printInfo(tempList,","));
    	}
    	ExpressNode root = splitExpressBlock(tempList);
    	if(isTrace == true){
    		log.debug("Block拆分后的结果:");
    		printTreeNode(root,1);
    	}
    	splitStatement(root);    	
    	if(isTrace == true){
    		log.debug("语句拆分后的结果:");
    		printTreeNode(root,1);
    	}
		buildExpressTree(root);
    	if(isTrace == true){
    		log.debug("最后的语法树:" );
    		printTreeNode(root,1);
    	}
		return root;
	}

	public static void main(String[] args) throws Exception {
		String condition="/** a **/";
		NodeTypeManager manager = new NodeTypeManager();
		ExpressParse parse = new ExpressParse(manager);
		String[] words = WordSplit.parse(manager,condition);
			log.debug("执行的表达式:" + condition);	
			log.debug("单词分解结果:" + WordSplit.getPrintInfo(words,","));  
    	List<ExpressNode> tempList = parse.transferWord2ExpressNode(null,words);    	
    		log.debug("单词分析结果:" + printInfo(tempList,","));
    	ExpressNode root = parse.splitExpressBlock(tempList);
    	printTreeNode(root,1);
    	List<NodeType> statement = new ArrayList<NodeType>();
    	statement.add(manager.findNodeType("STAT_IF"));
    	MatchResult result = parse.findMatchStatement(root.getLeftChildren(),0,statement);
    	parse.buildStatementTree(root.getLeftChildren(),0,result);
    	System.out.println("匹配结果："+ result);
    	printTreeNode(root,1);

	}
	public static void main2(String[] args) throws Exception {
		String condition="if 1 == 1 then  true ;";
		NodeTypeManager manager = new NodeTypeManager();
		ExpressParse parse = new ExpressParse(manager);
		parse.parse(null,condition,true);
  }
	   protected static String printInfo(List<ExpressNode> list,String splitOp){
		  	StringBuffer buffer = new StringBuffer();
			for(int i=0;i<list.size();i++){
				if(i > 0){buffer.append(splitOp);}
				buffer.append(list.get(i));
			}
			return buffer.toString();
		  }
}

