package com.ernest.mobilecontextaware.util;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;


public class HttpUtil {
	private static final String TAG = "HttpUtil";
	
	//private static final int CONNECT_TIME_OUT = 3000;
	//private static final int READ_TIME_OUT = 4000;
	//private static HttpURLConnection conn;
	    
    /**   
     * @param url 网页地址   
     * @param params 参数   
     * @return 返回网页内容   
     * @throws Exception
     * Ex.
     * String data;
     * List<NameValuePair> params = new ArrayList<NameValuePair>();   
     * params.add(new BasicNameValuePair("username", username));   
     * params.add(new BasicNameValuePair("password", password));   
     * data = HttpUtils.Post("http://10.0.2.2:8080/Auction/LoginAction.action", params);
     */   
    public static String Post(String url, List<NameValuePair> params) throws Exception {   
        String response = null;   
        HttpClient httpClient;   
        HttpPost httpPost;   
        try {
        	httpClient = new DefaultHttpClient();
        	httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            Log.i(TAG, "Data posted.");
            int statusCode = httpResponse.getStatusLine().getStatusCode();   
            if (statusCode == HttpStatus.SC_OK) {	//200请求成功
                response = EntityUtils.toString(httpResponse.getEntity());
                Log.i(TAG, "Get server response success.");
            } else {
                response = "Request error: " + statusCode;
                Log.i(TAG, "Post failed. " + response);
            }   
        } catch (Exception e) {   
            e.getMessage(); e.printStackTrace();
        }
        return response;   
    }
    /**   
     * @param url地址   
     * @return 返回网页内容   
     * @throws Exception
     * Ex.
     * String data;   
     * data = HttpUtils.Get("http://10.0.2.2:8080/Auction/LoginAction.action?username="+   
     *		username + "&password=" + password + "");
     */   
    public static String Get(String url) throws Exception {  
    	String response = null;
        HttpClient client = new DefaultHttpClient();   
        HttpGet get = new HttpGet(url);   
        HttpResponse httpResponse = client.execute(get);   
        Log.i(TAG, "Get request sended.");
        int statusCode = httpResponse.getStatusLine().getStatusCode();   
        if (statusCode == HttpStatus.SC_OK) {	//200请求成功
            response = EntityUtils.toString(httpResponse.getEntity());
            Log.i(TAG, "Get server response success.");
        } else {
            response = "Request error: " + statusCode;
            Log.i(TAG, "Get request failed. " + response);
        }
        return response;   
    }
	
	//PostFromWebByHttpURLConnection 发送POST请求参数
//    public static String PostFromWebByHttpURLConnection(String strUrl, Map<String, String> map) {
//        String result = "";
//        try {
//            URL url = new URL(strUrl);
//            conn = (HttpURLConnection) url.openConnection();
//            // 设置是否从httpUrlConnection读入，默认情况下是true;
//            conn.setDoInput(true);
//            // 设置是否向httpUrlConnection输出，post请求需把参数要放在  http正文内，因此需要设为true, 默认情况下是false;
//            conn.setDoOutput(true);
//            // 设定请求的方法为"POST"，默认是GET
//            conn.setRequestMethod("POST");
//            // 设置超时
//            conn.setConnectTimeout(CONNECT_TIME_OUT);
//            conn.setReadTimeout(READ_TIME_OUT);
//            // Post 请求不能使用缓存
//            conn.setUseCaches(false);
//            // 是否连接遵循重定向
//            conn.setInstanceFollowRedirects(true);
//            // 设定传送的内容类型是可序列化的java对象(如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
//            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            // 连接（从上述第2条中url.openConnection()至此的配置必须要在connect之前完成）
//            conn.connect();
//            //用此方法向服务器端发送数据  
//            String user = map.get("user");
//            DataOutputStream dop = new DataOutputStream(conn.getOutputStream());            
//            dop.writeBytes("user=" + URLEncoder.encode(user, "UTF-8"));
//            dop.flush();
//            dop.close();
//            Log.i(TAG, "Data posted.");
//            //接收数据          
//            BufferedReader buffer = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String strLine = null;
//            while ((strLine = buffer.readLine()) != null) {
//                result += strLine;
//            }
//            if(null != result) Log.i(TAG, "Result from server received.");
//            return result;
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            Log.i(TAG, "Exception.");
//            return null;
//        } finally {
//        	conn.disconnect();
//        }
//    }

}
