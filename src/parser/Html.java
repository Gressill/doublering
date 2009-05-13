package parser;

import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class Html {
	/**
	 * the parserstr
	 * 
	 */
	private String parseStr;
	/**
	 * 
	 * construct function
	 * 
	 */
	public Html(String str){	
		parseStr = str;
	}
	/**
	 * parent value return function
	 * @return parseStr
	 */
	public String getPs(){
		return parseStr;
	}
	/**
	 * parent value set function
	 * @param string
	 */
	public void setPs(String str){
		parseStr = str;
	}
	/**
	 * render function: 基本的解析函数
	 * 
	 */
	public void render(){
		try{
			
			Parser parser = new Parser(parseStr);
			NodeList list = parser.parse (null);
            System.out.println (list.toHtml());
		}
		catch(ParserException pe){
			pe.printStackTrace();
		}
		
	}
	
	
}
