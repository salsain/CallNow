package com.cyril_rayan.callnow.login.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Validation.java - a class for Email Varification
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 22/12/15.
 */
public class Validation {

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

	public static boolean specialCharacterValidation(String str) {
		String splChrs = "-/!@#$%^&*_+=()?<:;'>~`.,";
		return !(str.matches("[" + splChrs + "]+"));

	}
	public static boolean isValidEmail(String email) {
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);

		return matcher.matches();
	}
}
