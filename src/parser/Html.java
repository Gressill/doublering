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
	 * render function
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
