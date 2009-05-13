package parser;

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
	
	/**
	 * 
	 * construct function
	 */
	
	public Member(String str){
		//parent;
		super(str);//调用父类构造函数
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
			System.out.println("名号： "+page.getTitle());
			NodeList list = page.getBody();
			for (NodeIterator i = list.elements(); i.hasMoreNodes(); )
			     processMyNodes(i.nextNode());
		}catch(ParserException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void processMyNodes(Node node) throws ParserException
	 {
		 System.out.println(node.toString());
		
	     if (node instanceof TagNode)
	     {
	         // downcast to TagNode
	         TagNode tag = (TagNode)node;
	         parseUsernameAndCreateTime(tag);
	         
	         // do whatever processing you want with the tag itself
	         // ...
	         // process recursively (nodes within nodes) via getChildren()
	         NodeList nl = tag.getChildren ();
	         if (null != nl)
	             for (NodeIterator i = nl.elements(); i.hasMoreNodes(); )
	                 processMyNodes(i.nextNode());
	     }
	 }
	
	/**
	 * get member usrname and login time rule.
	 * @param tag
	 * @return
	 */
	private void parseUsernameAndCreateTime(TagNode tag){
		System.out.println(tag.toPlainTextString());
		if(checkByTagAndAtrr(tag,"div","class","pl")){
			//write to the database or print on the screen.
	        System.out.println("username及加入时间："+tag.toPlainTextString());
		}
	}
	/**
	 * checkTag
	 * @param tag
	 * @param tagname
	 * @param atrrname
	 * @param atrrvalue
	 * @return
	 */
	private boolean checkByTagAndAtrr(TagNode tag, String tagname, String atrrname, String atrrvalue)
	{
		String tagtext = null;
		if(tag.getTagName().equalsIgnoreCase(tagname))
        {
       	 if(tag.getAttribute(atrrname) != null && tag.getAttribute(atrrname).equalsIgnoreCase(atrrvalue))
       	 {
       		 tagtext = tag.toPlainTextString();
       	 	 System.out.println(tagtext);
       		 //if(tagtext.matches("id:*"))
       	 	 return true;
       	 }
        }
		return false;
	}

}
