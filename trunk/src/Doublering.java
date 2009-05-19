import thd.Parserthd;
import db.Dbo;

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
		new Parserthd("people").start();//开启用户抓取进程
		//new Parserthd("book").start();//开启subject抓取进程
	}
}
