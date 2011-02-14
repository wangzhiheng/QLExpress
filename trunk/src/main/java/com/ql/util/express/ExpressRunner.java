package com.ql.util.express;

import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ql.util.express.instruction.ForRelBreakContinue;
import com.ql.util.express.instruction.InstructionFactory;
import com.ql.util.express.instruction.OperatorFactory;
import com.ql.util.express.parse.ExpressNode;
import com.ql.util.express.parse.ExpressParse;
import com.ql.util.express.parse.NodeType;
import com.ql.util.express.parse.NodeTypeManager;

public class ExpressRunner {

	private static final Log log = LogFactory.getLog(ExpressRunner.class);
	
	private boolean isTrace = false;
    private Map<String,InstructionSet> expressInstructionSetCache = new ConcurrentHashMap<String, InstructionSet>();

	private NodeTypeManager manager = new NodeTypeManager();
	private OperatorFactory operatorManager = new OperatorFactory();
	private ExpressParse parse = new ExpressParse(manager);
	public ExpressRunner(boolean aIstrace){
		this.isTrace = aIstrace;
	}
	public ExpressRunner(){
		this.isTrace = false;
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
	public void addFunction(String name, Operator op) {
		this.operatorManager.addOperator(name, op);
	};
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
				aFunctionName, aParameterClassTypes, errorInfo);
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
				aFunctionName, aParameterTypes, errorInfo);
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
				aFunctionName, aParameterClassTypes, errorInfo);

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
				aFunctionName, aParameterTypes, errorInfo);
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

	public void clearExpressCache() {
		this.expressInstructionSetCache.clear();
	}

	public Object execute(InstructionSet[] instructionSets,
			ExpressLoader loader, IExpressContext<String,Object> context, List<String> errorList,
			FuncitonCacheManager aFunctionCacheMananger, boolean isTrace,
			boolean isCatchException, Log aLog) throws Exception {
		 return  InstructionSet.executeOuter(instructionSets,loader,context, errorList, aFunctionCacheMananger, isTrace,isCatchException,aLog);
	}

	public Object execute(String expressString, IExpressContext<String,Object> context,
			List<String> errorList, boolean isCache, boolean isTrace) throws Exception {
		return this.execute(expressString, context, errorList, isCache, isTrace, null);
	}

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
				context, errorList, null, isTrace, false,aLog);
	}

	public InstructionSet parseInstructionSet(String condition)
			throws Exception {
		ExpressNode root = this.parse.parse(condition, isTrace);
		InstructionSet result = createInstructionSet(root, "main");
		if (this.isTrace) {
			log.debug(result);
		}
		return result;
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
