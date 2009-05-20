package parser;

import java.io.IOException;

import util.Constant;

import com.google.gdata.client.douban.DoubanService;
import com.google.gdata.data.douban.Attribute;
import com.google.gdata.data.douban.SubjectEntry;
import com.google.gdata.data.douban.SubjectFeed;
import com.google.gdata.data.douban.Tag;
import com.google.gdata.data.extensions.Rating;
import com.google.gdata.util.ServiceException;

import db.Dbo;


public class Bookparser {
	
	private String _apiKey = Constant.api;
	private String _secret = Constant.secret;
	private String bookid;
	
	DoubanService myService;
	
	private Dbo db;
	
	public Bookparser(){
		//链接数据库
		try{
			db = new Dbo();
			if(db.OpenConnection()){
				System.out.println("[System Info] Database connected.");
			}
			//启动豆瓣服务
			myService = new DoubanService("book", _apiKey, _secret);
		}catch(Exception e){
				e.printStackTrace();
		}
	}
	public void setBookid(String id){
		//设置ID
		bookid = id;
	}
	public void parse(){
		bookid = "2032440";

		SubjectEntry subjectEntry = new SubjectEntry();
		try {//如果抓书成功，则抓书
			subjectEntry = myService.getBook(bookid);
			analyze_and_store_book(subjectEntry);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			System.out.print("[System Error] Get book failed");
			try {//如果不成功，则抓电影
				subjectEntry = myService.getMovie(bookid);
				analyze_and_store_movie(subjectEntry);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ServiceException e1) {
				// TODO Auto-generated catch block
				System.out.print("[System Error] Get movie failed");
				try {//如果不成功，则抓音乐
					subjectEntry = myService.getMusic(bookid);
					analyze_and_store_music(subjectEntry);
				}catch (IOException e2) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ServiceException e2) {
					// TODO Auto-generated catch block
					System.out.print("[System Error] Get music failed");
				} 
			}
		}
		
		//printSubjectEntry(subjectEntry);
		
	}
	private static void analyze_and_store_book(SubjectEntry subjectEntry){
		//获取title
		String title   = subjectEntry.getTitle().getPlainText();
		//获取author
		String author  = subjectEntry.getAuthors().get(0).getName();
		//获取summary
		String summary = subjectEntry.getSummary().getPlainText();
		//获取id
		String id = subjectEntry.getId();
		String douban_id = id.substring(id.length()-7, id.length());
		//String douban_link = subjectEntry.getHtmlLink().getHref();
		
		//System.out.println(douban_link);
	}
	private static void analyze_and_store_movie(SubjectEntry subjectEntry){
		
	}
	private static void analyze_and_store_music(SubjectEntry subjectEntry){
		
	}

	private static void printSubjectEntry(SubjectEntry subjectEntry) {

		if (subjectEntry.getSummary() != null)
			System.out.println("summary is "
					+ subjectEntry.getSummary().getPlainText());
		System.out.println("author is "
				+ subjectEntry.getAuthors().get(0).getName());
		System.out
				.println("title is " + subjectEntry.getTitle().getPlainText());

		for (Attribute attr : subjectEntry.getAttributes()) {
			System.out.println(attr.getName() + " : " + attr.getContent());
		}
		System.out.println("id is " + subjectEntry.getId());
		for (Tag tag : subjectEntry.getTags()) {
			System.out.println(tag.getName() + " : " + tag.getCount());
		}

		Rating rating = subjectEntry.getRating();
		if (rating != null)
			System.out.println("max is " + rating.getMax() + " min is "
					+ rating.getMin() + " numRaters is "
					+ rating.getNumRaters() + " average is "
					+ rating.getAverage());
		System.out.println("-------------------");
	}
	
}