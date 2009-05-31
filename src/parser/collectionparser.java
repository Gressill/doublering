package parser;

import java.io.IOException;
import java.util.List;

import util.Constant;

import com.google.gdata.client.douban.DoubanService;
import com.google.gdata.data.Link;
import com.google.gdata.data.Person;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.douban.Attribute;
import com.google.gdata.data.douban.CollectionEntry;
import com.google.gdata.data.douban.CollectionFeed;
import com.google.gdata.data.douban.Subject;
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
			parseBook();
			Thread.sleep(1500);
			parseMovie();
			Thread.sleep(1500);
			parseMusic();
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
	private  void parseBook() throws IOException, ServiceException{
		CollectionFeed cf = myService.getUserCollections(uid, "book", null, null, 1, 5);

		for (CollectionEntry ce : cf.getEntries()) {
			parseCollectionEntry(ce);
			printCollectionEntry(ce);
			
		}
	}
	private void parseMovie() throws IOException, ServiceException{
		CollectionFeed cf = myService.getUserCollections(uid, "movie", null, null, 1, 5);
	}
	private void parseMusic() throws IOException, ServiceException{
		CollectionFeed cf = myService.getUserCollections(uid, "music", null, null, 1, 5);
	}

	private static void parseCollectionEntry(CollectionEntry ce){
		String id = ce.getId();
		String title = ce.getTitle().getPlainText();
		String status = ce.getStatus().getContent();
		
	}
	private static void printCollectionEntry(CollectionEntry ce) {

		System.out.println("id is " + ce.getId());
		System.out.println("title is " + ce.getTitle().getPlainText());
		if (!ce.getAuthors().isEmpty()) {
			System.out.println("author name is : "
					+ ce.getAuthors().get(0).getName());
			System.out.println("author URI is : "
					+ ce.getAuthors().get(0).getUri());
		}
		System.out.println("status is " + ce.getStatus().getContent());

		printSubjectEntry(ce.getSubjectEntry());

		Rating rating = ce.getRating();
		if (rating != null)
			System.out.println("max is " + rating.getMax() + " min is "
					+ rating.getMin() + " value is " + rating.getValue()
					+ " numRaters is " + rating.getNumRaters() + " average is "
					+ rating.getAverage());
		System.out.println("Tags:");
		for (Tag tag : ce.getTags()) {
			System.out.println(tag.getName());
		}
	}
	private static void printSubjectEntry(Subject se) {

		if (se == null)
			return;
		if (se.getSummary() != null)
			System.out.println("summary is " + se.getSummary().getPlainText());
		System.out.println("author is " + se.getAuthors().get(0).getName());
		System.out.println("title is " + se.getTitle().getPlainText());

		for (Attribute attr : se.getAttributes()) {
			System.out.println(attr.getName() + " : " + attr.getContent());
		}
		System.out.println("id is " + se.getId());
		for (Tag tag : se.getTags()) {
			System.out.println(tag.getName() + " : " + tag.getCount());
		}

		Rating rating = se.getRating();
		if (rating != null)
			System.out.println("max is " + rating.getMax() + " min is "
					+ rating.getMin() + " numRaters is "
					+ rating.getNumRaters() + " average is "
					+ rating.getAverage());
		System.out.println("********************");
	}
}
