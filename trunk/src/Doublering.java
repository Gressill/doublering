import parser.Html;


public class Doublering {

	/**
	 * @param args 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Html h = new Html("http://www.douban.com/subject/3535792/");
		h.render();
		System.out.println("Hello world!");
	}
}
