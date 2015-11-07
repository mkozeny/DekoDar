package cz.mkozeny.dekodar.session;



import java.security.MessageDigest;

import org.jboss.seam.util.Hex;

public class PasswordHasher {

	
	public static String hashPassword(String password, String saltPhrase)
	{
	    try {
	       MessageDigest md = MessageDigest.getInstance("MD5");
	       md.update(saltPhrase.getBytes());
	       byte[] salt = md.digest();
	       md.reset();
	       md.update(password.getBytes("UTF-8"));
	       md.update(salt);
	       byte[] raw = md.digest();
	       return new String(Hex.encodeHex(raw));
	  }
	  catch (Exception e) {
	       throw new RuntimeException(e);
	  }
	}
	
	public static void main (String[] args) {
		
		System.out.println(hashPassword("pass1234", "admin"));
	}
}
