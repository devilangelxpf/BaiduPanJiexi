package com.youkujiexi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getYouKuDownloadUrl("");
    }

    //http://player.youku.com/player.php/Type/Folder/Fid/27706148/Ob/1/sid/XMTY1MzI2MzA1Mg==/v.swf
    private String getYouKuDownloadUrl(String url)
    {
        //get id XMTY1MzI2MzA1Mg==

        String id = getVid(url);

        //connect url http://play.youku.com/play/get.json?vid=XMTY1MzI2MzA1Mg==&ct=12

        url = "http://play.youku.com/play/get.json?vid=XMTY1MzI2MzA1Mg==&ct=12";

        String [] values = getValues("XMTQzNzIwODQ3Ng==","NgXWRwwWJr7Y1/fE/OJxV4D2uhdu1wrIXx0=");
        if (values!=null && values.length == 3){
            String sid = values[0];
            String token = values[1];
            String ep = values[2];
            ep = URLEncoder.encode(ep);
            Log.e("--xpf--sid",sid);
            Log.e("--xpf--token",token);
            Log.e("--xpf--ep",ep);
            Log.e("--xpf--vid","XMTY1MzI2MzA1Mg==");
            Log.e("--xpf--oip","2937657357");//
            Log.e("--xpf--type","mp4hd");


        }

        return url;
    }

    public static String getVid(String url) {
        String s = "";
        String strRegex = "(?<=id_)(\\w+)";
        Pattern pattern = Pattern.compile(strRegex);
        Matcher matcher = pattern.matcher(url);
        while (matcher.find()) {
            s = matcher.group();
        }
        return s;
    }

    /**
     * 获取到优酷链接的sid token 新的ep
     *
     * @param vid
     * @param ep
     *            旧的ep
     * @return rs[0] = sid; rs[1] = token; rs[2] = epNew;
     */
    public static String[] getValues(String vid, String ep) {
        try {
            String template1 = "becaf9be";
            String template2 = "bf7e5f01";
            byte[] bytes = Base64.decode(ep, Base64.DEFAULT);
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
                byte[] bytes = new byte[] { (byte) (c[q] ^ b[(b[h] + b[f]) % 256]) };
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
