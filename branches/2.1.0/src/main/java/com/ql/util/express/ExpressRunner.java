package com.ql.util.express;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ql.util.express.instruction.ForRelBreakContinue;
import com.ql.util.express.instruction.InstructionFactory;
import com.ql.util.express.instruction.OperatorBase;
import com.ql.util.express.instruction.OperatorFactory;
import com.ql.util.express.parse.ExpressNode;
import com.ql.util.express.parse.ExpressPackage;
import com.ql.util.express.parse.ExpressParse;
import com.ql.util.express.parse.NodeType;
import com.ql.util.express.parse.NodeTypeManager;

/**
 * �﷨�����ͼ���������
 * @author xuannan
 *
 */
public class ExpressRunner {

	private static final Log log = LogFactory.getLog(ExpressRunner.class);
	/**
	 * �Ƿ�������еĸ�����Ϣ��ͬʱ����Ҫlog������DEBUG����
	 */
	private boolean isTrace = false;
	
	/**
	 * �Ƿ���Ҫ�߾��ȼ���
	 */
	private boolean isPrecise = false;
	
	/**
	 * һ���ı���Ӧ��ָ��Ļ���
	 */
    private Map<String,InstructionSet> expressInstructionSetCache = new HashMap<String, InstructionSet>();

    /**
     * �﷨����Ĺ�����
     */
	private NodeTypeManager manager = new NodeTypeManager();
	/**
	 * �������Ĺ�����
	 */
	private OperatorFactory operatorManager;
	/**
	 * �﷨������
	 */
	private ExpressParse parse ;	
	
	/**
	 * ȱʡ��Class���ҵİ�������
	 */
	ExpressPackage rootExpressPackage = new ExpressPackage(null);
	
