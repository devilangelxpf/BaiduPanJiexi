package com.youkujiexi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private String startUrl = "http://player.youku.com/player.php/sid/XMTY0NTk4NDc2MA==/v.swf";
    private String ykss = "";
    private String __ysuid = "";
    private String firstUrl = "";
    private String ep = "";
    private String ip = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread() {
            public void run() {
                try {
                    getYouKuDownloadUrl(startUrl);
                } catch (Exception e) {
                }
            }
        }.start();

    }

    //http://player.youku.com/player.php/Type/Folder/Fid/27706148/Ob/1/sid/XMTY1MzI2MzA1Mg==/v.swf
    private void getYouKuDownloadUrl(String url) {
        String id = getVid(url);
        firstGet(id);
        getPvid();
        secondGet(id);
    }

    private void secondGet(String id) {
        try {
            String secondUrl = "http://play.youku.com/play/get.json?vid=";//&ct=12
            URL url = new URL(secondUrl + id + "&ct=12");
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
            httpURLConnection.addRequestProperty("content-type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            httpURLConnection.addRequestProperty("Cookie", "ykss=" + ykss + ";__ysuid=" + __ysuid);
            httpURLConnection.addRequestProperty("Referer", firstUrl);
            InputStream is = httpURLConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is,
                    "utf-8"));
            StringBuffer resultBuffer = new StringBuffer();
            String tempLine = null;
            while ((tempLine = br.readLine()) != null) {
                resultBuffer.append(tempLine);
            }
            String res = resultBuffer.toString();
            JSONObject resJson = JSON.parseObject(res);
            ep = JSON.parseObject(JSON.parseObject(resJson.getString("data")).getString("security")).getString("encrypt_string");
            ip = JSON.parseObject(JSON.parseObject(resJson.getString("data")).getString("security")).getString("ip");
            String[] values = getValues(id, ep);
            if (values != null && values.length == 3) {
                String sid = values[0];
                String token = values[1];
                String ep = values[2];
                ep = URLEncoder.encode(ep);
                StringBuilder sb = new StringBuilder();
                sb.append("http://pl.youku.com/playlist/m3u8?vid=")
                        .append(id).append("&type=mp4&ts=").append(System.currentTimeMillis())
                        .append("&keyframe=0&ep=").append(ep).append("&sid=").append(sid)
                        .append("&token=").append(token).append("&ctype=12&ev=1&oip=").append(ip);
                getRealDownloadUrl(sb.toString());
            }
        } catch (Exception e) {
        }
    }

    private void getRealDownloadUrl(String start) {
        try {
            URL url = new URL(start);
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
            httpURLConnection.addRequestProperty("content-type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            httpURLConnection.addRequestProperty("Cookie", "ykss=" + ykss + ";__ysuid=" + __ysuid);
            httpURLConnection.addRequestProperty("Referer", firstUrl);
            InputStream is = httpURLConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is,
                    "utf-8"));
            StringBuffer resultBuffer = new StringBuffer();
            String tempLine = null;
            while ((tempLine = br.readLine()) != null) {
                resultBuffer.append(tempLine);
            }
            String res = resultBuffer.toString();
            String realUrl = res.substring(res.indexOf("http://"), res.indexOf(".ts"));
            Log.e(TAG, "----xpf--realUrl----" + realUrl);
        } catch (Exception e) {

        }
    }

    private void firstGet(String id) {
        try {
            firstUrl = "http://v.youku.com/v_show/id_" + id + ".html";//.html
            URL url = new URL(firstUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.addRequestProperty("Accept", "*/*");
            httpURLConnection
                    .addRequestProperty(
                            "User-Agent",
                            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36");
            httpURLConnection.addRequestProperty("content-type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            InputStream is = httpURLConnection.getInputStream();
            //获取cookie
            List<String> cookies = httpURLConnection.getHeaderFields().get("Set-Cookie");
            if (cookies.size() > 0) {
                for (String cookie : cookies) {
                    if (cookie.contains("ykss")) {
                        ykss = cookie.substring(cookie.indexOf("ykss=") + 5, cookie.indexOf(";"));
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public void getPvid() {
        String[] randchar = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
        };
        String r = "";
        String seconds = String.valueOf(Calendar.getInstance().getTimeInMillis());
        for (int i = 0; i < 3; i++) {
            try {
                int idx = new Random().nextInt(randchar.length);
                r += randchar[idx];
            } catch (Exception e) {
            }
        }
        __ysuid = seconds + r;
    }


    public static String getVid(String url) {
        String s = "";
        s = url.substring(url.indexOf("sid/") + 4, url.indexOf("/v.swf"));
        return s;
    }

    /**
     * 获取到优酷链接的sid token 新的ep
     *
     * @param vid
     * @param mEp mEp
     * @return rs[0] = sid; rs[1] = token; rs[2] = epNew;
     */
    public static String[] getValues(String vid, String mEp) {
        try {
            String template1 = "becaf9be";
            String template2 = "bf7e5f01";
            byte[] bytes = Base64.decode(mEp, Base64.DEFAULT);
            String temp = myEncoder(template1, bytes, false);
            String[] part = temp.split("_");
            String sid = part[0];
            String token = part[1];
            String whole = sid + "_" + vid + "_" + token;
            byte[] newbytes = whole.getBytes("US-ASCII");
            String epNew = myEncoder(template2, newbytes, true);
            epNew = URLEncoder.encode(epNew);
            String[] rs = new String[3];
            rs[0] = sid;
            rs[1] = token;
            rs[2] = epNew;
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String myEncoder(String a, byte[] c, boolean isToBase64) {
        try {
            String result = "";
            ArrayList<Byte> bytesR = new ArrayList<Byte>();
            int f = 0, h = 0, q = 0;
            int[] b = new int[256];
            for (int i = 0; i < 256; i++)
                b[i] = i;
            while (h < 256) {
                f = (f + b[h] + a.charAt(h % a.length())) % 256;
                int temp = b[h];
                b[h] = b[f];
                b[f] = temp;
                h++;
            }
            f = 0;
            h = 0;
            q = 0;
            while (q < c.length) {
                h = (h + 1) % 256;
                f = (f + b[h]) % 256;
                int temp = b[h];
                b[h] = b[f];
                b[f] = temp;
                byte[] bytes = new byte[]{(byte) (c[q] ^ b[(b[h] + b[f]) % 256])};
                bytesR.add(bytes[0]);
                result += new String(bytes, "US-ASCII");
                q++;
            }
            if (isToBase64) {
                Byte[] byteR = bytesR.toArray(new Byte[bytesR.size()]);
                byte[] bs = new byte[byteR.length];
                for (int i = 0; i < byteR.length; i++) {
                    bs[i] = byteR[i].byteValue();
                }
                result = Base64.encodeToString(bs, Base64.DEFAULT);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
