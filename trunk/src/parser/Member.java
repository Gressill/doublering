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

import db.Dbo;


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
		super(str);//���ø��๹�캯��
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
			NodeList list = page.getBody();
			for (NodeIterator i = list.elements(); i.hasMoreNodes(); )
			     processMyNodes(i.nextNode());
		}catch(ParserException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	   showparse();
	   store();
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
	         NodeList n_inside = tag.getChildren ();
	         if (null != n_inside)
	             for (NodeIterator i = n_inside.elements(); i.hasMoreNodes(); )
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
		String[] gtmp = null;
		if(tag.getTagName().equalsIgnoreCase("div"))
        {
       	 if(tag.getAttribute("class") != null && tag.getAttribute("class").equalsIgnoreCase("pl"))
       	 {
       		 tagtext = tag.toPlainTextString();
       		 if(tagtext.trim().substring(0,2).equalsIgnoreCase("id"))
       		 	 gtmp = tag.toPlainTextString().split(" ");
       		 	if(gtmp != null )
       			_mymap.put("username", gtmp[1].toString());
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
			if(tag.getAttribute("href") != null && tag.getAttribute("href").length()>10){
				if(tag.getAttribute("href").substring(1,9).equalsIgnoreCase("location")){
					//System.out.println(tag.toPlainTextString());
					_mymap.put("place", new String(tag.toPlainTextString()));
				}
			}
		}
	}
	/**
	 * store the member info to database;
	 */
	private void store(){
		try{
		String sqlInsert = "INSERT INTO `dl_member`(`username`,`commomname`,`place`) VALUES ('"+
		_mymap.get("username").toString()+"','"+_mymap.get("commomname").toString()+"','"+_mymap.get("place").toString()+"')";
		System.out.println(sqlInsert);
		inserSQL("dl_member","username",_mymap.get("username").toString(),sqlInsert);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