	/**
	 * 
	 * @param aIstrace �Ƿ����ִ��ָ��Ĺ���
	 */
	public ExpressRunner(boolean aIsPrecise){
		this(aIsPrecise,false);
	}
	/**
	 * 
	 * @param aIsPrecise �Ƿ���Ҫ�߾��ȼ���֧��
	 * @param aIstrace �Ƿ����ִ��ָ��Ĺ���
	 */
	public ExpressRunner(boolean aIsPrecise,boolean aIstrace){
		this.isTrace = aIstrace;
		this.isPrecise = aIsPrecise;
		this.operatorManager = new OperatorFactory(this.isPrecise);
		this.parse =  new ExpressParse(manager,this.isPrecise);
		rootExpressPackage.addPackage("java.lang");
		rootExpressPackage.addPackage("java.util");
	}
	public ExpressRunner(){
		this(false,false);
	}
	/**
	 * ��ȡ�﷨����Ĺ�����
	 * @return
	 */
	public NodeTypeManager getNodeTypeManager(){
		return this.manager;
	}
	/**
	 * ��ȡ�������Ź�����
	 * @return
	 */
	public OperatorFactory getOperatorFactory(){
		return this.operatorManager;
	}
	/**
	 * ��Ӻ�������
	 * @param name ��������
	 * @param op ��Ӧ�Ĳ���ʵ����
	 */
	public void addFunction(String name, OperatorBase op) {
		this.operatorManager.addOperator(name, op);
	};
	/**
	 * ��ȡ�������壬ͨ��������������õ�������˵����Ϣ
	 * @param name ��������
	 * @return
	 */
	public OperatorBase getFunciton(String name){
		return this.operatorManager.getOperator(name);
	}
    /**
     * ���һ����ĺ������壬���磺Math.abs(double) ӳ��Ϊ���ʽ�е� "ȡ����ֵ(-5.0)"
     * @param name ��������
     * @param aClassName ������
     * @param aFunctionName ���еķ�������
     * @param aParameterClassTypes �����Ĳ�����������
     * @param errorInfo �������ִ�еĽ����false����Ҫ����Ĵ�����Ϣ
     * @throws Exception
     */
	public void addFunctionOfClassMethod(String name, String aClassName,
			String aFunctionName, Class<?>[] aParameterClassTypes,
			String errorInfo) throws Exception {
		this.operatorManager.addFunctionOfClassMethod(name, aClassName,
				aFunctionName, aParameterClassTypes,null,null,errorInfo);
	}
    /**
     * ���һ����ĺ������壬���磺Math.abs(double) ӳ��Ϊ���ʽ�е� "ȡ����ֵ(-5.0)"
     * @param name ��������
     * @param aClassName ������
     * @param aFunctionName ���еķ�������
     * @param aParameterClassTypes �����Ĳ�����������
     * @param aParameterDesc �����Ĳ���˵��     
     * @param aParameterAnnotation �����Ĳ���ע��
     * @param errorInfo �������ִ�еĽ����false����Ҫ����Ĵ�����Ϣ
     * @throws Exception
     */
	public void addFunctionOfClassMethod(String name, String aClassName,
			String aFunctionName, Class<?>[] aParameterClassTypes,
			String[] aParameterDesc,String[] aParameterAnnotation,
			String errorInfo) throws Exception {
		this.operatorManager.addFunctionOfClassMethod(name, aClassName,
				aFunctionName, aParameterClassTypes,aParameterDesc,aParameterAnnotation,errorInfo);
	}
    /**
     * ���һ����ĺ������壬���磺Math.abs(double) ӳ��Ϊ���ʽ�е� "ȡ����ֵ(-5.0)"
     * @param name ��������
     * @param aClassName ������
     * @param aFunctionName ���еķ�������
     * @param aParameterTypes �����Ĳ�����������
     * @param errorInfo �������ִ�еĽ����false����Ҫ����Ĵ�����Ϣ
     * @throws Exception
     */
	public void addFunctionOfClassMethod(String name, String aClassName,
			String aFunctionName, String[] aParameterTypes, String errorInfo)
			throws Exception {
		this.operatorManager.addFunctionOfClassMethod(name, aClassName,
				aFunctionName, aParameterTypes,null,null, errorInfo);
	}
    /**
     * ���һ����ĺ������壬���磺Math.abs(double) ӳ��Ϊ���ʽ�е� "ȡ����ֵ(-5.0)"
     * @param name ��������
     * @param aClassName ������
     * @param aFunctionName ���еķ�������
     * @param aParameterTypes �����Ĳ�����������
     * @param aParameterDesc �����Ĳ���˵��     
     * @param aParameterAnnotation �����Ĳ���ע��
     * @param errorInfo �������ִ�еĽ����false����Ҫ����Ĵ�����Ϣ
     * @throws Exception
     */
	public void addFunctionOfClassMethod(String name, String aClassName,
			String aFunctionName, String[] aParameterTypes,
			String[] aParameterDesc,String[] aParameterAnnotation,
			String errorInfo)
			throws Exception {
		this.operatorManager.addFunctionOfClassMethod(name, aClassName,
				aFunctionName, aParameterTypes,aParameterDesc,aParameterAnnotation, errorInfo);
	}
    /**
     * ���ڽ�һ���û��Լ�����Ķ���(����Spring����)����ת��Ϊһ�����ʽ����ĺ���
     * @param name
     * @param aServiceObject
     * @param aFunctionName
     * @param aParameterClassTypes
     * @param errorInfo
     * @throws Exception
     */
	public void addFunctionOfServiceMethod(String name, Object aServiceObject,
			String aFunctionName, Class<?>[] aParameterClassTypes,
			String errorInfo) throws Exception {
		this.operatorManager.addFunctionOfServiceMethod(name, aServiceObject,
				aFunctionName, aParameterClassTypes,null,null,errorInfo);
	}
    /**
     * ���ڽ�һ���û��Լ�����Ķ���(����Spring����)����ת��Ϊһ�����ʽ����ĺ���
     * @param name
     * @param aServiceObject
     * @param aFunctionName
     * @param aParameterClassTypes
     * @param aParameterDesc �����Ĳ���˵��     
     * @param aParameterAnnotation �����Ĳ���ע��
     * @param errorInfo
     * @throws Exception
     */	
	public void addFunctionOfServiceMethod(String name, Object aServiceObject,
			String aFunctionName, Class<?>[] aParameterClassTypes,
			String[] aParameterDesc,String[] aParameterAnnotation,
			String errorInfo) throws Exception {
		this.operatorManager.addFunctionOfServiceMethod(name, aServiceObject,
				aFunctionName, aParameterClassTypes,aParameterDesc,aParameterAnnotation, errorInfo);

	}
    /**
     * ���ڽ�һ���û��Լ�����Ķ���(����Spring����)����ת��Ϊһ�����ʽ����ĺ���
     * @param name
     * @param aServiceObject
     * @param aFunctionName
     * @param aParameterTypes
     * @param errorInfo
     * @throws Exception
     */
	public void addFunctionOfServiceMethod(String name, Object aServiceObject,
			String aFunctionName, String[] aParameterTypes, String errorInfo)
			throws Exception {
		this.operatorManager.addFunctionOfServiceMethod(name, aServiceObject,
				aFunctionName, aParameterTypes,null,null,errorInfo);
	}
	public void addFunctionOfServiceMethod(String name, Object aServiceObject,
			String aFunctionName, String[] aParameterTypes,
			String[] aParameterDesc,String[] aParameterAnnotation,
			String errorInfo)
			throws Exception {
		this.operatorManager.addFunctionOfServiceMethod(name, aServiceObject,
				aFunctionName, aParameterTypes,aParameterDesc,aParameterAnnotation, errorInfo);
	}
	/**
	 * ��Ӳ������ţ��˲������ŵ����ȼ��� "*"��ͬ���﷨��ʽҲ��  data name data
	 * @param name
	 * @param op
	 */
	public void addOperator(String name,Operator op) {
		 this.addOperator(name, "*", op);
	}
	/**
	 * ��Ӳ������ţ��˲�������������Ĳ��ղ������������ȼ�����﷨��ʽ��һ��
	 * @param name ������������
	 * @param aRefOpername ���յĲ������ţ����� "+","--"��
	 * @param op
	 */
	public void addOperator(String name,String aRefOpername,Operator op) {
		this.manager.addOperatorWithLevelOfReference(name, aRefOpername);
		this.operatorManager.addOperator(name, op);
	}

