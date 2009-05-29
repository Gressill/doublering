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

import parser.Peoplefriendsparser;
import parser.Peopleparser;
import parser.Subjectparser;
import util.Constant;

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
		}else if("subject".equals(request)){
			spiding_subject();
		}else if("peoplefriends".equals(request)){
			spiding_peoplefriends();
		}
	}
	
	private void spiding_peoplefriends(){
		System.out.println("[System Info] Spiding people's frinds...");
		Peoplefriendsparser pf = new Peoplefriendsparser();//book即使subject
		int uid = Constant.seed;
		int runid = 0 ;
		int i=0;
		do{//循环抓取用户及好友信息
			//if(i>2)break;
			try {
				
				System.out.println("[System Info] Spiding people and his friends: " + uid);
				sleep(3000);
				if(runid == 0){
					pf.setUid(Integer.toString(uid));
					
				}else{
					pf.setUid(Integer.toString(runid));
				}
				runid = pf.parse();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			uid = uid +1;
			i++;
		}while(true);
	}

	private void spiding_subject(){
		//调用Bookparser来抓取、解析、储蓄书籍信息
		System.out.println("[System Info] Spiding subject...");
		Subjectparser b = new Subjectparser();//book即使subject
		
		int i=0;
		int ui = get_subject_last_id();
		
		do{
			if( ui<Constant.min_id || ui>Constant.max_id){
				System.out.println("[System Info] Spiding out of the range, shut down!");
				break;
			}
			String  uid = ""+ui;
			
			System.out.println("[System Info] Spiding subject: " + uid);
			//if(i>2)break;
			
			try {
				
				b.setBookid(uid);
				b.parse();
				
				sleep(1550);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ui++;
			i++;
		}while(true);
		
	}
	
	private void spiding_people(){
		//调用Peopleparser来抓取、解析、储蓄书籍信息
		Peopleparser p = new Peopleparser();
		
		int i=0;
		int ui = getlastid("dr_user","douban_id");
		
		System.out.println("[System Info] Spider people...");
		
		do{
			
			String  uid = ""+ui;
			
			System.out.println("[System Info] Spiding people: " + uid);
			
			//if(i>2)break;
			try {
				
				sleep(1550);
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
	
	private int get_subject_last_id(){
		int book_last_id = getlastid("dr_book","douban_id");
		int movie_last_id = getlastid("dr_movie","douban_id");
		int music_last_id = getlastid("dr_music","douban_id");
		
		int tmp = (book_last_id > movie_last_id)? book_last_id : movie_last_id;
		
		int ret = (music_last_id > tmp)? music_last_id : tmp;
		
		System.out.println("[System Info] Get the last subject id: " + ret);
		return ret;
	}
	
	private int getlastid(String table, String id){
		Dbo db = new Dbo();
		try{
			
			if(db.OpenConnection()){
				System.out.println("[System Info] Database connected for getlastid from "+table);
				String getlastsql = "SELECT `"+id+"` FROM `"+table+"` WHERE `douban_id`>="+Constant.min_id+" AND `douban_id`<="+Constant.max_id+" ORDER BY `"+id+"` DESC LIMIT 1";
				ResultSet res = (ResultSet) db.ExecuteQuery(getlastsql);//S
				//处理结果集
				while (res.next()) {
					int last_douban_id = res.getInt(id)+1;
					System.out.println("[System Info] Get last "+id+": "+last_douban_id);
					return (int)last_douban_id;
				}
				}
		}catch(Exception e){
				e.printStackTrace();
		}finally{
			db.CloseConnection();
		}
		return (int)Constant.min_id;
	}
	
}
