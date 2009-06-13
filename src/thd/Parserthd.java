package thd;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.mysql.jdbc.ResultSet;

import db.Dbo;

import parser.Peoplefriendsparser;
import parser.Peopleparser;
import parser.Subjectparser;
import parser.collectionparser;
import util.Constant;

public class Parserthd extends Thread {
	
	private  String request = null; 
	private  Dbo db;
	private  Random generator;
	public Parserthd(String re){
		super(re);
		request = re;
		db = new Dbo();
		generator = new Random();
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
		}else if("peoplecollection".equals(request)){
			spiding_peoplecollection();
		}
	}
	private void spiding_peoplecollection(){
		System.out.println("[System Info] Spiding people's collections...");
		collectionparser pc= new collectionparser();
		Hashtable<String, Integer> last_info = get_info(Constant.virtualmachine,Constant.spideobject);
		int position = last_info.get("current_position");
		int from = last_info.get("from");
		int to = last_info.get("to");
		do{
			if(position<=from && position>=to){
				System.out.println("[System Info] Spiding out of range.");
			}
			if(position%10 == 0){
				System.out.println("[System Info] Update current_position to database.");
				update_position(Constant.virtualmachine,Constant.spideobject,Integer.toString(position));
			}
			try {
				//if(i++>2)break;
				pc.setUid(Integer.toString(position));
				pc.parser();
				sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			position++;
		}while(true);
	}
	private void spiding_peoplefriends(){
		System.out.println("[System Info] Spiding people's frinds...");
		Peoplefriendsparser pf = new Peoplefriendsparser();//book即使subject
		Hashtable<String, Integer> last_info = get_info(Constant.virtualmachine,Constant.spideobject);
		int position = last_info.get("current_position");
		int from = last_info.get("from");
		int to = last_info.get("to");
		do{//循环抓取用户及好友信息
			//if(i>2)break;
			if(position<=from && position>=to){
				System.out.println("[System Info] Spiding out of range.");
			}
			if(position%10 == 0){
				System.out.println("[System Info] Update current_position to database.");
				update_position(Constant.virtualmachine,Constant.spideobject,Integer.toString(position));
			}
			try {
				
			System.out.println("[System Info] Spiding people "+position+" and his friends.");
			pf.setUid(Integer.toString(position));
			pf.parse();
			
			sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			position++;
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
		
		try{
			if(db.OpenConnection()){
				System.out.println("[System Info] Database connected for getlastid from "+table);
				String getlastsql = "SELECT `"+id+"` FROM `"+table+"` WHERE `douban_id`>="+Constant.min_id+" AND `douban_id`<="+Constant.max_id+" ORDER BY `"+id+"` DESC LIMIT 1";
				ResultSet res = (ResultSet) db.ExecuteQuery(getlastsql);//S
				//处理结果集
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
		return (int)Constant.min_id;
	}
	private Hashtable<String, Integer> get_info(String virtualmachine, String type){
		Hashtable<String, Integer> info = new Hashtable<String, Integer>();
		try{
			if(db.OpenConnection()){
				System.out.println("[System Info] Get the last id from machine "+ virtualmachine+", spiding type is "+type);
				String get_info = "SELECT * FROM `dr_spiding_stat` WHERE `virtualmachine`='"+virtualmachine+"' AND `type`='"+type+"'";
				System.out.println("[System Info] get_info sql is: "+get_info);
				ResultSet res = (ResultSet) db.ExecuteQuery(get_info);
				while (res.next()){
					int current_position = res.getInt("current_position");
					info.put("current_position", new Integer(current_position));
					int from  = res.getInt("from");
					info.put("from", from);
					info.put("to", res.getInt("to"));
					System.out.println("[System Info] Get current postion: "+current_position);
					return info;
				}
			}
		}catch(Exception e){
				e.printStackTrace();
		}finally{
			//db.CloseConnection();
		}
		return info;
	}
	
	private void update_position(String virtualmachine, String type, String position){
		try{
			if(db.OpenConnection()){
				System.out.println("[System Info] Update the last id from machine "+ virtualmachine+", spiding type is "+type);
				String update_position = "UPDATE `dr_spiding_stat` SET `current_position`='"+position+"' WHERE `virtualmachine`='"+virtualmachine+"' AND `type`='"+type+"'";
				db.ExecuteUpdate(update_position);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			//db.CloseConnection();
		}	
	}
	
}
