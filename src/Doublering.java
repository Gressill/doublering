import parser.Html;
import parser.Member;


public class Doublering {

	/**
	 * @param args 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Html h = new Html("http://www.douban.com/people/gtalklay/");
		//h.render();
		Member m = new Member("http://www.douban.com/people/abelfly/");
		String user_url = "http://www.douban.com/people/abelfly/";
		m.setPs(user_url);
		m.render();
		//从1001001用户开始访问100次用户
//		for(int i= 1001088; i<1001520; i++)
//		{
//			
//			m.setPs(user_url+i+"/");
//			m.render();
//		}
		//System.out.println("Hello world!");
	}
}
