package com.ql.util.express.parse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ql.util.express.ExpressUtil;
import com.ql.util.express.IExpressResourceLoader;

public class ExpressParse {

	private static final Log log = LogFactory.getLog(ExpressParse.class);
	NodeTypeManager nodeTypeManager;
	IExpressResourceLoader expressResourceLoader;
	/**
	 * �Ƿ���Ҫ�߾��ȼ���
	 */
	private boolean isPrecise = false;
	public ExpressParse(NodeTypeManager aNodeTypeManager,IExpressResourceLoader aLoader,boolean aIsPrecise){
		this.nodeTypeManager = aNodeTypeManager;
		this.expressResourceLoader = aLoader;
		this.isPrecise = aIsPrecise;
	}
	protected Word[] getExpressByName(String expressFileName) throws Exception{
		String express = this.expressResourceLoader.loadExpress(expressFileName);
		return WordSplit.parse(nodeTypeManager, express);
	}
	protected  Word[] dealInclude(Word[] wordObjects) throws Exception{
	    boolean isInclude = false;
	    StringBuffer includeFileName = new StringBuffer();
	    int point = 0;
	    List<Word> result = new ArrayList<Word>();
	    while(point <wordObjects.length ){
	      if(wordObjects[point].word.equals("include") ==true){
	    	  isInclude = true;
	    	  includeFileName.setLength(0);
	      }else if(isInclude == true && wordObjects[point].word.equals(";") ==true) {
	    	  isInclude = false;
	    	  Word[] childExpressWord = this.getExpressByName(includeFileName.toString());
	    	  childExpressWord = this.dealInclude(childExpressWord);
	    	  for(int i=0;i< childExpressWord.length;i++){
	    		  result.add(childExpressWord[i]);
	    	  }
	      }else if(isInclude == true){
	    	  includeFileName.append(wordObjects[point].word);
	      }else{
	    	  result.add(wordObjects[point]);
	      }
	      point = point + 1;
	    }
	    return result.toArray(new Word[0]);
	}
	/**
	 * ���е������ͷ���
	 * @param words
	 * @return
	 * @throws Exception
	 */
	public List<ExpressNode> transferWord2ExpressNode(ExpressPackage aRootExpressPackage,Word[] wordObjects) throws Exception{
		List<ExpressNode> result = new ArrayList<ExpressNode>();
		String tempWord;
		NodeType tempType;
		ExpressPackage  tmpImportPackage = new ExpressPackage(aRootExpressPackage);  

	    //�ȴ���import��import��������ļ����ʼ��������;����
	    boolean isImport = false;
	    StringBuffer importName = new StringBuffer();
	    int point = 0;
	    while(point <wordObjects.length ){
	      if(wordObjects[point].word.equals("import") ==true){
	    	  isImport = true;
	    	  importName.setLength(0);
	      }else if(wordObjects[point].word.equals(";") ==true) {
	    	  isImport = false;
	    	  tmpImportPackage.addPackage(importName.toString());
	      }else if(isImport == true){
	    	  importName.append(wordObjects[point].word);
	      }else{
	    	  break;
	      }
	      point = point + 1;
	    }
	    
		String orgiValue = null;
		Object objectValue = null;
		NodeType treeNodeType = null;
		Word tmpWordObject = null;
		while(point <wordObjects.length){
		  tmpWordObject = wordObjects[point];
		  tempWord = wordObjects[point].word;
		  
		  char firstChar = tempWord.charAt(0);
		  char lastChar = tempWord.substring(tempWord.length() - 1).toLowerCase().charAt(0);		  
		  if(firstChar >='0' && firstChar<='9'){
			  if(result.size() >0){//�� ���Ž������⴦��
				  if(result.get(result.size() -1).getValue().equals("-")){
					  if(result.size() == 1 
						 || result.size() >=2 
						    && result.get(result.size() - 2).isTypeEqualsOrChild("OP_LIST")
						    && result.get(result.size() - 2).isTypeEqualsOrChild(")")==false
						    && result.get(result.size() - 2).isTypeEqualsOrChild("]")==false 
						    ){
						  result.remove(result.size() -1);
						  tempWord = "-" + tempWord;
					  }
				  }
			  }
			  if(lastChar =='d'){
				  tempType = nodeTypeManager.findNodeType("CONST_DOUBLE");
				  tempWord = tempWord.substring(0,tempWord.length() -1);
				  if(this.isPrecise == true){
					  objectValue = new BigDecimal(tempWord);
				  }else{
				      objectValue = Double.valueOf(tempWord);
				  }
			  }else if(lastChar =='f'){
				  tempType = nodeTypeManager.findNodeType("CONST_FLOAT");
				  tempWord = tempWord.substring(0,tempWord.length() -1);
				  if(this.isPrecise == true){
					  objectValue = new BigDecimal(tempWord);
				  }else{
				      objectValue = Float.valueOf(tempWord);
				  }
			  }else if(tempWord.indexOf(".") >=0){
				  tempType = nodeTypeManager.findNodeType("CONST_DOUBLE");
				  if(this.isPrecise == true){
					  objectValue = new BigDecimal(tempWord);
				  }else{
					  objectValue = Double.valueOf(tempWord);
				  }
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
				  throw new Exception("û�йرյ��ַ�����" + tempWord);
			  }
			  tempWord = tempWord.substring(1,tempWord.length() -1);
			  tempType =nodeTypeManager.findNodeType("CONST_STRING");
			  objectValue = tempWord;
			  treeNodeType = nodeTypeManager.findNodeType("CONST");
			  point = point + 1;
		  }else if(firstChar =='\''){
			  if(lastChar !='\'' || tempWord.length() <2){
				  throw new Exception("û�йرյ��ַ���" + tempWord);
			  }
			  tempWord = tempWord.substring(1,tempWord.length() -1);
			  
			  treeNodeType = nodeTypeManager.findNodeType("CONST");
			  if(tempWord.length() == 1){ //ת��Ϊ�ַ���
				  tempType =nodeTypeManager.findNodeType("CONST_CHAR");
				  objectValue = tempWord.charAt(0);
			  }else{
				  tempType =nodeTypeManager.findNodeType("CONST_STRING");
				  objectValue = tempWord;
			  }
			  
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
					while (j < wordObjects.length) {
						tmpStr = tmpStr + wordObjects[j].word;
						tmpClass = tmpImportPackage.getClass(tmpStr);
						if (tmpClass != null) {
							point = j + 1;
							isClass = true;
							break;
						}
						if (j < wordObjects.length - 1 && wordObjects[j + 1].word.equals(".") == true) {
							tmpStr = tmpStr + wordObjects[j + 1].word;
							j = j + 2;
							continue;
						} else {
							break;
						}
					}
					if (isClass == true) {
						// ������������
						String arrayStr = "";
						int tmpPoint = point;
						while (tmpPoint < wordObjects.length) {
							if (wordObjects[tmpPoint].word.equals("[]")) {
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
					}else if(this.nodeTypeManager.isFunction(tempWord)){
						tempType = nodeTypeManager.findNodeType("FUNCTION_NAME");
						point = point + 1;
				    }else{
						tempType = nodeTypeManager.findNodeType("ID");
						point = point + 1;
					}
				}else{
					point = point + 1;
				}
		  }	  
		  //System.out.println(tempWord+":" +objectValue + ":" + (objectValue == null?"":objectValue.getClass()));
		  result.add(new ExpressNode(tempType,tempWord,orgiValue,objectValue,treeNodeType,tmpWordObject.line,tmpWordObject.col));
		  treeNodeType = null;
		  objectValue = null;
		  orgiValue = null;
		}
		return result;
	}

	
	/**
	 * �����еı��ʽ������(),[],{},;�ֳ��ӿ�
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
									+ " ��" + tempNode.getValue() + "��ƥ��");
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
    		throw new Exception("\""+startNodeStack.peek().getNodeType().getStartTag().getTag() + "\"û���ҵ���Ӧƥ��ķ���");
    	}
    	startNodeStack.peek().setLeftChildren(childrenStack.pop());
    	
    	return startNodeStack.pop();
    }
    /**
     * �����䣺";" ,"for(){}","if(){}else{}"
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
    				children.remove(startPoint);//�Ƴ�ԭ�еĶ���
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
    	//�������
    	if(root.getTreeType().isEqualsOrChild("{}")
    		 &&	children.get(children.size() - 1).getTreeType().isEqualsOrChild(nodeTypeManager.S_STATEMNET) == false){
    		ExpressNode tempNode = new ExpressNode(nodeTypeManager.findNodeType(";"),";");
    			for(int j= startPoint;j<i;j++){
    				tempNode.addLeftChild(children.get(startPoint));
    				children.remove(startPoint);//�Ƴ�ԭ�еĶ���
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
					}else if(matchResult.matchLastIndex - i == 1){
						i = i+1;
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
			//System.out.println("match:" + statement + ":" + point + ":" + nodes);
    		if(statement == null){ 
    			throw new RuntimeException("û��Ϊ" + aStatementDefines.get(i).getTag() +"�����﷨����DEFINE,��鶨�壺" + aStatementDefines.get(i).getDefineStr());
    		}
    		MatchResult  result = statement.findMatchStatement(this.nodeTypeManager,nodes, point);
    		if(result != null){
    			result.statementNodeType = aStatementDefines.get(i);
    			return result;
    		}
    	}	
    	return null;
    }
    public static void printTreeNode(StringBuilder builder,ExpressNode node, int level){
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
		builder.append("\t"+ node.getTreeType().getTag()).append("\n");
		
		List<ExpressNode> leftChildren = node.getLeftChildren();
		if (leftChildren != null && leftChildren.size() > 0) {
			for (ExpressNode item : leftChildren) {
				printTreeNode(builder,item, level + 1);
			}
		}
		List<ExpressNode> rightChildren = node.getRightChildren();
		if (rightChildren != null && rightChildren.size() > 0) {
			for (ExpressNode item : rightChildren) {
				printTreeNode(builder,item, level + 1);
			}
		}    	
    }
	public static void printTreeNode(ExpressNode node, int level) {
		StringBuilder builder = new StringBuilder();
		printTreeNode(builder,node,level);
		System.out.println(builder.toString());
	}
	public static String printTreeNodeToString(ExpressNode node, int level) {
		StringBuilder builder = new StringBuilder();
		printTreeNode(builder,node,level);
		return builder.toString();
	}
	public ExpressNode parse(ExpressPackage rootExpressPackage,String express,boolean isTrace) throws Exception{
		Word[] words = WordSplit.parse(this.nodeTypeManager,express);
		if(isTrace == true && log.isDebugEnabled()){
			log.debug("ִ�еı��ʽ:" + express);	
			log.debug("���ʷֽ���:" + WordSplit.getPrintInfo(words,","));  
		}
		words = this.dealInclude(words);
		if(isTrace == true && log.isDebugEnabled()){
			log.debug("Ԥ�������:" + WordSplit.getPrintInfo(words,","));  
		}
    	List<ExpressNode> tempList = this.transferWord2ExpressNode(rootExpressPackage,words);
    	if(isTrace == true && log.isDebugEnabled()){
    		log.debug("���ʷ������:" + printInfo(tempList,","));
    	}
    	ExpressNode root = splitExpressBlock(tempList);
    	if(isTrace == true && log.isDebugEnabled()){
    		log.debug("Block��ֺ�Ľ��:");
    		printTreeNode(root,1);
    	}
    	splitStatement(root);    	
    	if(isTrace == true && log.isDebugEnabled()){
    		log.debug("����ֺ�Ľ��:");
    		printTreeNode(root,1);
    	}
		buildExpressTree(root);
    	if(isTrace == true && log.isDebugEnabled()){
    		log.debug("�����﷨��:" );
    		printTreeNode(root,1);
    	}
		return root;
	}

	public static void main(String[] args) throws Exception {
		String condition="/** a **/";
		NodeTypeManager manager = new NodeTypeManager();
		ExpressParse parse = new ExpressParse(manager,null,false);
		Word[] words = WordSplit.parse(manager,condition);
			log.debug("ִ�еı��ʽ:" + condition);	
			log.debug("���ʷֽ���:" + WordSplit.getPrintInfo(words,","));  
    	List<ExpressNode> tempList = parse.transferWord2ExpressNode(null,words);    	
    		log.debug("���ʷ������:" + printInfo(tempList,","));
    	ExpressNode root = parse.splitExpressBlock(tempList);
    	printTreeNode(root,1);
    	List<NodeType> statement = new ArrayList<NodeType>();
    	statement.add(manager.findNodeType("STAT_IF"));
    	MatchResult result = parse.findMatchStatement(root.getLeftChildren(),0,statement);
    	parse.buildStatementTree(root.getLeftChildren(),0,result);
    	System.out.println("ƥ������"+ result);
    	printTreeNode(root,1);

	}
	public static void main2(String[] args) throws Exception {
		String condition="if 1 == 1 then  true ;";
		NodeTypeManager manager = new NodeTypeManager();
		ExpressParse parse = new ExpressParse(manager,null,false);
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

