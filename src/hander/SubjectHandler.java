package hander;

import java.util.Hashtable;
import java.util.Iterator;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import db.Dbo;

public class SubjectHandler extends DefaultHandler {//继承DefaultHandler重写startElement解析XML

	private Hashtable<String, String> _pentry;
	private String cur_qName;
	private Dbo db;
	
	public SubjectHandler(){
		
		try{
			db = new Dbo();
			if(db.OpenConnection()){
				System.out.println("[System Info] Database connected.");
			}
		}catch(Exception e){
				e.printStackTrace();
		}
		
		 _pentry = new Hashtable<String, String>();
		 clear();
		 
		System.out.println("[System info] Construction");
	}
	public void startElement(String uri, String localName, String qName,
                             org.xml.sax.Attributes attributes) throws
            SAXException {
		cur_qName = qName;
    	
		//System.out.println("[System info]"+qName);
		
    	if ("id".equals(qName)){
    		cur_qName = "id";
    	}else if("title".equals(qName)){
    		cur_qName = "title";
    	}else if("link".equals(qName)){
    		cur_qName = "link";
    		if("alternate".equals(attributes.getValue("rel"))){
    			String myhref = attributes.getValue("href");
    			_pentry.put("douban_link", new String(myhref));
    		}
    		if("self".equals(attributes.getValue("rel"))){
    			String myhref = attributes.getValue("href");
    			String doubanid = myhref.substring(myhref.length()-7, myhref.length());
    			_pentry.put("douban_id", new String(doubanid));
    		}
    	}else if("summary".equals(qName)){
    		cur_qName = "summary";
    	}
    	else if("content".equals(qName)){
    		cur_qName = "content";
    	}else if("db:attribute".equals(qName)){
    		if("isbn10".equals(attributes.getValue("name"))){
    			cur_qName = "isbn10";	
    		}else if("isbn13".equals(attributes.getValue("name"))){
    			cur_qName = "isbn13";	
    		}
    	}else if("gd:rating".equals(qName)){
    		_pentry.put("rating", attributes.getValue("average"));
    		_pentry.put("rater", attributes.getValue("numRaters"));
    	}
        
        super.startElement(uri, localName, qName, attributes);//递归调用

    }
    
    public void characters(char[] ch,
            int start,
            int length){
    	String text=new String(ch,start,length);
    	if(text == ""){
    		return;
    	}
    	Object o = _pentry.get(cur_qName);
    	if(o != null){
    		_pentry.put(cur_qName, new String(o.toString()+text));
    	}else{
    		_pentry.put(cur_qName, new String(text));
    	}
        //System.out.println(_pentry.toString());
    }
    
    public void endDocument(){
    	//Insert into the database here.
    	store();
    	clear();
    }
    private void store(){
    	System.out.println(_pentry.toString());
    	
    	try{
    		if(_pentry.get("uid") == null) return; //空的table，不储蓄
    		String sqlInsert = "INSERT INTO `dr_book`(`uid`,`title`,`douban_id`,`douban_link`,`location_id`,`location`,`content`) VALUES ('"+
    		_pentry.get("uid").toString()+"','"+_pentry.get("title").toString()+"','"+_pentry.get("douban_id").toString()+"','"+_pentry.get("douban_link").toString()+"','"+_pentry.get("location_id").toString()+"','"+_pentry.get("location").toString()+"','"+_pentry.get("content").toString()+"')";
    		System.out.println(sqlInsert);
    		if(db.OpenConnection()){
    			//db.ExecuteUpdate(sqlInsert);
    		}
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    }
    private void clear(){
    	_pentry.clear();
    	_pentry.put("uid", "");
    	_pentry.put("title", "");
    	_pentry.put("link", "");
    	_pentry.put("douban_id", "");
    	_pentry.put("douban_link", "");
    	_pentry.put("location_id", "");
    	_pentry.put("location", "");
    	_pentry.put("content", "");
    	//System.out.println(_pentry);
    	//_pentry = new Hashtable<String, String>();
//    	for(Iterator it = _pentry.keySet().iterator();it.hasNext(); )
//    	{  
//            String key = (String) it.next();
//            System.out.println(key);
//            _pentry.remove(key);
//            _pentry.put(key.toString(),""); 
//    	}
    }

}
