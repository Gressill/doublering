package parser;

import java.io.IOException;
import java.util.List;

import util.Constant;

import com.google.gdata.client.douban.DoubanService;
import com.google.gdata.data.Link;
import com.google.gdata.data.Person;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.douban.Attribute;
import com.google.gdata.data.douban.CollectionEntry;
import com.google.gdata.data.douban.CollectionFeed;
import com.google.gdata.data.douban.Subject;
import com.google.gdata.data.douban.SubjectEntry;
import com.google.gdata.data.douban.Tag;
import com.google.gdata.data.douban.UserEntry;
import com.google.gdata.data.douban.UserFeed;
import com.google.gdata.data.extensions.Rating;
import com.google.gdata.util.ServiceException;

import db.Dbo;

public class collectionparser {
	private String _apiKey = Constant.api;
	private String _secret = Constant.secret;
	private String uid;
	
	DoubanService myService;
	
	private static Dbo db;
	public collectionparser(){
		//链接数据库
		try{
			db = new Dbo();
			if(db.OpenConnection()){
				System.out.println("[System Info] Database connected.");
			}
			//启动豆瓣服务
			myService = new DoubanService("collection", _apiKey, _secret);
		}catch(Exception e){
				e.printStackTrace();
		}
	}
	public void setUid(String _uid){
		uid=_uid;
	}
	
