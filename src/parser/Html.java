package parser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.htmlparser.Parser;
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
            System.out.println (list.toHtml());
		}
		catch(ParserException pe){
			pe.printStackTrace();
		}
		
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
