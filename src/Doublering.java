import thd.Parserthd;
import util.Constant;

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
		
		if (Constant.initGameFromXml()) {
			new Parserthd(Constant.spideobject).start();//�����û�ץȡ����
			//new Parserthd("book").start();//����subjectץȡ����
		}
	}
}
