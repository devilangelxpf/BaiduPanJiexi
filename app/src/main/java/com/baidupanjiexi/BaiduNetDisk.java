package com.baidupanjiexi;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
public class BaiduNetDisk {
    public static List<Map<String, Object>> getUrl(String url) throws Exception {
        List<String> fs_id = new ArrayList<String>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String,String> cookie = new HashMap<>();
        cookie.put("BAIDUID", "573CB59D06D488C7E2CE85CBCC3CE69E:FG=1");
        cookie.put("PANWEB", "1");
        Document doc = Jsoup.connect(url)
        		.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36")
                .timeout(1000*5)
                .header("Accept-Encoding", "gzip, deflate, sdch")
                .cookies(cookie)
                .get();
        String html = doc.toString();
        int a = html.indexOf("{\"typicalPath");
        int b = html.indexOf("yunData.getCon");
        int sign_head = html.indexOf("yunData.SIGN = \"");
        int sign_foot = html.indexOf("yunData.TIMESTAMP");
        int time_head = html.indexOf("yunData.TIMESTAMP = \"");
        int time_foot = html.indexOf("yunData.SHARE_UK");
        int share_id_head = html.indexOf("yunData.SHARE_ID = \"");
        int share_id_foot = html.indexOf("yunData.SIGN ");
        String sign = html.substring(sign_head, sign_foot);
        sign = sign.substring(sign.indexOf("\"") + 1, sign.indexOf("\";"));
        String time = html.substring(time_head, time_foot);
        time = time.substring(time.indexOf("\"") + 1, time.indexOf("\";"));
        String share_id = html.substring(share_id_head, share_id_foot);
        share_id = share_id.substring(share_id.indexOf("\"") + 1,
                share_id.indexOf("\";"));
        System.out.println(share_id);
        html = html.substring(a, b);
        a = html.indexOf("{\"typicalPath");
        b = html.indexOf("};");
        JSONArray jsonArray = new JSONArray("[" + html.substring(a, b + 1)
                + "]");
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        String uk = jsonObject.getString("uk");
        String shareid = jsonObject.getString("shareid");
        String path = URLEncoder.encode(jsonObject.getString("typicalPath"),
                "utf-8");
        jsonArray = new JSONArray("[" + jsonObject.getString("file_list") + "]");
        jsonObject = jsonArray.getJSONObject(0);
        jsonArray = new JSONArray(jsonObject.getString("list"));
        jsonObject = jsonArray.getJSONObject(0);
        String app_id = jsonObject.getString("app_id");
        if (jsonObject.getString("isdir").equals("1")) {
            String url1 = "http://pan.baidu.com/share/list?uk="
                    + uk
                    + "&shareid="
                    + shareid
                    + "&page=1&num=100&dir="
                    + path
                    + "&order=time&desc=1&_="
                    + time
                    + "&bdstoken=c51077ce0e0e313a16066612a13fbcd4&channel=chunlei&clienttype=0&web=1&app_id="
                    + app_id;
            String fileListJson = HttpRequest.getData(url1);
            System.out.println(fileListJson);
            jsonArray = new JSONArray("[" + fileListJson + "]");
            jsonObject = jsonArray.getJSONObject(0);
            jsonArray = new JSONArray(jsonObject.getString("list"));
        }
        final int size = jsonArray.length();
        for (int i = 0; i < size; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            jsonObject = jsonArray.getJSONObject(i);
            String fileName = jsonObject.getString("server_filename");
            map.put("fileName", fileName);
            fs_id.add(jsonObject.getString("fs_id"));
            String fileInfo = HttpRequest
                    .getData("http://pan.baidu.com/api/sharedownload?sign="
                            + sign
                            + "&timestamp="
                            + time
                            + "&bdstoken=c51077ce0e0e313a16066612a13fbcd4&channel=chunlei&clienttype=0&web=1&app_id=250528&encrypt=0&product=share&tamp=1&shareid=199488145&type=dlink&encrypt=0&extra=%7B%22sekey%22%3A%22null%22%7D&product=share&uk="
                            + uk + "&primaryid=" + share_id + "&fid_list=%5B"
                            + fs_id.get(i) + "%5D");
            JSONArray jsonArray2 = new JSONArray("[" + fileInfo + "]");
            JSONObject json_data = jsonArray2.getJSONObject(0);
            if (json_data.getString("errno").equals("0")) {
                jsonArray2 = new JSONArray(json_data.getString("list"));
                json_data = jsonArray2.getJSONObject(0);
                map.put("url", json_data.getString("dlink"));
            } else if (json_data.getString("errno").equals("-20")) {
                return null;
            } else {
                return null;
            }
            list.add(map);
        }
        return list;
    }
}