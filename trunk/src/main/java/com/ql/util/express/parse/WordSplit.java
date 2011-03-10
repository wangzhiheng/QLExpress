
package com.ql.util.express.parse;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 语法解析类
 * 1、单词分解
 * @author xuannan
 *
 */

public class WordSplit
{

	  private static final Log log = LogFactory.getLog(WordSplit.class);
	  
	   public static  void splitOperator(NodeTypeManager nodeManager,String opStr,List<String> objList){
		     String orgiStr = opStr;
		     while (opStr.length() >0){ //处理操作符字串
		          boolean isFind = false;
		          int index =opStr.length();
		          while (index > 0)
		          {
		              if (nodeManager.isExistNodeTypeDefine(opStr.substring(0,index)) != null)
		              {
		            	 objList.add(opStr.substring(0,index));
		                 opStr = opStr.substring(index);
		                 isFind = true;
		                 break;
		              }
		              else
		                index = index - 1;
		          }
		          if(isFind == false){
		        	 log.error("不能识别的符号定义：\"" + opStr.substring(0,1) +"\":" + "\"" + orgiStr +"\"" ); 
		             opStr = opStr.substring(1);
		          }   
		     }
		  }
   protected static boolean isNumber(String str){
    if(str == null || str.equals(""))
      return false;
    char c = str.charAt(0);
    if (c >= '0' && c <= '9') { //数字
      return true;
    }
    else {
      return false;
    }
  }
  /**
   * 文本分析函数，“.”作为操作符号处理
   * @param str String
   * @throws Exception
   * @return String[]
   */
   public static String[] parse(NodeTypeManager nodeManager,String str) throws Exception
  {
    if (str == null){
       return new String[0];
    }
    String tmpWord ="";
    String tmpOpStr ="";
    char c;
    List<String> list = new ArrayList<String>();
    int i= 0;
    while(i<str.length())
    {
       c = str.charAt(i);
      if (c=='"' || c=='\''){//字符串处理        
    	int index = str.indexOf(c,i + 1);
    	//处理字符串中的”问题
        while(index >0 && str.charAt(index - 1) =='\\'){
        	index = str.indexOf(c,index + 1);
        }
        if (index < 0)
        	throw new Exception("字符串没有关闭");
        //先将操作符填入队列，再填充操作数
        splitOperator(nodeManager,tmpOpStr,list);
        tmpOpStr="";
        if (tmpWord.length() >0){
            list.add(tmpWord);
            tmpWord  = "";
        }
        String tempDealStr = str.substring(i,index + 1);
        //处理 \\，\"的情况
        String tmpResult = "";
        int tmpPoint = tempDealStr.indexOf("\\");        
        while(tmpPoint >=0 ){
        	tmpResult = tmpResult + tempDealStr.substring(0,tmpPoint);
        	if(tmpPoint == tempDealStr.length() -1){
        		throw new Exception("字符串中的" + "\\错误:" + tempDealStr);
        	}
        	tmpResult = tmpResult + tempDealStr.substring(tmpPoint + 1 ,tmpPoint + 2);
        	tempDealStr = tempDealStr.substring(tmpPoint + 2);
        	tmpPoint = tempDealStr.indexOf("\\");  
        }
        tmpResult = tmpResult + tempDealStr;
        list.add(tmpResult);
        i = index + 1;
      }else if (((c >='0') && (c <='9'))
            || ((c >='a') && (c <='z'))
            || ((c >='A') && (c <='Z'))
            || (c=='\'')
            || (c=='$')
            || (c=='_')
            || (c > 127))  //标准字符
       {
           tmpWord = tmpWord + c;
           i = i + 1;
           splitOperator(nodeManager,tmpOpStr,list);
           tmpOpStr = "";
       }else if(c=='.' && isNumber(tmpWord) == true){
           //是数字，当数据处理
           tmpWord = tmpWord + c;
           i = i + 1;
           splitOperator(nodeManager,tmpOpStr,list);
           tmpOpStr = "";
       }else{	   
         if (tmpWord.length() >0)
         {    list.add(tmpWord);
              tmpWord = "";
         }
         if(c == ' ' ||c =='\r'|| c =='\n'||c=='\t'||c=='\u000C'){
        	 splitOperator(nodeManager,tmpOpStr,list);
        	 tmpOpStr = "";
         }else{
             tmpOpStr = tmpOpStr + c;
         }
         i = i + 1;
       }
    }

    if (tmpWord.length() >0)
    {    list.add(tmpWord);
         tmpWord = "";
    }
    splitOperator(nodeManager,tmpOpStr,list);

    String result[] = new String[list.size()];
    list.toArray(result);
    return result;
  }

   public static String getPrintInfo(Object[] list,String splitOp){
	  	StringBuffer buffer = new StringBuffer();
		for(int i=0;i<list.length;i++){
			if(i > 0){buffer.append(splitOp);}
			buffer.append("{" + list[i] +"}");
		}
		return buffer.toString();
	  }

}
