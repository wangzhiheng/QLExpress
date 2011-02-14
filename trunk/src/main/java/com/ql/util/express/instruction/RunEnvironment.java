package com.ql.util.express.instruction;

import java.util.Arrays;

import com.ql.util.express.InstructionSet;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;


public class RunEnvironment {
	    private boolean isTrace = false;	
		private int point = -1;
	    private int programPoint = 0;
		private OperateData[] dataContainer;
		
		private boolean isExit = false;
		private Object returnValue = null; 
		
		private InstructionSet instructionSet;
		private InstructionSetContext<String, Object> context;
		
		
		public RunEnvironment(int aStackSize,InstructionSet aInstructionSet,InstructionSetContext<String, Object> aContext,boolean aIsTrace){
			if(aStackSize <0){
				aStackSize =0;
			}
			dataContainer = new OperateData[aStackSize];
			this.instructionSet = aInstructionSet;
			this.context = aContext;
			this.isTrace = aIsTrace;
		}
		
		
		public InstructionSet getInstructionSet() {
			return instructionSet;
		}


		public InstructionSetContext<String, Object> getContext(){
			return this.context;
		}
		public void setContext(InstructionSetContext<String, Object> aContext){
			this.context = aContext;
		}

		public boolean isExit() {
			return isExit;
		}
		public Object getReturnValue() {
			return returnValue;
		}
		public void setReturnValue(Object value){
			this.returnValue = value;
		}
		public void quitExpress(Object aReturnValue){
			this.isExit = true;
			this.returnValue = aReturnValue;
		}
		public boolean isTrace(){
			return this.isTrace;
		}
		public int getProgramPoint() {
			return programPoint;
		}
		public void programPointAddOne() {
			programPoint ++ ;
		}
	    public int getDataStackSize(){
	    	return this.point + 1;
	    }
		public void push(OperateData data){
			this.point++;
			if(this.point >= this.dataContainer.length){
			   ensureCapacity(this.point + 1);
			}
			this.dataContainer[point] = data;
		}
		public OperateData peek(){
			if(point <0){
				throw new RuntimeException("系统异常，堆栈指针错误");
			}
			return this.dataContainer[point];		
		}
		public OperateData pop(){
			if(point <0)
				throw new RuntimeException("系统异常，堆栈指针错误");
			OperateData result = this.dataContainer[point];
			this.point--;
			return result;
		}
		public void clearDataStack(){
			this.point = -1;
		}
		public void gotoWithOffset(int aOffset ){
			this.programPoint = this.programPoint + aOffset;
		}

		public OperateData[] popArray(InstructionSetContext<String, Object> context,int len) throws Exception {
			OperateData[] result = new OperateData[len];
			int start = point - len + 1;
			if(start <0){
				throw new Exception("堆栈溢出，请检查表达式是否错误");
			}
			for (int i = 0 ; i < len; i++) {
				result[i] = this.dataContainer[start + i];
				if(void.class.equals(result[i].getType(context))){
					throw new Exception("void 不能参与任何操作运算,请检查使用在表达式中使用了没有返回值的函数,或者分支不完整的if语句");
				}
			}
			point = point - len;
			return result;
		}

		public void ensureCapacity(int minCapacity) {
			int oldCapacity = this.dataContainer.length;
			if (minCapacity > oldCapacity) {
				int newCapacity = (oldCapacity * 3) / 2 + 1;
				if (newCapacity < minCapacity){
					newCapacity = minCapacity;
				}
				this.dataContainer = Arrays.copyOf(this.dataContainer, newCapacity);
			}
		}
	}
