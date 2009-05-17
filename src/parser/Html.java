package parser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import db.Dbo;

public class Html {

	/**
	 * 
	 * private varibles 
	 */
	public Map<String, String> _mymap;
	
	private String _url;
	
	public static Dbo dbo;
	/**
	 * 
	 * construct function
	 * 
	 */
	public Html(String str){
		_url = str;
		_mymap = new HashMap<String, String>();
		try{
		dbo = new Dbo();
		if(dbo.OpenConnection()){
			System.out.println("[System Info] Database connected.");
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * parent value return function
	 * @return parseStr
	 */
	public String getURL(){
		return _url;
	}
	/**
	 * parent value set function
	 * @param string
	 */
	public void setURL(String str){
		_url = str;
	}
	/**
	 * render function: 基本的解析函数
	 * 
	 */
	public void render(){
		try{
			
			Parser parser = new Parser(_url);
			NodeList list = parser.parse (null);
			processParsing(list);
            System.out.println (list.toHtml());
		}
		catch(ParserException pe){
			pe.printStackTrace();
		}
		
	}
	
	private void processParsing(NodeList list){
		try {
			for (NodeIterator i = list.elements(); i.hasMoreNodes(); )
			     processMyNodes(i.nextNode());
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			System.out.println("[System Info] Process function error:");
			e.printStackTrace();
		}
	}
	private void processMyNodes(Node node)
	 {
		 //System.out.println(node.toString());
	     if (node instanceof TagNode)
	     {
	         // downcast to TagNode
	    	 
	         TagNode tag = (TagNode)node;
	         parsingInTag(tag);
	         // do whatever processing you want with the tag itself
	         // ...
	         // process recursively (nodes within nodes) via getChildren()
	         NodeList n_inside = tag.getChildren ();
	         if (null != n_inside)
				try {
					for (NodeIterator i = n_inside.elements(); i.hasMoreNodes(); )
					     processMyNodes(i.nextNode());
				} catch (ParserException e) {
					// TODO Auto-generated catch block
					System.out.println("[System Info] Tag parsing error:");
					e.printStackTrace();
				}
	     }
	 }
	private void parsingInTag(TagNode tag){
		System.out.println(tag.toPlainTextString());
	}
	
	public void showparse(){
		for   (Object o  : _mymap.keySet()){    
		    System.out.println(o.toString() + ": " + _mymap.get(o).toString());    
		}
	}
	
	public void inserSQL(String Tablename, String Idname, String Idvalue, String sql)
	{
		//ResultSet res;
		String testUniqueSQL = "SELECT count(*) AS res_num FROM `" + Tablename + "` WHERE `" + Idname + "`='"+ Idvalue +"'";
		System.out.println(testUniqueSQL);
		if(dbo.OpenConnection()){
			int i = dbo.ExecuteUpdate(sql);
		}
	}
	
	public int getLastid(String Tablename, String idname){
		String lastidSQL = new String("SELECT * FROM `" + Tablename + "` WHERE 1 ORDER BY `" + idname + "` DESC LIMIT 1");
		System.out.println(lastidSQL);
		if(dbo.OpenConnection()){
			ResultSet res = dbo.ExecuteQuery(lastidSQL);
			try {
				while(res.next()){
					int id = res.getInt(idname);
					System.out.println(id);
					return id;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	public void clear(){
		_mymap.clear();
	}
}
