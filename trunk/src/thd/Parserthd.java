package thd;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.google.gdata.client.douban.DoubanService;
import com.google.gdata.data.Link;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.douban.UserEntry;
import com.google.gdata.data.douban.UserFeed;
import com.google.gdata.util.ServiceException;
import com.mysql.jdbc.ResultSet;

import db.Dbo;

import parser.Bookparser;
import parser.Peopleparser;

public class Parserthd extends Thread {
	
	private  String request = null; 
	
	public Parserthd(String re){
		super(re);
		request = re;
		System.out.println("[System Info] Start spiding " + re);
	}
	public void run(){
		
		//System.out.println("subject".equals(request));
		
		if("people".equals(request)){
			spiding_people();
		}else if("book".equals(request)){
			spiding_book();
		}else if("movie".equals(request)){
			spiding_movie();
		}else if("music".equals(request)){
			spiding_music();
		}
	}
	
	private void spiding_movie(){
		//����Bookparser��ץȡ�������������鼮��Ϣ
		System.out.println("[System Info] Spider movie...");
	}
	
	private void spiding_music(){
		//����Musicparser��ץȡ�������������鼮��Ϣ
		System.out.println("[System Info] Spider music...");
		
	}

	private void spiding_book(){
		//����Bookparser��ץȡ�������������鼮��Ϣ
		System.out.println("[System Info] Spider book...");
		Bookparser b = new Bookparser();
		
		int i=0;
		int ui = getlastid("dr_book","douban_id");
		do{
			String  uid = ""+ui;
			if(i>2)break;
			
			try {
				
				b.setURLbyId(uid);
				b.parseBook();
				
				sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}while(true);
		
	}
	
	private void spiding_people(){
		//����Peopleparser��ץȡ�������������鼮��Ϣ
		Peopleparser p = new Peopleparser();
		
		int i=0;
		int ui = getlastid("dr_user","douban_id");
		
		System.out.println("[System Info] Spider people...");
		
		do{
			
			String  uid = ""+ui;
			//if(i>2)break;
			try {
				
				sleep(2000);
				p.setURLbyId(uid);
				p.parseUser();
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			ui++;
			i++;
		}while(true);

	}
	
	private int getlastid(String table, String id){
		Dbo db = new Dbo();
		try{
			
			if(db.OpenConnection()){
				System.out.println("[System Info] Database connected for getlastid.");
				String getlastsql = "SELECT `"+id+"` FROM `"+table+"` WHERE 1 ORDER BY `"+id+"` DESC LIMIT 1";
				ResultSet res = (ResultSet) db.ExecuteQuery(getlastsql);//S
				//��������
				while (res.next()) {
					int last_douban_id = res.getInt(id);
					System.out.println("[System Info] Get last "+id+": "+last_douban_id);
					return (int)last_douban_id;
				}
				}
		}catch(Exception e){
				e.printStackTrace();
		}finally{
			db.CloseConnection();
		}
		return (int)1000001;
	}
	
}
