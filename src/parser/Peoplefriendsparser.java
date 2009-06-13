package parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import util.Constant;

import com.google.gdata.client.Service;

import com.google.gdata.client.douban.DoubanService;
import com.google.gdata.data.Link;

import com.google.gdata.data.douban.UserEntry;
import com.google.gdata.data.douban.UserFeed;

import com.google.gdata.util.ServiceException;

import db.Dbo;

public class Peoplefriendsparser  extends Service{
	private String _apiKey = Constant.api;
	private String _secret = Constant.secret;
	private String uid;
	
	DoubanService myService;
	
	private static Dbo db;
	
	private  Random generator;
	
	public Peoplefriendsparser(){
		//�������ݿ�
		try{
			db = new Dbo();
			generator = new Random();
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
			//��ץȡ�û���Ϣ
			UserEntry u_me = myService.getUser(uid);
			System.out.println("[System Info] Spinding people: " +uid);
			parse_entry(u_me);
			
			
			UserFeed uf=null;
			List<String> douban_id_list = new ArrayList<String>(); 
			do{
			System.out.println("[System Info] Spiding people's friends with {start_index: "+start_index+"} and {max_result: "+max_result+"}");	
			uf = myService.getUserFriends(uid, start_index, max_result);
			String id="",douban_id="";
			for(UserEntry u : uf.getEntries()){
				id = u.getId();
				douban_id = id.substring(id.length()-7, id.length());
				//put douban_id to list
				douban_id_list.add(douban_id);
				parse_entry(u);
				
				String insert_friends_sql = "INSERT INTO `dr_user_friends` (`userid`,`friendid`) VALUES ('"+uid+"','"+douban_id+"');";
				//System.out.println("[System Info] Insert sql:\n" + insert_friends_sql);
				store_sql(insert_friends_sql);
			}
			
			//����������ʼ����ָ��
			start_index = uf.getStartIndex() + max_result;
			System.out.println("[System Info] The user has "+uf.getTotalResults()+" friends.");
			if(uf.getTotalResults() < start_index){
				break;
			}
			//ֹͣ1.5��
			Thread.sleep(Constant.sleep);
			}while(true);
			
			if(uf == null || douban_id_list==null || douban_id_list.isEmpty() ==true)
				return 0;
			//randomly return a value;
			String runid = douban_id_list.get(generator.nextInt(uf.getTotalResults()));
			
			System.out.println(runid);
			return Integer.parseInt(runid);
			
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
		
		return 0;
	}
	
	private static void parse_entry(UserEntry ue){
		String douban_uid="",title="",douban_id="",douban_link="",location_id="",location="",content="";
		for(Link l : ue.getLinks()){
			
			if("alternate".equals(l.getRel())){
				
				//��ȡdouban_link
				douban_link = ue.getLinks().get(1).getHref();
				//��ȡ����uid
				if(douban_link.length()>20){
					douban_uid = douban_link.substring(douban_link.lastIndexOf("people")+7, douban_link.length()-1);
				}
			}
		}
		
		//��ȡ����title
		title = ue.getTitle().getPlainText();
		//���˲���ȫ����
		if(title !=null){
			title = title.replace("'", "\\'");
		}
		//��ȡid,douban_id
		String id = ue.getId();
		douban_id = id.substring(id.length()-7, id.length());
		
		//System.out.println("[System Info] Spiding friend "+douban_id);
		//��ȡlocation_id
		location_id = ue.getLocation_id();
		//���˲���ȫ����
		if(location_id != null){
			location_id = location_id.replace("'", "\\'");
		}
		//��ȡλ��
		location=ue.getLocation();
		//���˲���ȫ����
		if(location !=null){
			location = location.replace("'", "\\'");
		}
		//��ȡcontent
		content = ue.getPlainTextContent();
		//���˲���ȫ����
		if(content !=null){
			content = content.replace("'", "\\'");
		}
		String insert_user_sql = "INSERT INTO `dr_user` (`uid`,`title`,`douban_id`,`douban_link`,`location_id`,`location`,`content`) VALUES ("
			+"'"+douban_uid+"',"
			+"'"+title+"',"
			+"'"+douban_id+"',"
			+"'"+douban_link+"',"
			+"'"+location_id+"',"
			+"'"+location+"',"
			+"'"+content+"')";
		store_sql(insert_user_sql);
	}
	
	private static void store_sql(String sql){
		System.out.println("[System Info] Insert sql:\n" + sql);
		if(db.OpenConnection()){
			db.ExecuteUpdate(sql);
		}
	}
}
