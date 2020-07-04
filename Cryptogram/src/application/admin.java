package application;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class admin {

	private String id;
	private String pass;
	
	public admin(){
		id = "admin";
		pass = "daaad6e5604e8e17bd9f108d91e26afe6281dac8fda0091040a7a6d7bd9b43b5";
	}

	public boolean verifyAdmin(String id, String pass){
		if(this.id.equals(id) && this.pass.equals(Compute_hash(pass))) return true;
		return false;
	}
	
	static String Compute_hash(String pswd){
		byte pass[] = pswd.getBytes();
		StringBuilder sb = new StringBuilder();
		try{
			MessageDigest d = MessageDigest.getInstance("SHA-256");
			d.update(pass);
			pass = d.digest();
            for(int i=0; i< pass.length ;i++)
            	sb.append(Integer.toString((pass[i] & 0xff) + 0x100, 16).substring(1));
		}
		catch (NoSuchAlgorithmException e){System.out.println("Exception");}
		return sb.toString();
	}

}
