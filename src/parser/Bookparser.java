package parser;

import java.io.IOException;
import java.util.List;

import util.Constant;

import com.google.gdata.client.douban.DoubanService;
import com.google.gdata.data.Person;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.douban.Attribute;
import com.google.gdata.data.douban.SubjectEntry;
import com.google.gdata.data.douban.Tag;
import com.google.gdata.data.extensions.Rating;
import com.google.gdata.util.ServiceException;

import db.Dbo;


public class Bookparser {
	
	private String _apiKey = Constant.api;
	private String _secret = Constant.secret;
	private String bookid;
	
	DoubanService myService;
	
	private static Dbo db;
	
	public Bookparser(){
		//�������ݿ�
		try{
			db = new Dbo();
			if(db.OpenConnection()){
				System.out.println("[System Info] Database connected.");
			}
			//�����������
			myService = new DoubanService("book", _apiKey, _secret);
		}catch(Exception e){
				e.printStackTrace();
		}
	}
	public void setBookid(String id){
		//����ID
		bookid = id;
	}
	public void parse(){
		//bookid = "3729312";

		SubjectEntry subjectEntry = new SubjectEntry();
		try {//���ץ��ɹ�����ץ��
			subjectEntry = myService.getBook(bookid);
			analyze_and_store_book(subjectEntry);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			System.out.println("[System Error] Get book failed, try to get movie");
			try {//������ɹ�����ץ��Ӱ
				subjectEntry = myService.getMovie(bookid);
				analyze_and_store_movie(subjectEntry);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ServiceException e1) {
				// TODO Auto-generated catch block
				System.out.println("[System Error] Get movie failed, try to get music");
				try {//������ɹ�����ץ����
					subjectEntry = myService.getMusic(bookid);
					analyze_and_store_music(subjectEntry);
				}catch (IOException e2) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ServiceException e2) {
					// TODO Auto-generated catch block
					System.out.println("[System Error] Get music failed");
				} 
			}
		}
		
