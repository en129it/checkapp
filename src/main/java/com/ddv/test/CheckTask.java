package com.ddv.test;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.core.env.Environment;

public class CheckTask implements Runnable {

	private String userName;
	private String userPassword;
	private Environment environment;
	private List<String> resourceIdList;
	private String cookie;
	
	public CheckTask(String aUserName, String aUserPassword, List<String> aResourceIdList, Environment anEnvironment) {
		userName = aUserName;
		userPassword = aUserPassword;
		resourceIdList = aResourceIdList;
		environment = anEnvironment;
	}
	
	@Override
	public void run() {
		try {
			cookie = "";
			CookieHandler.setDefault(new CookieManager());
			
			CloseableHttpClient client = HttpClientBuilder.create().build();
			HttpGet loginRequest = new HttpGet(getLoginUrl());
			HttpResponse loginResponse = client.execute(loginRequest);
			
			if (HttpStatus.SC_OK==loginResponse.getStatusLine().getStatusCode()) {
				extractCookie(loginResponse);

				HttpGet checkinRequest = new HttpGet(getCheckinUrl());
				addCookieHeader(checkinRequest);
				HttpResponse checkinResponse = client.execute(checkinRequest);

				if (HttpStatus.SC_OK==checkinResponse.getStatusLine().getStatusCode()) {
					extractCookie(checkinResponse);
		
					for (String resourceId: resourceIdList) {
						try {
							HttpGet bookRequest = new HttpGet(getBookUrl(resourceId));
							addCookieHeader(bookRequest);
							HttpResponse bookResponse = client.execute(bookRequest);
							if (HttpStatus.SC_OK==bookResponse.getStatusLine().getStatusCode()) {
								break;
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}				
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private String getLoginUrl() {
		return replaceToken(replaceToken(environment.getProperty("login.url"), "userName", userName), "userPassword", userPassword);
	}
	
	private String getCheckinUrl() {
		return environment.getProperty("checkin.url");
	}
	
	private String getBookUrl(String aResourceId) {
		LocalDate tomorrow = LocalDate.now().plusDays(1L);
		DayOfWeek dayOfWeek = tomorrow.getDayOfWeek();
		if (DayOfWeek.SATURDAY.equals(dayOfWeek)) {
			tomorrow = tomorrow.plusDays(2);
		} else if (DayOfWeek.SUNDAY.equals(dayOfWeek)) {
			tomorrow = tomorrow.plusDays(1);
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String fromDate = formatter.format(tomorrow);
		String toDate = formatter.format(tomorrow.plusDays(1));
		
		String rslt = replaceToken(replaceToken(replaceToken(environment.getProperty("book.url"), "resourceId", aResourceId), "fromDate", fromDate), "toDate", toDate);
		return rslt;
	}
	
	private String replaceToken(String aText, String aToken, String aValue) {
		return aText.replace("{" + aToken + "}", aValue);
	}
	
	private void extractCookie(HttpResponse aResponse) {
		Header cookieHeader = aResponse.getFirstHeader("Set-Cookie");
		cookie = (cookieHeader!=null) ? cookieHeader.toString() : "";
	}
	
	private void addCookieHeader(HttpRequestBase aRequest) {
		aRequest.addHeader("Cookie", cookie);
	}
}
