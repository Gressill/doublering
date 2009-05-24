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
		//�������ݿ�
		try{
			db = new Dbo();
			if(db.OpenConnection()){
				System.out.println("[System Info] Database connected.");
			}
			//�����������
			myService = new DoubanService("userfriends", _apiKey, _secret);
		}catch(Exception e){
				e.printStackTrace();
		}
	}
	
	public void setUid(String _uid){
		uid=_uid;
	}
	
	public int parse(){
		
		//ÿ��ȡһ��entry���ݣ�per_read����1,��ÿҳ��ȡ������per_page_read<max_result������������û�������Ϣ������
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
						
						//��ȡdouban_link
						douban_link = u.getLinks().get(1).getHref();
						//��ȡ����uid
						if(douban_link.length()>20){
						douban_uid = douban_link.substring(douban_link.lastIndexOf("people")+7, douban_link.length()-1);
						}
					}
				}
				
				//��ȡ����title
				title = u.getTitle().getPlainText();
				//���˲���ȫ����
				if(title !=null){
					title = title.replace("'", "\\'");
				}
				//��ȡid,douban_id
				String id = u.getId();
				douban_id = id.substring(id.length()-7, id.length());
				
				System.out.println("[System Info] Parse ans spiding friend "+douban_id);
				//��ȡlocation_id
				location_id = u.getLocation_id();
				//���˲���ȫ����
				if(location_id != null){
					location_id = location_id.replace("'", "\\'");
				}
				//��ȡλ��
				location=u.getLocation();
				//���˲���ȫ����
				if(location !=null){
					location = location.replace("'", "\\'");
				}
				//��ȡcontent
				content = u.getPlainTextContent();
				//���˲���ȫ����
				if(content !=null){
					content = content.replace("'", "\\'");
				}
				//��entry��Ϣ����user��
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
				//����ϵ����user_friends��
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
			//����������ʼ����ָ��
			start_index = uf.getStartIndex() + max_result;
			//ֹͣ1.5��
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
