package parser;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Baseparser {
	
	private String _url = null;
	
	public Baseparser(){
		_url = "http://api.douban.com/people/1000001?apikey=06d5aaf0b4b5f090148100d21e21d1b0";
	}
	
	public void sendRE() throws IOException, ParserConfigurationException,
    SAXException {
	try{
	    URL url = new URL(_url);//北京的城市代码
	    InputStream input = url.openStream();
	    SAXParserFactory factory = SAXParserFactory.newInstance();
	    factory.setNamespaceAware(false);
	    SAXParser parser = factory.newSAXParser();
	    parser.parse(input, new BaseHandler());
	}catch(FileNotFoundException e){
		System.out.println("[System Error] The resource not found!");
	}
	
	}
	
    public class BaseHandler extends DefaultHandler {//继承DefaultHandler重写startElement解析XML

        public void startElement(String uri, String localName, String qName,
                                 org.xml.sax.Attributes attributes) throws
                SAXException {
        	System.out.println("[System info]"+localName);
        	System.out.println("[System info]"+localName);
        	if ("link".equals(qName)){
        		String s_id = attributes.getValue("href");
        		System.out.println("[System info]"+s_id);
        	}
        	
            if ("yweather:condition".equals(qName)) {
                String s_date = attributes.getValue(3);
                System.out.println("[System info]"+attributes.getValue(0));
                System.out.println("[System info]"+attributes.getValue(1));
                System.out.println("[System info]"+attributes.getValue("date"));
                System.out.println("[System info]"+s_date);
                try {
                    Date publish = new SimpleDateFormat(
                            "EEE, dd MMM yyyy hh:mm a z",
                            Locale.US).parse(s_date);
                    System.out.println("Publish: " + publish.getDate());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new SAXException("Cannot parse date: " + s_date);
                }
            } else if ("yweather:location".equals(qName)) {
                String city = attributes.getValue(0);
                if (city.equalsIgnoreCase("Beijing")) {
                    city = "北京";
                }
                System.out.println("这里是" + city + ",天气预报为您服务");
            } else if ("yweather:forecast".equals(qName)) {
                String s_date = attributes.getValue(1);
                Date date = null;
                try {
                    date = new SimpleDateFormat("dd MMM yyyy",
                                                Locale.US).parse(s_date);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new SAXException("Cannot parse date: " + s_date);
                }
                int low = Integer.parseInt(attributes.getValue(2));
                int high = Integer.parseInt(attributes.getValue(3));
                String text = attributes.getValue(4);
                int code = Integer.parseInt(attributes.getValue(5));
                System.out.println("Weather: " + text + ", low=" + low +
                                   ", high=" + high + ",data=" + date);
            }
            
            super.startElement(uri, localName, qName, attributes);//递归调用

        }
        
        public void characters(char[] ch,
                int start,
                int length){
        	String text=new String(ch,start,length);
            System.out.println(text);
        	
        }

    }

}