		//printSubjectEntry(subjectEntry);
		
	}
	private static void analyze_and_store_book(SubjectEntry subjectEntry){
		String title ="", author ="", summary ="", id="", douban_id="",douban_link="",isbn10="",isbn13="",price="";
		String publisher = "", binding="",pubdate="";
		//��ȡtitle
		title   = subjectEntry.getTitle().getPlainText();
		//��ȡauthor
		List<Person> authors = subjectEntry.getAuthors();
		if(authors.isEmpty() == false){
			for(Person ath : authors ){
				author = author + "  " + ath.getName();
			}
		}
		//��ȡsummary
		TextConstruct sbe = subjectEntry.getSummary();
		if(sbe != null){
			summary = subjectEntry.getSummary().getPlainText().replace("'", "\\'");
		}
		//��ȡid,douban_id
		id = subjectEntry.getId();
		douban_id = id.substring(id.length()-7, id.length());
		//��ȡdouban_link
		douban_link = subjectEntry.getLinks().get(1).getHref();
		
		for (Attribute attr : subjectEntry.getAttributes()) {
			if("isbn10".equals(attr.getName())){
				//��ȡcountry
				isbn10 = attr.getContent();
			}else if("isbn13".equals(attr.getName())){
				//��ȡwriter
				isbn13 = attr.getContent();
			}else if("price".equals(attr.getName())){
				//��ȡlanguage
				price = attr.getContent();
			}else if("publisher".equals(attr.getName())){
				//��ȡdirector
				publisher = attr.getContent();
			}else if("pubdate".equals(attr.getName())){
				//��ȡpubdate
				pubdate = attr.getContent();
			}else if("binding".equals(attr.getName())){
				//��ȡaka
				binding = attr.getContent();
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
		//��book��Ϣд�����ݿ�
		store_sql(insert_book_sql);
		
		Rating rating = subjectEntry.getRating();
		if(rating != null){
			int minrate = 0; int maxrate = 0; float avgrate = 0; int ratenum = 0;
			//��ȡ��С���֣�������֣�ƽ�����֣�����������Ϣ��
			minrate = rating.getMin();
			maxrate = rating.getMax();
			avgrate = rating.getAverage();
			ratenum = rating.getNumRaters();
			//��������Ϣratingд��dr_subject_rating���ݿ�
			String insert_rating_sql = "INSERT INTO `" + Constant.DB_DATABASE+"`.`dr_subject_rating` (`minrate`,`maxrate`,`avgrate`,`ratenum`,`target_id`) VALUES "
				+"('"+minrate+"','"+maxrate+"','"+avgrate+"','"+ratenum+"','"+douban_id+"')";
			store_sql(insert_rating_sql);
		}
		
		//�ٻ�ȡtagֵ
		List<Tag> tags = subjectEntry.getTags();
		if(tags.isEmpty() == false){
			String insert_tag_sql = "INSERT INTO `"+Constant.DB_DATABASE+"`.`dr_subject_tag` (`tag`,`count`,`target_id`) VALUES ";
			for (Tag tag : tags) {
				insert_tag_sql = insert_tag_sql + "('" + tag.getName() + "','" + tag.getCount() + "','" + douban_id + "'),";
				System.out.println(tag.getName() + " : " + tag.getCount());
			}
			insert_tag_sql = insert_tag_sql.substring(0, insert_tag_sql.length()-1)+";";
			//����book��ص�tagд�����ݿ�
			store_sql(insert_tag_sql);
		}
	}
	private static void analyze_and_store_movie(SubjectEntry subjectEntry){
		String title ="", author ="", summary ="", id="", douban_id="",douban_link="";
		//��ȡtitle
		title   = subjectEntry.getTitle().getPlainText();
		//��ȡauthor
		List<Person> authors = subjectEntry.getAuthors();
		if(authors.isEmpty() == false){
			for(Person ath : authors ){
				author = author + "  " + ath.getName();
			}
		}
		//��ȡsummary
		TextConstruct sbe = subjectEntry.getSummary();
		if(sbe.isEmpty()){
			summary = subjectEntry.getSummary().getPlainText().replace("'", "\\'");
		}
		//��ȡid,douban_id
		id = subjectEntry.getId();
		douban_id = id.substring(id.length()-7, id.length());
		//��ȡdouban_link
		douban_link = subjectEntry.getLinks().get(1).getHref();
		//����attributes
		String writer="",language="",cast="",country="",director="",pubdate="",aka="",imdb="";
		for (Attribute attr : subjectEntry.getAttributes()) {
			if("country".equals(attr.getName())){
				//��ȡcountry
				country = attr.getContent();
			}else if("writer".equals(attr.getName())){
				//��ȡwriter
				writer = writer + "   " + attr.getContent();
			}else if("language".equals(attr.getName())){
				//��ȡlanguage
				language = language + "   " + attr.getContent();
			}else if("cast".equals(attr.getName())){
				//��ȡcast
				cast  = cast + "   " + attr.getContent();
			}else if("director".equals(attr.getName())){
				//��ȡdirector
				director = attr.getContent();
			}else if("pubdate".equals(attr.getName())){
				//��ȡpubdate
				pubdate = attr.getContent();
			}else if("aka".equals(attr.getName())){
				//��ȡaka
				aka = attr.getContent();
			}else if("imdb".equals(attr.getName())){
				//��ȡimdb
				imdb = attr.getContent();
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
		//��movie��Ϣд�����ݿ�
		store_sql(insert_movie_sql);
		
		Rating rating = subjectEntry.getRating();
		if(rating != null){
			int minrate = 0; int maxrate = 0; float avgrate = 0; int ratenum = 0;
			//��ȡ��С���֣�������֣�ƽ�����֣�����������Ϣ��
			minrate = rating.getMin();
			maxrate = rating.getMax();
			avgrate = rating.getAverage();
			ratenum = rating.getNumRaters();
			//��������Ϣratingд��dr_subject_rating���ݿ�
			String insert_rating_sql = "INSERT INTO `" + Constant.DB_DATABASE+"`.`dr_subject_rating` (`minrate`,`maxrate`,`avgrate`,`ratenum`,`target_id`) VALUES "
				+"('"+minrate+"','"+maxrate+"','"+avgrate+"','"+ratenum+"','"+douban_id+"')";
			store_sql(insert_rating_sql);
		}
		
	
		//�ٻ�ȡtagֵ
		List<Tag> tags = subjectEntry.getTags();
		if(tags.isEmpty() == false){
		String insert_tag_sql = "INSERT INTO `"+Constant.DB_DATABASE+"`.`dr_subject_tag` (`tag`,`count`,`target_id`) VALUES ";
		for (Tag tag : tags) {
			insert_tag_sql = insert_tag_sql + "('" + tag.getName() + "','" + tag.getCount() + "','" + douban_id + "'),";
			System.out.println(tag.getName() + " : " + tag.getCount());
		}
		insert_tag_sql = insert_tag_sql.substring(0, insert_tag_sql.length()-1)+";";
		//����movie��ص�tagд�����ݿ�
		store_sql(insert_tag_sql);
		}
	}
	private static void analyze_and_store_music(SubjectEntry subjectEntry){
		String title ="", author ="", summary ="", id="", douban_id="",douban_link="";
		//��ȡtitle
		title   = subjectEntry.getTitle().getPlainText();
		//��ȡauthor
		List<Person> authors = subjectEntry.getAuthors();
		if(authors.isEmpty() == false){
			for(Person ath : authors ){
				author = author + "  " + ath.getName();
			}
		}
		//��ȡsummary
		TextConstruct sbe = subjectEntry.getSummary();
		if(sbe != null){
			summary = subjectEntry.getSummary().getPlainText().replace("'", "\\'");
			System.out.println("[System Debug] Summary is:\n"+summary);
		}
		//��ȡid,douban_id
		id = subjectEntry.getId();
		douban_id = id.substring(id.length()-7, id.length());
		//��ȡdouban_link
		douban_link = subjectEntry.getLinks().get(1).getHref();
		//����attributes
		String ean="",tracks="",cast="",discs="",media="",pubdate="",aka="",singer="";
		for (Attribute attr : subjectEntry.getAttributes()) {
			if("discs".equals(attr.getName())){
				//��ȡdiscs
				discs = attr.getContent();
			}else if("ean".equals(attr.getName())){
				//��ȡean
				ean = attr.getContent();
			}else if("tracks".equals(attr.getName())){
				//��ȡlanguage
				tracks = tracks + "   " + attr.getContent();
			}else if("cast".equals(attr.getName())){
				//��ȡcast
				cast  = cast + "   " + attr.getContent();
			}else if("media".equals(attr.getName())){
				//��ȡmedia
				media = attr.getContent();
			}else if("pubdate".equals(attr.getName())){
				//��ȡpubdate
				pubdate = attr.getContent();
			}else if("aka".equals(attr.getName())){
				//��ȡaka
				aka = attr.getContent();
			}else if("singer".equals(attr.getName())){
				//��ȡsinger
				singer = attr.getContent();
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
		//��music��Ϣд�����ݿ�
		store_sql(insert_music_sql);
		
		Rating rating = subjectEntry.getRating();
		if(rating != null){
			int minrate = 0; int maxrate = 0; float avgrate = 0; int ratenum = 0;
			//��ȡ��С���֣�������֣�ƽ�����֣�����������Ϣ��
			minrate = rating.getMin();
			maxrate = rating.getMax();
			avgrate = rating.getAverage();
			ratenum = rating.getNumRaters();
			//��������Ϣratingд��dr_subject_rating���ݿ�
			String insert_rating_sql = "INSERT INTO `" + Constant.DB_DATABASE+"`.`dr_subject_rating` (`minrate`,`maxrate`,`avgrate`,`ratenum`,`target_id`) VALUES "
				+"('"+minrate+"','"+maxrate+"','"+avgrate+"','"+ratenum+"','"+douban_id+"')";
			store_sql(insert_rating_sql);
		}
		
		
		//�ٻ�ȡtagֵ
		List<Tag> tags = subjectEntry.getTags();
		if(tags.isEmpty() == false){
		String insert_tag_sql = "INSERT INTO `"+Constant.DB_DATABASE+"`.`dr_subject_tag` (`tag`,`count`,`target_id`) VALUES ";
		for (Tag tag : tags) {
			insert_tag_sql = insert_tag_sql + "('" + tag.getName() + "','" + tag.getCount() + "','" + douban_id + "'),";
			System.out.println(tag.getName() + " : " + tag.getCount());
		}
		insert_tag_sql = insert_tag_sql.substring(0, insert_tag_sql.length()-1)+";";
		//����book��ص�tagд�����ݿ�
		store_sql(insert_tag_sql);
		}
		
	}
	
	private static void store_sql(String sql){
		System.out.println("[System Info] Insert sql:\n" + sql);
		if(db.OpenConnection()){
			db.ExecuteUpdate(sql);
		}
	}
	
}