	public void parser(){
		
		try {
			parse_subject("book");
			Thread.sleep(1500);
			parse_subject("music");
			Thread.sleep(1500);
			parse_subject("movie");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private  void parse_subject(String type) throws IOException, ServiceException, InterruptedException{
		int start_index=1;
		int max_result =5;
		
		do{
			CollectionFeed cf = myService.getUserCollections(uid, type, null, null, start_index, max_result);
			for (CollectionEntry ce : cf.getEntries()) {
				parseCollectionEntry(ce,uid,type);//
			}
			if(cf.getTotalResults() <cf.getStartIndex() + max_result){
				break;
			}
			//否则，增加起始访问指标
			start_index = cf.getStartIndex() + max_result;
			Thread.sleep(2000);
		}while(true);
	}

	private static void parseCollectionEntry(CollectionEntry ce, String uid, String typ){
		Subject sub = ce.getSubjectEntry();
		if(sub == null)
			return;
		
		
		String collection_id   = ce.getId();
		collection_id = collection_id.substring(collection_id.lastIndexOf("collection")+11,collection_id.length()-1);
		String user_id = uid;
		String subject_id = sub.getId();
		subject_id = subject_id.substring(subject_id.lastIndexOf("subject")+8, subject_id.length()-1);
		String type = typ;
		String title = ce.getTitle().getPlainText().replace("'", "\\'");
		String status = ce.getStatus().getContent();
		//将collection写入数据库
		String insert_collection_sql = "INSERT INTO `dr_user_collection` (`user_id`,`subject_id`,`collection_id`,`type`,`title`,`status`)"
			+" VALUES ("
			+"'"+user_id+"',"
			+"'"+subject_id+"',"
			+"'"+collection_id+"',"
			+"'"+type+"',"
			+"'"+title+"',"
			+"'"+status+"')";
		store_sql(insert_collection_sql);
		
		if("book".equals(typ)){
			analyze_and_store_book(sub);
		}else if("music".equals(typ)){
			analyze_and_store_music(sub);
		}else if("movie".equals(typ)){
			analyze_and_store_music(sub);
		}
	}

	private static void analyze_and_store_book(Subject subjectEntry){
		String title ="", author ="", summary ="", id="", douban_id="",douban_link="",isbn10="",isbn13="",price="";
		String publisher = "", binding="",pubdate="";
		//获取title
		title   = subjectEntry.getTitle().getPlainText().replace("'", "\\'");
		//获取author
		List<Person> authors = subjectEntry.getAuthors();
		if(authors.isEmpty() == false){
			for(Person ath : authors ){
				author = author + "  " + ath.getName().replace("'", "\\'");
			}
		}
		//获取summary (SUMMARY这段代码还不稳定)
		TextConstruct sbe=null;
		sbe = subjectEntry.getSummary();
		if(sbe != null){
			summary = subjectEntry.getSummary().getPlainText().replace("'", "\\'");
		}
		//获取id,douban_id
		id = subjectEntry.getId();
		douban_id = id.substring(id.length()-7, id.length());
		//获取douban_link
		douban_link = subjectEntry.getLinks().get(1).getHref();
		
		for (Attribute attr : subjectEntry.getAttributes()) {
			if("isbn10".equals(attr.getName())){
				//获取country
				isbn10 = attr.getContent().replace("'", "\\'");
			}else if("isbn13".equals(attr.getName())){
				//获取writer
				isbn13 = attr.getContent().replace("'", "\\'");
			}else if("price".equals(attr.getName())){
				//获取language
				price = attr.getContent().replace("'", "\\'");
			}else if("publisher".equals(attr.getName())){
				//获取director
				publisher = attr.getContent().replace("'", "\\'");
			}else if("pubdate".equals(attr.getName())){
				//获取pubdate
				pubdate = attr.getContent();
			}else if("binding".equals(attr.getName())){
				//获取aka
				binding = attr.getContent().replace("'", "\\'");
			}
		}
		
		String insert_book_sql = " INSERT INTO `"+Constant.DB_DATABASE+"`.`dr_book` "+
			"(`title` ,`douban_id` ,`douban_link` ,`author`,`summary` ,`price` ,`publisher` ,`binding` ,`pubdate` ,`isbn10` ,`isbn13`)"+
			" VALUES ( '"
			+ title + "', '"
			+douban_id+"', '"
			+douban_link+"', '"
			+author+"', '"
			+summary+"', '"
			+price+"', '"
			+publisher+"', '"
			+binding+"', '"
			+pubdate+"', '"
			+isbn10+"', '"
			+isbn13+"');";
		//将book信息写入数据库
		store_sql(insert_book_sql);
		
		Rating rating = null;
		rating = subjectEntry.getRating();
		if(rating != null){
			int minrate = 0; int maxrate = 0; float avgrate = 0; int ratenum = 0;
			//获取最小评分，最大评分，平均评分，评分数量信息。
			minrate = rating.getMin();
			maxrate = rating.getMax();
			avgrate = rating.getAverage();
			ratenum = rating.getNumRaters();
			//将评分信息rating写入dr_subject_rating数据库
			String insert_rating_sql = "INSERT INTO `" + Constant.DB_DATABASE+"`.`dr_subject_rating` (`minrate`,`maxrate`,`avgrate`,`ratenum`,`target_id`) VALUES "
				+"('"+minrate+"','"+maxrate+"','"+avgrate+"','"+ratenum+"','"+douban_id+"')";
			store_sql(insert_rating_sql);
		}
		
		//再获取tag值
		List<Tag> tags = subjectEntry.getTags();
		if(tags  != null){
			boolean flag = false;
			String insert_tag_sql = "INSERT INTO `"+Constant.DB_DATABASE+"`.`dr_subject_tag` (`tag`,`count`,`target_id`) VALUES ";
			for (Tag tag : tags) {
				flag = true;
				insert_tag_sql = insert_tag_sql + "('" + tag.getName() + "','" + tag.getCount() + "','" + douban_id + "'),";
				//System.out.println(tag.getName() + " : " + tag.getCount());
			}
			if(flag){
			insert_tag_sql = insert_tag_sql.substring(0, insert_tag_sql.length()-1)+";";
			//将与book相关的tag写入数据库
			store_sql(insert_tag_sql);
		}
		}
	}
	private static void analyze_and_store_movie(Subject subjectEntry){
		String title ="", author ="", summary ="", id="", douban_id="",douban_link="";
		//获取title
		title   = subjectEntry.getTitle().getPlainText().replace("'", "\\'");
		//获取author
		List<Person> authors = subjectEntry.getAuthors();
		if(authors.isEmpty() == false){
			for(Person ath : authors ){
				author = author + "  " + ath.getName().replace("'", "\\'");
			}
		}
		//获取summary
		TextConstruct sbe = null;
		sbe = subjectEntry.getSummary();
		if(sbe != null){
			summary = subjectEntry.getSummary().getPlainText().replace("'", "\\'");
			//System.out.println("[System Debug] Summary is:\n"+summary);
		}
		//获取id,douban_id
		id = subjectEntry.getId();
		douban_id = id.substring(id.length()-7, id.length());
		//获取douban_link
		douban_link = subjectEntry.getLinks().get(1).getHref();
		//处理attributes
		String writer="",language="",cast="",country="",director="",pubdate="",aka="",imdb="";
		for (Attribute attr : subjectEntry.getAttributes()) {
			if("country".equals(attr.getName())){
				//获取country
				country = attr.getContent().replace("'", "\\'");
			}else if("writer".equals(attr.getName())){
				//获取writer
				writer = writer + "   " + attr.getContent().replace("'", "\\'");
			}else if("language".equals(attr.getName())){
				//获取language
				language = language + "   " + attr.getContent().replace("'", "\\'");
			}else if("cast".equals(attr.getName())){
				//获取cast
				cast  = cast + "   " + attr.getContent().replace("'", "\\'");
			}else if("director".equals(attr.getName())){
				//获取director
				director = attr.getContent().replace("'", "\\'");
			}else if("pubdate".equals(attr.getName())){
				//获取pubdate
				pubdate = attr.getContent().replace("'", "\\'");
			}else if("aka".equals(attr.getName())){
				//获取aka
				aka = attr.getContent().replace("'", "\\'");
			}else if("imdb".equals(attr.getName())){
				//获取imdb
				imdb = attr.getContent().replace("'", "\\'");
			}
		}
		
		
		String insert_movie_sql = " INSERT INTO `"+Constant.DB_DATABASE+"`.`dr_movie`"+
		"(`title` ,`douban_id` ,`douban_link` ,`author`,`summary` ,`country` ,`director` ,`language` ,`pubdate` ,`cast` ,`aka` ,`imdb`)"+
		" VALUES ( '"
		+ title + "', '"
		+douban_id+"', '"
		+douban_link+"', '"
		+author+"', '"
		+summary+"', '"
		+country+"', '"
		+director+"', '"
		+language+"', '"
		+pubdate+"', '"
		+cast+"', '"
		+aka+"', '"
		+imdb+"');";
		//将movie信息写入数据库
		store_sql(insert_movie_sql);
		
		Rating rating = null;
		rating = subjectEntry.getRating();
		if(rating != null){
			int minrate = 0; int maxrate = 0; float avgrate = 0; int ratenum = 0;
			//获取最小评分，最大评分，平均评分，评分数量信息。
			minrate = rating.getMin();
			maxrate = rating.getMax();
			avgrate = rating.getAverage();
			ratenum = rating.getNumRaters();
			//将评分信息rating写入dr_subject_rating数据库
			String insert_rating_sql = "INSERT INTO `" + Constant.DB_DATABASE+"`.`dr_subject_rating` (`minrate`,`maxrate`,`avgrate`,`ratenum`,`target_id`) VALUES "
				+"('"+minrate+"','"+maxrate+"','"+avgrate+"','"+ratenum+"','"+douban_id+"')";
			store_sql(insert_rating_sql);
		}
		
	
		//再获取tag值
		List<Tag> tags = null ;
		tags = subjectEntry.getTags();
		if(tags != null){
		boolean flag = false;
		String insert_tag_sql = "INSERT INTO `"+Constant.DB_DATABASE+"`.`dr_subject_tag` (`tag`,`count`,`target_id`) VALUES ";
		for (Tag tag : tags) {
			flag = true;
			insert_tag_sql = insert_tag_sql + "('" + tag.getName() + "','" + tag.getCount() + "','" + douban_id + "'),";
		}
		if(flag){
			insert_tag_sql = insert_tag_sql.substring(0, insert_tag_sql.length()-1)+";";
			//将与movie相关的tag写入数据库
			store_sql(insert_tag_sql);
		}
		}
	}
	private static void analyze_and_store_music(Subject subjectEntry){
		String title ="", author ="", summary ="", id="", douban_id="",douban_link="";
		//获取title
		title   = subjectEntry.getTitle().getPlainText().replace("'", "\\'");
		//获取author
		List<Person> authors = subjectEntry.getAuthors();
		if(authors.isEmpty() == false){
			for(Person ath : authors ){
				author = author + "  " + ath.getName().replace("'", "\\'");
			}
		}
		//获取summary
		TextConstruct sbe = null;
		sbe = subjectEntry.getSummary();
		if(sbe != null){
			summary = subjectEntry.getSummary().getPlainText().replace("'", "\\'");
			//System.out.println("[System Debug] Summary is:\n"+summary);
		}
		//获取id,douban_id
		id = subjectEntry.getId();
		douban_id = id.substring(id.length()-7, id.length());
		//获取douban_link
		douban_link = subjectEntry.getLinks().get(1).getHref();
		//处理attributes
		String ean="",tracks="",cast="",discs="",media="",pubdate="",aka="",singer="";
		for (Attribute attr : subjectEntry.getAttributes()) {
			if("discs".equals(attr.getName())){
				//获取discs
				discs = attr.getContent().replace("'", "\\'");
			}else if("ean".equals(attr.getName())){
				//获取ean
				ean = attr.getContent().replace("'", "\\'");
			}else if("tracks".equals(attr.getName())){
				//获取language
				tracks = tracks + "   " + attr.getContent().replace("'", "\\'");
			}else if("cast".equals(attr.getName())){
				//获取cast
				cast  = cast + "   " + attr.getContent().replace("'", "\\'");
			}else if("media".equals(attr.getName())){
				//获取media
				media = attr.getContent().replace("'", "\\'");
			}else if("pubdate".equals(attr.getName())){
				//获取pubdate
				pubdate = attr.getContent().replace("'", "\\'");
			}else if("aka".equals(attr.getName())){
				//获取aka
				aka = attr.getContent().replace("'", "\\'");
			}else if("singer".equals(attr.getName())){
				//获取singer
				singer = attr.getContent().replace("'", "\\'");
			}
		}
		
		String insert_music_sql = " INSERT INTO `"+Constant.DB_DATABASE+"`.`dr_music` ("+
		"`title` ,`douban_id` ,`douban_link` ,`author`,`summary` ,`discs` ,`ean` ,`singer` ,`pubdate` ,`tracks` ,`aka` ,`media`"+
		") VALUES ( '"
		+ title + "', '"
		+douban_id+"', '"
		+douban_link+"', '"
		+author+"', '"
		+summary+"', '"
		+discs+"', '"
		+ean+"', '"
		+singer+"', '"
		+pubdate+"', '"
		+tracks+"', '"
		+aka+"', '"
		+media+"');";
		//将music信息写入数据库
		store_sql(insert_music_sql);
		
		Rating rating = null;
		rating = subjectEntry.getRating();
		if(rating != null){
			int minrate = 0; int maxrate = 0; float avgrate = 0; int ratenum = 0;
			//获取最小评分，最大评分，平均评分，评分数量信息。
			minrate = rating.getMin();
			maxrate = rating.getMax();
			avgrate = rating.getAverage();
			ratenum = rating.getNumRaters();
			//将评分信息rating写入dr_subject_rating数据库
			String insert_rating_sql = "INSERT INTO `" + Constant.DB_DATABASE+"`.`dr_subject_rating` (`minrate`,`maxrate`,`avgrate`,`ratenum`,`target_id`) VALUES "
				+"('"+minrate+"','"+maxrate+"','"+avgrate+"','"+ratenum+"','"+douban_id+"')";
			store_sql(insert_rating_sql);
		}
		
		
		//再获取tag值
		List<Tag> tags = null;
		tags = subjectEntry.getTags();
		if(tags != null){
		boolean flag = false;
		String insert_tag_sql = "INSERT INTO `"+Constant.DB_DATABASE+"`.`dr_subject_tag` (`tag`,`count`,`target_id`) VALUES ";
		for (Tag tag : tags) {
			flag = true;
			insert_tag_sql = insert_tag_sql + "('" + tag.getName() + "','" + tag.getCount() + "','" + douban_id + "'),";
			//System.out.println(tag.getName() + " : " + tag.getCount());
		}
		if(flag){
			insert_tag_sql = insert_tag_sql.substring(0, insert_tag_sql.length()-1)+";";
			//将与book相关的tag写入数据库
			store_sql(insert_tag_sql);
		}
		}
		
	}
	private static void store_sql(String sql){
		System.out.println("[System Info] Insert sql:\n" + sql);
		if(db.OpenConnection()){
			db.ExecuteUpdate(sql);
		}
	}
}
