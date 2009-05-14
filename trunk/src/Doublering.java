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
//    	m.setPs(user_url+"abelfly");
//		m.render();
		//从1001001用户开始访问100次用户
		for(int i= 1001000; i<1001200; i++)
		{	
			m.setPs(user_url+i+"/");
			m.render();
		}
		System.out.println("Spiding the member from kouban, congratulations!");
	}
}