	/**
	 * ��Ӳ������͹ؼ��ֵı�����ͬʱ�Բ���������ָ��������Ϣ��
	 * ���磺addOperatorWithAlias("��","+",null)
	 * @param keyWordName
	 * @param realKeyWordName
	 * @param errorInfo
	 * @throws Exception
	 */
	public void addOperatorWithAlias(String keyWordName, String realKeyWordName,
			String errorInfo) throws Exception {
		NodeType realNodeType = this.manager.findNodeType(realKeyWordName);
		if(realNodeType == null){
			throw new Exception("�ؼ��֣�" + realKeyWordName +"������");			
		}
		if(errorInfo != null && errorInfo.trim().length() == 0){
			errorInfo = null;
		}
		boolean isExist = this.operatorManager.isExistOperator(realNodeType.getTag());
		if(isExist == false &&  errorInfo != null){
			throw new Exception("�ؼ��֣�" + realKeyWordName +"��ͨ��ָ����ʵ�ֵģ��������ô������ʾ��Ϣ��errorInfo ������ null");
		}
		if(isExist == false || errorInfo == null){
			//����Ҫ�����������ţ�ֻ��Ҫ����һ���ؼ��Ӽ���
			this.manager.addOperatorWithRealNodeType(keyWordName, realNodeType.getTag());
		}else{
			this.manager.addOperatorWithLevelOfReference(keyWordName, realNodeType.getTag());		
			this.operatorManager.addOperatorWithAlias(keyWordName, realNodeType.getTag(), errorInfo);
		}
	}
		/**
	 * �滻����������
	 * @param name
	 */
    	public OperatorBase replaceOperator(String name,Operator op){
    		return this.operatorManager.replaceOperator(name, op);
    	}
	public ExpressPackage getRootExpressPackage(){
		return this.rootExpressPackage;
	}
	  /**
	   * �������
	   */
	public void clearExpressCache() {
		this.expressInstructionSetCache.clear();
	}

