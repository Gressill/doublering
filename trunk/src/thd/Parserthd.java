package thd;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import parser.Baseparser;

public class Parserthd extends Thread {
	
	private  String request = null; 
	
	public Parserthd(String re){
		super(re);
		request = re;
		System.out.println("[System Info] Start spiding " + re);
	}
	public void run(){
		
		//System.out.println("subject".equals(request));
		
		if("people".equals(request)){
			spiderpeople();
			
		}else if("subject".equals(request)){
			spidersubject();
		}
	}
	
	private void spidersubject(){
		System.out.println("[System Info] Spider people...");
		int i=0;
		do{
			if(i>10)break;
			
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}while(true);
		
	}
	private void spiderpeople(){
		Baseparser bp = new Baseparser();
		int i=0;
		
		System.out.println("[System Info] Spider people...");
		
		do{
			if(i>10)break;
			try {
				
				
				bp.sendRE();
				sleep(1000);
				
				
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
			
			i++;
		}while(true);

	}
	
}
