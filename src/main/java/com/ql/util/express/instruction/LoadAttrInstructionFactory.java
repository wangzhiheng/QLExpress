package com.ql.util.express.instruction;

import java.util.List;
import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.parse.ExpressNode;


public class LoadAttrInstructionFactory extends InstructionFactory {
	public boolean createInstruction(ExpressRunner aCompile,InstructionSet result,
			Stack<ForRelBreakContinue> forStack, ExpressNode node,boolean isRoot)
			throws Exception{
		FunctionInstructionSet functionSet = result.getMacroDefine(node.getValue());
		if(functionSet != null){//�Ǻ궨��
			result.insertInstruction(result.getCurrentPoint()+1, new InstructionCallMacro(node.getValue()));
		}else{
		  result.addInstruction(new InstructionLoadAttr(node.getValue()));
		  if(node.getChildren().length >0){
			  throw new Exception("���ʽ���ô���");
		  }
		}  
		return false;
	}
}
class InstructionLoadAttr extends Instruction{
    String attrName;
    InstructionLoadAttr(String aName){
    	this.attrName = aName;
    }
	public void execute(RunEnvironment environment,List<String> errorList)throws Exception{
		Object o = environment.getContext().getSymbol(this.attrName);
		if(o != null && o instanceof InstructionSet){//�Ǻ�������ִ��
			if(environment.isTrace()){
				log.debug("ָ��ת���� LoadAttr -- >CallMacro ");						
			}
			InstructionCallMacro macro = new InstructionCallMacro(this.attrName);
			macro.setLog(this.log);
			macro.execute(environment, errorList);
		}else{
			if(environment.isTrace()){
				log.debug(this +":" + ((OperateDataAttr)o).getObject(environment.getContext()));						
			}
		    environment.push((OperateDataAttr)o);
		    environment.programPointAddOne();
		}
	}
	public String toString(){
		  return "LoadAttr:" +this.attrName;	
	}
}
