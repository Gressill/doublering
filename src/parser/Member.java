package parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.RemarkNode;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.HtmlPage;


public class Member extends Html{
	
	/**
	 * 
	 * private varibles 
	 */
	private Map<String, String> _mymap;
	/**
	 * 
	 * construct function
	 */
	
	public Member(String str){
		//parent;
		super(str);//调用父类构造函数
		_mymap = new HashMap<String, String>();
	}
	/**
	 * 
	 * render member information
	 */
	public void render(){
		try{
			Parser parser = new Parser(getPs());
			HtmlPage page = new HtmlPage(parser);
			parser.visitAllNodesWith(page);
			parsecommonname(page);
			//System.out.println("名号： "+page.getTitle());
			NodeList list = page.getBody();
			for (NodeIterator i = list.elements(); i.hasMoreNodes(); )
			     processMyNodes(i.nextNode());
		}catch(ParserException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	   showparse();
	}
	private void processMyNodes(Node node) throws ParserException
	 {
		 //System.out.println(node.toString());
	     if (node instanceof TagNode)
	     {
	         // downcast to TagNode
	         TagNode tag = (TagNode)node;
	         parseUsernameAndCreateTime(tag);
	         parsePlace(tag);
	         // do whatever processing you want with the tag itself
	         // ...
	         // process recursively (nodes within nodes) via getChildren()
	         NodeList nl = tag.getChildren ();
	         if (null != nl)
	             for (NodeIterator i = nl.elements(); i.hasMoreNodes(); )
	                 processMyNodes(i.nextNode());
	     }
	 }
	private void showparse(){
		for   (Object o  : _mymap.keySet()){    
		    System.out.println(o.toString() + ": " + _mymap.get(o).toString());    
		}
	}
	/**
	 * parse commonname
	 * @param page
	 */
	private void parsecommonname(HtmlPage page){
		_mymap.put("commomname", new String(page.getTitle()));
	}
	
	/**
	 * get member usrname and login time rule.
	 * @param tag
	 * @return
	 */
	private void parseUsernameAndCreateTime(TagNode tag){
		//System.out.println(tag.toPlainTextString());
		String tagtext = null;
		if(tag.getTagName().equalsIgnoreCase("div"))
        {
       	 if(tag.getAttribute("class") != null && tag.getAttribute("class").equalsIgnoreCase("pl"))
       	 {
       		 tagtext = tag.toPlainTextString();
       		 if(tagtext.trim().substring(0,2).equalsIgnoreCase("id"))
       			_mymap.put("username", tag.toPlainTextString());
       	 }
        }
	}
	/**
	 * parse place 
	 * @param tag
	 */
	private void parsePlace(TagNode tag){
		String tagtext = null;
		if(tag.getTagName().equalsIgnoreCase("a")){
			if(tag.getAttribute("href").length()>10){
				if(tag.getAttribute("href").substring(1,9).equalsIgnoreCase("location")){
					//System.out.println(tag.toPlainTextString());
					_mymap.put("place", new String(tag.toPlainTextString()));
				}
			}	
		}
	}
}
