package parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import util.Constant;

import com.google.gdata.client.Service;
import com.google.gdata.client.douban.DoubanQuery;
import com.google.gdata.client.douban.DoubanService;
import com.google.gdata.data.Link;
import com.google.gdata.data.Person;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.douban.Attribute;
import com.google.gdata.data.douban.Namespaces;
import com.google.gdata.data.douban.SubjectEntry;
import com.google.gdata.data.douban.Tag;
import com.google.gdata.data.douban.UserEntry;
import com.google.gdata.data.douban.UserFeed;
import com.google.gdata.data.extensions.Rating;
import com.google.gdata.util.ServiceException;

import db.Dbo;

public class Peoplefriendsparser  extends Service{
	private String _apiKey = Constant.api;
	private String _secret = Constant.secret;
	private String uid;
	
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
	
	public void setUid(String _uid){
		uid=_uid;
	}
	
	public int parse(){
		
		//每读取一个entry数据，per_read增加1,当每页读取完后，如果per_page_read<max_result，这表明数据用户好友信息被读完
		int start_index=1;
		int max_result =50;
		try {
			String url = Namespaces.userURL + "/" + uid  + "/friends";
			DoubanQuery query = new DoubanQuery(new URL(url));
			do{
			System.out.println("[System Info] Spiding people's friends with {start_index: "+start_index+"} and {max_result: "+max_result+"}");	
		
			query.setStartIndex(start_index);
			query.setMaxResults(max_result);
			UserFeed uf = getFeed(query, UserFeed.class);
			
			
			//UserFeed uf = myService.getUserFriends("1000001", 1, 2);
			//List<UserEntry> ul = ;
			String douban_uid="",title="",douban_id="",douban_link="",location_id="",location="",content="";
			for(UserEntry u : uf.getEntries()){
				
				for(Link l : u.getLinks()){
					
					if("alternate".equals(l.getRel())){
						
						//获取douban_link
						douban_link = u.getLinks().get(1).getHref();
						//获取豆瓣uid
						if(douban_link.length()>20){
						douban_uid = douban_link.substring(douban_link.lastIndexOf("people")+7, douban_link.length()-1);
						}
					}
				}
				
				//获取豆瓣title
				title = u.getTitle().getPlainText();
				//过滤不安全数据
				if(title !=null){
					title = title.replace("'", "\\'");
				}
				//获取id,douban_id
				String id = u.getId();
				douban_id = id.substring(id.length()-7, id.length());
				
				System.out.println("[System Info] Parse ans spiding friend "+douban_id);
				//获取location_id
				location_id = u.getLocation_id();
				//过滤不安全数据
				if(location_id != null){
					location_id = location_id.replace("'", "\\'");
				}
				//获取位置
				location=u.getLocation();
				//过滤不安全数据
				if(location !=null){
					location = location.replace("'", "\\'");
				}
				//获取content
				content = u.getPlainTextContent();
				//过滤不安全数据
				if(content !=null){
					content = content.replace("'", "\\'");
				}
				//将entry信息插入user表
				String insert_user_sql = "INSERT INTO `dr_user` (`uid`,`title`,`douban_id`,`douban_link`,`location_id`,`location`,`content`) VALUES ("
					+"'"+douban_uid+"',"
					+"'"+title+"',"
					+"'"+douban_id+"',"
					+"'"+douban_link+"',"
					+"'"+location_id+"',"
					+"'"+location+"',"
					+"'"+content+"')";
				store_sql(insert_user_sql);
				
				if( "".equals(douban_id) == false){
				//将关系插入user_friends表
				String insert_friends_sql = "INSERT INTO `dr_user_friends` (`userid`,`friendid`) VALUES ('"+uid+"','"+douban_id+"')";
				store_sql(insert_friends_sql);
				}
			}
			
			
			if(uf.getTotalResults() <uf.getStartIndex() + max_result){
				if("".equals(douban_id) == false){
					return (int)Constant.min_id + (int)Math.random() * (Constant.max_id - Constant.min_id);
				}
				else{
					int ret =  (int)Integer.valueOf(douban_id);
 					return ret;
				}
			}
			//否则，增加起始访问指标
			start_index = uf.getStartIndex() + max_result;
			//停止1.5秒
			Thread.sleep(2550);
			}while(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return (int)Constant.min_id + (int)Math.random() * (Constant.max_id - Constant.min_id);
		
	}
	
	private static void store_sql(String sql){
		System.out.println("[System Info] Insert sql:\n" + sql);
		if(db.OpenConnection()){
			db.ExecuteUpdate(sql);
		}
	}
}
