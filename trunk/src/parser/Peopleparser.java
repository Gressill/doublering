package parser;

import hander.PeopleHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;


public class Peopleparser {
	
	private String _url = "";
	private String _apiKey = "06d5aaf0b4b5f090148100d21e21d1b0";
	private String _userentrylink = "http://api.douban.com/people/";
	private String _uid="";
	
	private PeopleHandler hander;
	public Peopleparser(){
		hander = new PeopleHandler();
	}
	
	public void setURL(String url){
		_url = url;
	}
	public void setURLbyId(String uid){
		if(uid != null)
		_uid = uid;
		if(_uid.length()==0){
			System.out.println("[System Error] uid should not be empty");
		}
		
		_url = _userentrylink + _uid + "?apikey=" + _apiKey;
	}
	
	public void parseUser() throws IOException, ParserConfigurationException,
    SAXException {
	try{
	    URL url = new URL(_url);//北京的城市代码
	    InputStream input = url.openStream();
	    SAXParserFactory factory = SAXParserFactory.newInstance();
	    factory.setNamespaceAware(false);
	    SAXParser parser = factory.newSAXParser();
	    parser.parse(input, hander);
	}catch(FileNotFoundException e){
		System.out.println("[System Error] The resource not found!");
	}
	
	}
	
}
