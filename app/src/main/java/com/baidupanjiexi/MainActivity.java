package com.baidupanjiexi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

	private String url = "http://pan.baidu.com/s/1c1IslQo";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TextView text = (TextView)findViewById(R.id.text);
		new Thread(){
			public void run() {
				try {
					List<Map<String, Object>> urlMap = BaiduNetDisk.getUrl(url);
					Log.e("xpf","---------------------"+urlMap.get(2).get("url"));
				} catch (Exception e) {
					Log.e("xpf","---------------------ERROR-----------");
					e.printStackTrace();
				}
			};
		}.start();
		
	}
}
