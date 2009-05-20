package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import util.Constant;

/**
 * use to operation database
 * @author yufei
 *
 */

public class Dbo {

	private String ClassString		 	= null;
	private String ConnectionString 		= null;
	private String UserName 				= null;
	private String PassWord 				= null;

	private Connection Conn;
	private Statement Stmt;

	public Dbo() {
		ClassString 		= "com.mysql.jdbc.Driver";//"oracle.jdbc.driver.OracleDriver";
		ConnectionString 	= "jdbc:mysql://localhost:3306/" + Constant.DB_DATABASE;//"jdbc:oracle:thin:@192.168.103.171:1521:jstrd";
		UserName 			= Constant.DB_USER_NAME;
		PassWord 			= Constant.DB_PASSWORD;
		}

	//打开连接
	public boolean OpenConnection() {
		boolean mResult = true;
		try {
			Class.forName(ClassString);
			if ((UserName == null) && (PassWord == null)) {
				Conn = DriverManager.getConnection(ConnectionString);
			} else {
				Conn = DriverManager.getConnection(ConnectionString, UserName,
						PassWord);
			}

			Stmt = Conn.createStatement();
			mResult = true;
		} catch (Exception e) {
			System.out.println(e.toString()+"\n"+"Error Message: Database Connection Open Error reported in line 56 DatabaseOperation.java");
			mResult = false;
		}
		return (mResult);
	}

	//关闭数据库连接
	public void CloseConnection() {
		try {
			Stmt.close();
			Conn.close();
		} catch (Exception e) {
			System.out.println(e.toString());
			System.out.println(e.toString()+"\n"+"Error Message: Database Connection Close Error reported in line 69 DatabaseOperation.java");
		}
	}

	//获取当前时间(JAVA)
	public String GetDateTime() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mDateTime = formatter.format(cal.getTime());
		return (mDateTime);
	}

	//获取当前时间(T-SQL)
	public java.sql.Date GetDate() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String mDateTime = formatter.format(cal.getTime());
		return (java.sql.Date.valueOf(mDateTime));
	}

	//生成新的ID
	public int GetMaxID(String vTableName, String vFieldName) {
		int mResult = 0;
		boolean mConn = true;
		String mSql = new String();
		mSql = "select max(" + vFieldName + ")+1 as MaxID from " + vTableName;
		try {
			if (Conn != null) {
				mConn = Conn.isClosed();
			}
			if (mConn) {
				OpenConnection();
			}

			ResultSet result = ExecuteQuery(mSql);
			if (result.next()) {
				mResult = result.getInt("MaxID");
			}
			result.close();

			if (mConn) {
				CloseConnection();
			}

		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return (mResult);
	}

	//数据检索
	public ResultSet ExecuteQuery(String SqlString) {
		ResultSet result = null;
		try {
			result = Stmt.executeQuery(SqlString);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return (result);
	}

	//数据更新(增、删、改)
	public int ExecuteUpdate(String SqlString) {
		int result = 0;
		try {
			result = Stmt.executeUpdate(SqlString);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return (result);
	}

	public static void testSql() {
		String sqlString = "select * from `member`";
		String sqlInsert = "INSERT INTO `member` (`username`,`commomname`,`place`) VALUES ('abelfly','交易专家','chengdu')";
		//String ssString = "INSERT INTO `price_info` (`price`) VALUES (34.90),(12),(25)";
		ResultSet res;
		Dbo dbo = new Dbo();
		if(dbo.OpenConnection())
		{
			int i = dbo.ExecuteUpdate(sqlInsert);//I,U,D
			System.out.print("i="+i);
			res = dbo.ExecuteQuery(sqlString);//S
			//处理结果集
			try {
				while (res.next()) {
					String name = res.getString("username");
					System.out.println(name);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				dbo.CloseConnection();
			}
		}
	}
}