	public Object execute(InstructionSet[] instructionSets,
			ExpressLoader loader, IExpressContext<String,Object> context, List<String> errorList,
			boolean isTrace,boolean isCatchException, Log aLog) throws Exception {
		 return  InstructionSet.executeOuter(instructionSets,loader,context, errorList,
				 	isTrace,isCatchException,aLog,false);
	}
/**
 * ִ��һ���ı�
 * @param expressString �����ı�
 * @param context ִ��������
 * @param errorList ����Ĵ�����ϢList
 * @param isCache �Ƿ�ʹ��Cache�е�ָ�
 * @param isTrace �Ƿ������ϸ��ִ��ָ����Ϣ
 * @return
 * @throws Exception
 */
	public Object execute(String expressString, IExpressContext<String,Object> context,
			List<String> errorList, boolean isCache, boolean isTrace) throws Exception {
		return this.execute(expressString, context, errorList, isCache, isTrace, null);
	}
/**
 * ִ��һ���ı�
 * @param expressString �����ı�
 * @param context ִ��������
 * @param errorList ����Ĵ�����ϢList
 * @param isCache �Ƿ�ʹ��Cache�е�ָ�
 * @param isTrace �Ƿ������ϸ��ִ��ָ����Ϣ
 * @param aLog �����log
 * @return
 * @throws Exception
 */
	public Object execute(String expressString, IExpressContext<String,Object> context,
			List<String> errorList, boolean isCache, boolean isTrace, Log aLog)
			throws Exception {
		InstructionSet parseResult = null;
		if (isCache == true) {
			parseResult = expressInstructionSetCache.get(expressString);
			if (parseResult == null) {
				synchronized (expressInstructionSetCache) {
					parseResult = expressInstructionSetCache.get(expressString);
					if (parseResult == null) {
						parseResult = this.parseInstructionSet(expressString);
						expressInstructionSetCache.put(expressString,
								parseResult);
					}
				}
			}
		} else {
			parseResult = this.parseInstructionSet(expressString);
		}
		return this.execute(new InstructionSet[] { parseResult }, null,
				context, errorList, isTrace, false,aLog);
	}

	/**
	 * ����һ���ı�������ָ���
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public InstructionSet parseInstructionSet(String text)
			throws Exception {
		ExpressNode root = this.parse.parse(this.rootExpressPackage,text, isTrace);
		checkExpressNode(root);
		InstructionSet result = createInstructionSet(root, "main");
		if (this.isTrace) {
			log.debug(result);
		}
		return result;
	}

/**
 * ����﷨���Ƿ���ȷ
 * @param aRoot
 * @throws Exception 
 */
    public void checkExpressNode(ExpressNode aRoot) throws Exception{
    	ExpressNode[] children = aRoot.getChildren();
    	if(aRoot.isTypeEqualsOrChild("STAT_SEMICOLON") || aRoot.isTypeEqualsOrChild("STAT_SEMICOLON_EOF")){
    		if(children.length >1){
    			throw new Exception("�﷨��������" + aRoot.getTreeType().getName() +" Ӧ��ֻ��һ�����ӽڵ�:\n" 
    					+ ExpressParse.printTreeNodeToString(aRoot, 0)); 
    		}
    	}
    	for(ExpressNode item:children){
    		checkExpressNode(item);
    	}
    }
	public InstructionSet createInstructionSet(ExpressNode root, String type)
			throws Exception {
		InstructionSet result = new InstructionSet(type);
		createInstructionSet(root, result);
		return result;
	}

	public void createInstructionSet(ExpressNode root, InstructionSet result)
			throws Exception {
		Stack<ForRelBreakContinue> forStack = new Stack<ForRelBreakContinue>();
		createInstructionSetPrivate(result, forStack, root, true);
		if (forStack.size() > 0) {
			throw new Exception("For�������");
		}
	}

	public boolean createInstructionSetPrivate(InstructionSet result,
			Stack<ForRelBreakContinue> forStack, ExpressNode node,
			boolean isRoot) throws Exception {
		InstructionFactory factory = InstructionFactory
				.getInstructionFactory(node.getInstructionFactory());
		boolean hasLocalVar = factory.createInstruction(this,result, forStack, node, isRoot);
		return hasLocalVar;
	}
}
