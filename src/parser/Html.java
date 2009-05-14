package parser;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import db.Dbo;

public class Html {
	/**
	 * the parserstr
	 * 
	 */
	private String _url;
	public static Dbo dbo;
	/**
	 * 
	 * construct function
	 * 
	 */
	public Html(String str){
		_url = str;
		try{
		dbo = new Dbo();
		if(dbo.OpenConnection()){
			System.out.println("database connected");
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * parent value return function
	 * @return parseStr
	 */
	public String getPs(){
		return _url;
	}
	/**
	 * parent value set function
	 * @param string
	 */
	public void setPs(String str){
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
            System.out.println (list.toHtml());
		}
		catch(ParserException pe){
			pe.printStackTrace();
		}
		
	}
	
	public void inserSQL(String Tablename, String Idname, String Idvalue, String sql)
	{
		ResultSet res;
		String testUniqueSQL = "SELECT count(*) AS res_num FROM `" + Tablename + "` WHERE `" + Idname + "`='"+ Idvalue +"'";
		System.out.println(testUniqueSQL);
		if(dbo.OpenConnection()){
			int i = dbo.ExecuteUpdate(sql);
		}
		
	}
	
	
}
