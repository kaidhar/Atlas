package dataCheck;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.sql.Connection;

public class SSHTunnelMaker {

	/**
	 * Java Program to connect to remote database through SSH using port forwarding
	 * 
	 * @author Pankaj@JournalDev
	 * @throws SQLException
	 */
	public static void SSHMaker(String[] args) throws SQLException {

		int lport = 5656;
		String rhost = "secure.journaldev.com";
		String host = "secure.journaldev.com";
		int rport = 3306;
		String user = "sshuser";
		String password = "sshpassword";
		String dbuserName = "mysql";
		String dbpassword = "mysql123";
		String url = "jdbc:mysql://localhost:" + lport + "/mydb";
		String driverName = "com.mysql.jdbc.Driver";
		Connection conn = null;
		Session session = null;
		try {
			// Set StrictHostKeyChecking property to no to avoid UnknownHostKey issue
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();
			session = jsch.getSession(user, host, 22);
			session.setPassword(password);
			session.setConfig(config);
			session.connect();
			System.out.println("Connected");
			session.setPortForwardingL(lport, rhost, rport);
			System.out.println("Port Forwarded");

			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null && !conn.isClosed()) {
				System.out.println("Closing Database Connection");
				conn.close();
			}
			if (session != null && session.isConnected()) {
				System.out.println("Closing SSH Connection");
				session.disconnect();
			}
		}
		
		DataValidator DBV = new DataValidator();
		
		
		
	}

}