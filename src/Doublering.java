import db.Dbo;
import parser.Html;
import parser.Member;


public class Doublering {

	/**
	 * 
	 * preivate common resources
	 */
	/**
	 * @param args 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub}
		memspider();
	}
	
	public static void memspider(){
		
		Member m = new Member("http://www.douban.com/people/");
		String user_url = "http://www.douban.com/people/";
//    	m.setURL(user_url+"abelfly");
//		m.render();
//		Html h = new Html("http://www.douban.com/people/abelfly");
//		h.render();
		//从1001001用户开始访问500次用户
		for(int i= 1000001; i<1000300; i++)
		{	
			m.setURL(user_url+i+"/");
			m.render();
			//sleep(100);
		}
		System.out.println("Spiding the member from kouban, congratulations!");
	}
}
