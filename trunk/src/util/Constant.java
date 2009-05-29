package util;
import java.io.File;
import java.util.ArrayList;
import java.util.Queue;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
public class Constant {
	public  static String DB_HOST     = "127.0.0.1"; 
	public  static String DB_DATABASE = "doublering";
	public  static String DB_USER_NAME = "ctottrunsql";
	public  static String DB_PASSWORD  = "ctottruningsql";
	public  static String spideobject;
	public  static String api;
	public  static String secret;
	public  static int    min_id;
	public  static int    max_id;
	public  static int    seed;
	
	public static boolean initGameFromXml() {
		// long lasting = System.currentTimeMillis();
		try {
			//get path
			String userdirString = System.getProperty("user.dir");
			File configfile = new File(userdirString+"/config.xml");
			System.out.println(userdirString);
			SAXReader reader = new SAXReader();
			Document doc = reader.read(configfile);
			Element root = doc.getRootElement();
			DB_HOST      = root.element("host").getTextTrim();
			DB_DATABASE  = root.element("database").getTextTrim();
			DB_USER_NAME = root.element("username").getTextTrim();
			DB_PASSWORD = root.element("password").getTextTrim();
			spideobject = root.element("spideobject").getTextTrim();
			api         = root.element("api").getTextTrim();
			secret      = root.element("secret").getTextTrim();
			min_id      = new Integer(root.element("fromid").getTextTrim());
			max_id      = new Integer(root.element("toid").getTextTrim());
			seed        = new Integer(root.element("seed").getTextTrim());
			//System.out.println("port:" + port + "\ndatabase" + DB__DATABASE+ "\nusername:" + DB_USER_NAME + "\npassword:" + DB_PASSWORD);
			System.out.println("[System Msgs]: Load config file succeed. The config argument is: ");
			System.out.println("DATABASE: "+DB_DATABASE+"\n DB_USER_NAME: "+DB_USER_NAME+"\n DB_PASSWORD: "+DB_PASSWORD+"\n Spideobject: "+spideobject);
			return true;
		} catch (Exception e) {
			System.out.println("[System Msgs]: Load config file error:"+e.getMessage());
			return false;
		}
	}
}
