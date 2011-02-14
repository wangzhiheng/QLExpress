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
	 * 获取语法定义的管理器
	 * @return
	 */
	public NodeTypeManager getNodeTypeManager(){
		return this.manager;
	}
	/**
	 * 获取操作符号管理器
	 * @return
	 */
	public OperatorFactory getOperatorFactory(){
		return this.operatorManager;
	}
	/**
	 * 添加函数定义
	 * @param name 函数名称
	 * @param op 对应的操作实现类
	 */
	public void addFunction(String name, Operator op) {
		this.operatorManager.addOperator(name, op);
	};
    /**
     * 添加一个类的函数定义，例如：Math.abs(double) 映射为表达式中的 "取绝对值(-5.0)"
     * @param name 函数名称
     * @param aClassName 类名称
     * @param aFunctionName 类中的方法名称
     * @param aParameterClassTypes 方法的参数类型名称
     * @param errorInfo 如果函数执行的结果是false，需要输出的错误信息
     * @throws Exception
     */
	public void addFunctionOfClassMethod(String name, String aClassName,
			String aFunctionName, Class<?>[] aParameterClassTypes,
			String errorInfo) throws Exception {
		this.operatorManager.addFunctionOfClassMethod(name, aClassName,
				aFunctionName, aParameterClassTypes, errorInfo);
	}
    /**
     * 添加一个类的函数定义，例如：Math.abs(double) 映射为表达式中的 "取绝对值(-5.0)"
     * @param name 函数名称
     * @param aClassName 类名称
     * @param aFunctionName 类中的方法名称
     * @param aParameterTypes 方法的参数类型名称
     * @param errorInfo 如果函数执行的结果是false，需要输出的错误信息
     * @throws Exception
     */
	public void addFunctionOfClassMethod(String name, String aClassName,
			String aFunctionName, String[] aParameterTypes, String errorInfo)
			throws Exception {
		this.operatorManager.addFunctionOfClassMethod(name, aClassName,
				aFunctionName, aParameterTypes, errorInfo);
	}
    /**
     * 用于将一个用户自己定义的对象(例如Spring对象)方法转换为一个表达式计算的函数
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
     * 用于将一个用户自己定义的对象(例如Spring对象)方法转换为一个表达式计算的函数
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
	 * 添加操作符号，此操作符号的优先级与 "*"相同，语法形式也是  data name data
	 * @param name
	 * @param op
	 */
	public void addOperator(String name,Operator op) {
		 this.addOperator(name, "*", op);
	}
	/**
	 * 添加操作符号，此操作符号与给定的参照操作符号在优先级别和语法形式上一致
	 * @param name 操作符号名称
	 * @param aRefOpername 参照的操作符号，例如 "+","--"等
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
			throw new Exception("关键字：" + realKeyWordName +"不存在");			
		}
		if(errorInfo != null && errorInfo.trim().length() == 0){
			errorInfo = null;
		}
		boolean isExist = this.operatorManager.isExistOperator(realNodeType.getTag());
		if(isExist == false &&  errorInfo != null){
			throw new Exception("关键字：" + realKeyWordName +"是通过指令来实现的，不能设置错误的提示信息，errorInfo 必须是 null");
		}
		if(isExist == false || errorInfo == null){
			//不需要新增操作符号，只需要建立一个关键子即可
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
			throw new Exception("For处理错误");
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
