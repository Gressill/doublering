package thd;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.mysql.jdbc.ResultSet;

import db.Dbo;

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
			spiderpeople();
			
		}else if("subject".equals(request)){
			spidersubject();
		}
	}
	
	private void spidersubject(){
		System.out.println("[System Info] Spider people...");
		int i=0;
		do{
			if(i>2)break;
			
			try {
				sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}while(true);
		
	}
	private void spiderpeople(){
		Peopleparser p = new Peopleparser();
		
		int i=0;
		int ui = getlastid();
		
		System.out.println("[System Info] Spider people...");
		
		do{
			
			String  uid = ""+ui;
			//if(i>40)break;
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
	
	private int getlastid(){
		Dbo db = new Dbo();
		try{
			
			if(db.OpenConnection()){
				System.out.println("[System Info] Database connected for getlastid.");
				String getlastsql = "SELECT `douban_id` FROM `dr_user` WHERE 1 ORDER BY `id` DESC LIMIT 1";
				ResultSet res = (ResultSet) db.ExecuteQuery(getlastsql);//S
				//处理结果集
				while (res.next()) {
					int last_douban_id = res.getInt("douban_id");
					System.out.println("[System Info] Get last id: "+last_douban_id);
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
