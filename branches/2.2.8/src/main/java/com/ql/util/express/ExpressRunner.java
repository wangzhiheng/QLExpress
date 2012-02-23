package com.ql.util.express;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ql.util.express.instruction.ForRelBreakContinue;
import com.ql.util.express.instruction.InstructionFactory;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.instruction.op.OperatorFactory;
import com.ql.util.express.instruction.op.OperatorMinMax;
import com.ql.util.express.instruction.op.OperatorPrint;
import com.ql.util.express.instruction.op.OperatorPrintln;
import com.ql.util.express.instruction.op.OperatorRound;
import com.ql.util.express.instruction.op.OperatorSelfDefineClassFunction;
import com.ql.util.express.instruction.op.OperatorSelfDefineServiceFunction;
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
	private static final String GLOBAL_DEFINE_NAME="ȫ�ֶ���";
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
    
    private ExpressLoader loader;
    private IExpressResourceLoader expressResourceLoader;
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
	
	public ExpressRunner(){
		this(false,false);
	}
	/**
	 * 
	 * @param aIsPrecise �Ƿ���Ҫ�߾��ȼ���֧��
	 * @param aIstrace �Ƿ����ִ��ָ��Ĺ���
	 */
	public ExpressRunner(boolean aIsPrecise,boolean aIstrace){
		this(aIsPrecise,aIstrace,new DefaultExpressResourceLoader());
	}
	/**
	 * 
	 * @param aIsPrecise �Ƿ���Ҫ�߾��ȼ���֧��
	 * @param aIstrace �Ƿ����ִ��ָ��Ĺ���
	 * @param aExpressResourceLoader ���ʽ����Դװ����
	 */
	public ExpressRunner(boolean aIsPrecise,boolean aIstrace,IExpressResourceLoader aExpressResourceLoader){
		this.isTrace = aIstrace;
		this.isPrecise = aIsPrecise;
		this.expressResourceLoader = aExpressResourceLoader;
		this.operatorManager = new OperatorFactory(this.isPrecise);
		this.loader = new ExpressLoader(this);
		this.parse =  new ExpressParse(manager,this.expressResourceLoader,this.isPrecise);
		rootExpressPackage.addPackage("java.lang");
		rootExpressPackage.addPackage("java.util");
		this.addSystemFunctions();
	}	
	public void addSystemFunctions(){	
		  this.addFunction("max", new OperatorMinMax("max"));	
		  this.addFunction("min", new OperatorMinMax("min"));	
		  this.addFunction("round", new OperatorRound("round"));
		  this.addFunction("print", new OperatorPrint("print"));
		  this.addFunction("println", new OperatorPrintln("println"));
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
	public IExpressResourceLoader getExpressResourceLoader(){
		return this.expressResourceLoader;
	}
	/**
	 * ��Ӻ궨�� ���磺 macro ���� { abc(userinfo.userId);}
	 * @param macroName������
	 * @param express ��abc(userinfo.userId);
	 * @throws Exception 
	 */
	public void addMacro(String macroName,String express) throws Exception{		
		String macroExpress = "macro " + macroName  +" {" + express + "}";
		this.loader.parseInstructionSet(GLOBAL_DEFINE_NAME,macroExpress);
	}
	
	/**
	 * װ�ر��ʽ������ִ�У�����һЩ�궨�壬�����Զ��庯��
	 * @param groupName
	 * @param express
	 * @throws Exception
	 */
	public void loadMutilExpress(String groupName,String express) throws Exception{		
		if(groupName == null || groupName.trim().length() ==0){
			groupName = GLOBAL_DEFINE_NAME;
		}	
		this.loader.parseInstructionSet(groupName,express);
	}
    /**
     * װ���ļ��ж����Express
     * @param fileName
     * @throws Exception
     */
	public void loadExpress(String expressName) throws Exception {
		this.loader.loadExpress(expressName);
	}
	/**
	 * ��Ӻ�������
	 * @param name ��������
	 * @param op ��Ӧ�Ĳ���ʵ����
	 */
	public void addFunction(String name, OperatorBase op) {
		this.operatorManager.addOperator(name, op);
		this.manager.addFunctionName(name);
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
		this.addFunction(name, new OperatorSelfDefineClassFunction(name,
				aClassName, aFunctionName, aParameterClassTypes,null,null, errorInfo));
		
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
		this.addFunction(name, new OperatorSelfDefineClassFunction(name,
				aClassName, aFunctionName, aParameterClassTypes,aParameterDesc,aParameterAnnotation, errorInfo));

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
		this.addFunction(name, new OperatorSelfDefineClassFunction(name,
				aClassName, aFunctionName, aParameterTypes, null,null,errorInfo));		
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
		this.addFunction(name, new OperatorSelfDefineClassFunction(name,
				aClassName, aFunctionName, aParameterTypes, aParameterDesc,aParameterAnnotation,errorInfo));		
	
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
		this.addFunction(name, new OperatorSelfDefineServiceFunction(name,
				aServiceObject, aFunctionName, aParameterClassTypes,null,null, errorInfo));
		
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
		this.addFunction(name, new OperatorSelfDefineServiceFunction(name,
				aServiceObject, aFunctionName, aParameterClassTypes,aParameterDesc,aParameterAnnotation, errorInfo));

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
		this.addFunction(name, new OperatorSelfDefineServiceFunction(name,
				aServiceObject, aFunctionName, aParameterTypes,null,null, errorInfo));

	}
	public void addFunctionOfServiceMethod(String name, Object aServiceObject,
			String aFunctionName, String[] aParameterTypes,
			String[] aParameterDesc,String[] aParameterAnnotation,
			String errorInfo)
			throws Exception {
		this.addFunction(name, new OperatorSelfDefineServiceFunction(name,
				aServiceObject, aFunctionName, aParameterTypes,aParameterDesc,aParameterAnnotation, errorInfo));

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
		if(errorInfo != null && errorInfo.trim().length() == 0){
			errorInfo = null;
		}
		//��Ӻ�������
		if(this.manager.isFunction(realKeyWordName)){
			this.manager.addFunctionName(keyWordName);
			this.operatorManager.addOperatorWithAlias(keyWordName, realKeyWordName, errorInfo);
			return;
		}
		NodeType realNodeType = this.manager.findNodeType(realKeyWordName);
		if(realNodeType == null){
			throw new Exception("�ؼ��֣�" + realKeyWordName +"������");			
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
	/**
	 * ���ݱ��ʽ�����ƽ���ִ��
	 * @param name
	 * @param context
	 * @param errorList
	 * @param isTrace
	 * @param isCatchException
	 * @param aLog
	 * @return
	 * @throws Exception
	 */
	public Object executeByExpressName(String name,IExpressContext<String,Object> context, List<String> errorList,
			boolean isTrace,boolean isCatchException, Log aLog) throws Exception {
		return this.executeByExpressName(new String[]{name}, context, errorList, isTrace, isCatchException, aLog);
	}
	/**
	 * �Ѽ������ʽ��֯��һ�����ʽ��ִ��
	 * @param setsNames
	 * @param context
	 * @param errorList
	 * @param isTrace
	 * @param isCatchException
	 * @param aLog
	 * @return
	 * @throws Exception
	 */
	public Object executeByExpressName(String[] setsNames,IExpressContext<String,Object> context, List<String> errorList,
			boolean isTrace,boolean isCatchException, Log aLog) throws Exception {
		InstructionSet[] instructionSets = new InstructionSet[setsNames.length];
		for(int i=0;i< setsNames.length;i++){
			instructionSets[i] = this.loader.getInstructionSet(setsNames[i]);
		}
		return this.execute(instructionSets, context, errorList, isTrace, isCatchException, aLog);
	}	
    
	/**
	 * ִ��ָ�
	 * @param instructionSets
	 * @param context
	 * @param errorList
	 * @param isTrace
	 * @param isCatchException
	 * @param aLog
	 * @return
	 * @throws Exception
	 */
	public Object execute(InstructionSet[] instructionSets,IExpressContext<String,Object> context, List<String> errorList,
			boolean isTrace,boolean isCatchException, Log aLog) throws Exception {
		return  InstructionSetRunner.executeOuter(this,instructionSets,this.loader,context, errorList,
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
		return this.execute(new InstructionSet[] { parseResult },
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
		Map<String,String> selfDefineClass = new HashMap<String,String> ();
		for(ExportItem  item : this.loader.getExportInfo()){
			if(item.getType().equals(InstructionSet.TYPE_CLASS)){
				selfDefineClass.put(item.getName(), item.getName());
			}
		}
		
		ExpressNode root = this.parse.parse(this.rootExpressPackage,text, isTrace,selfDefineClass);
		checkExpressNode(root);
		InstructionSet result = createInstructionSet(root, "main");
		if (this.isTrace && log.isDebugEnabled()) {
			log.debug(result);
		}
		return result;
	}
	/**
	 * ���ȫ�ֶ�����Ϣ
	 * @return
	 */
	public ExportItem[] getExportInfo(){
		return this.loader.getExportInfo();
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
	/**
	 * ��ȡһ�����ʽ��Ҫ���ⲿ���������б�
	 * @param express
	 * @return
	 * @throws Exception 
	 */
	public String[] getOutVarNames(String express) throws Exception{
		return this.parseInstructionSet(express).getOutAttrNames();
	}
}
