package com.baidupanjiexi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class HttpRequest {
	public static String getData(String u) throws Exception {
		URL url = new URL(u);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url
				.openConnection();
		httpURLConnection.setRequestMethod("POST");
		httpURLConnection.setDoInput(true);
		httpURLConnection.setDoOutput(true);
		httpURLConnection.addRequestProperty("Accept", "*/*");
		httpURLConnection
				.addRequestProperty(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36");
		httpURLConnection.addRequestProperty("X-Requested-With",
				"XMLHttpRequest");
		httpURLConnection.addRequestProperty("content-type",
				"application/x-www-form-urlencoded;charset=UTF-8");
		InputStream is = httpURLConnection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is,
				"utf-8"));
		StringBuffer resultBuffer = new StringBuffer();
		String tempLine = null;
		while ((tempLine = br.readLine()) != null) {
			resultBuffer.append(tempLine);
		}
		String res = resultBuffer.toString();
		Log.e("xpf", res);
		return res;
	}
}
