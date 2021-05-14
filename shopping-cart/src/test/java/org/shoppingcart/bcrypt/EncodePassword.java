package org.shoppingcart.bcrypt;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class EncodePassword {

	public static void main(String[] args) {
		String password="123456";
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodedPassword = encoder.encode(password);
		System.out.println("Encoded Password: "+encodedPassword);
		System.out.println("Password Length: "+encodedPassword.length());
	}
}
