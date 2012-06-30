package com.ql.util.express;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class InstructionSetRunner {
	private static final Log log = LogFactory.getLog(InstructionSetRunner.class);
	  public static Object executeOuter(ExpressRunner runner,InstructionSet[] sets,ExpressLoader loader,
				IExpressContext<String,Object> aContext, List<String> errorList,
				boolean isTrace,boolean isCatchException,
				Log aLog,boolean isSupportDynamicFieldName) throws Exception{
		 return execute(runner,sets, loader, aContext, errorList, isTrace, isCatchException,true, aLog,isSupportDynamicFieldName);
	  }
	  
	  /**
	   * 批量执行指令集合，指令集间可以共享 变量和函数
	   * @param sets
	   * @param aOperatorManager
	   * @param aContext
	   * @param errorList
	   * @param aFunctionCacheMananger
	   * @param isTrace
	   * @return
	   * @throws Exception
	   */
	  public static Object execute(ExpressRunner runner,InstructionSet[] sets,ExpressLoader loader,
				IExpressContext<String,Object> aContext, List<String> errorList,
				boolean isTrace,boolean isCatchException,
				boolean isReturnLastData,Log aLog,boolean isSupportDynamicFieldName)
				throws Exception {
		  InstructionSetContext<String,Object> context = new InstructionSetContext<String, Object>(
					runner,aContext,loader,isSupportDynamicFieldName);
		  Object result = execute(sets,context,errorList,isTrace,isCatchException,isReturnLastData,aLog);
	      return result;
	  }

		public static Object execute(InstructionSet[] sets,
				InstructionSetContext<String,Object> context, List<String> errorList, boolean isTrace,boolean isCatchException,
				boolean isReturnLastData,Log aLog) throws Exception {
			RunEnvironment environmen = null;
			Object result = null;
			for (int i = 0; i < sets.length; i++) {
				InstructionSet tmpSet = sets[i];
				environmen = new RunEnvironment(tmpSet,
						(InstructionSetContext<String,Object>) context, isTrace);
				try {
					CallResult tempResult = tmpSet.excute(environmen, context,
							errorList, i == sets.length - 1,isReturnLastData,aLog);
					if (tempResult.isExit == true) {
						result = tempResult.returnValue;
						break;
					}
				} catch (Exception e) {
					if(isCatchException == true){
						if (aLog != null){
					       aLog.error(e.getMessage(), e);
						}else{
						   log.error(e.getMessage(),e);
						}
					}else{
						throw e;
					}
				}
			}
			return result;

		}
}
