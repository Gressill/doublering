package parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import util.Constant;

import com.google.gdata.client.douban.DoubanQuery;
import com.google.gdata.client.douban.DoubanService;
import com.google.gdata.data.Person;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.douban.Attribute;
import com.google.gdata.data.douban.SubjectEntry;
import com.google.gdata.data.douban.Tag;
import com.google.gdata.data.douban.UserEntry;
import com.google.gdata.data.douban.UserFeed;
import com.google.gdata.data.extensions.Rating;
import com.google.gdata.util.ServiceException;

import db.Dbo;

public class Peoplefriendsparser {
	private String _apiKey = Constant.api;
	private String _secret = Constant.secret;
	private String userid;
	
	DoubanService myService;
	
	private static Dbo db;
	
	public Peoplefriendsparser(){
		//链接数据库
		try{
			db = new Dbo();
			if(db.OpenConnection()){
				System.out.println("[System Info] Database connected.");
			}
			//启动豆瓣服务
			myService = new DoubanService("userfriends", _apiKey, _secret);
		}catch(Exception e){
				e.printStackTrace();
		}
	}
	public void setUserid(String id){
		//设置ID
		userid = id;
	}
	public void parse(){
		System.out.println("[System Info] Spiding people's friends");
		try {
			UserFeed uf = myService.getUserFriends("1000001", 1, 5);
			List<UserEntry> ul = uf.getEntries();
			for(UserEntry u : ul){
				System.out.println(u.getId());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
