package net.batkin.forms.server.db.utility;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class FormUtility {

	public static String generateChecksum(String input) throws IOException {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			throw new IOException("Unable to load SHA1 algorithm");
		}
		byte[] bytes = input.getBytes("UTF-8");
		byte[] digest = md.digest(bytes);

		Formatter formatter = new Formatter();
		for (byte b : digest) {
			formatter.format("%02x", Byte.valueOf(b));
		}
		return formatter.toString();
	}

}
