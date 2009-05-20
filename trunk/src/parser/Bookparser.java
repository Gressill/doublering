package parser;

import hander.BookHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.google.gdata.client.douban.DoubanService;
import com.google.gdata.data.douban.Attribute;
import com.google.gdata.data.douban.SubjectEntry;
import com.google.gdata.data.douban.SubjectFeed;
import com.google.gdata.data.douban.Tag;
import com.google.gdata.data.extensions.Rating;
import com.google.gdata.util.ServiceException;


public class Bookparser {
	
	private String _url = "";
	private String _apiKey = "06d5aaf0b4b5f090148100d21e21d1b0";
	private String _userentrylink = "http://api.douban.com/book/subject/";
	private String bookid = new String("");
	private String apiKey = "059ef56f6b705e1210dce04e42511a36";
	private String secret = "006ba4a489916c13";
	
	DoubanService myService;
	public Bookparser(){

		myService = new DoubanService("subApplication", apiKey,secret);
		
		
		//hander = new BookHandler();
	}
	public void setBookid(String id){
		bookid = id;
	}
	public void parse(){
		SubjectEntry subjectEntry;
		try {

			subjectEntry = myService.getBook(bookid);
			printSubjectEntry(subjectEntry);

			// tag=cowboy&start-index=1&max-results=2
			// q=null
			SubjectFeed subjectFeed = myService.findMovie(null, "cowboy", 1, 2);
			for (SubjectEntry sf : subjectFeed.getEntries()) {
				printSubjectEntry(sf);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
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
	
	public void setURL(String url){
		_url = url;
	}
	
}