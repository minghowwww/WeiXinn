package Util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import menu.Button;
import menu.ClickButton;
import menu.Menu;
import menu.ViewButton;
import net.sf.json.JSON;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import pojo.AccessToken;

public class WeiXinUntil {
	
	private static final String AppID = "wx24b69a8f0713c81c";// wx24b69a8f0713c81c
	private static final String AppSecret = "9e15542a77b214e4bd1089f2dc10b96a";
	
	private static final String Access_Token_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	private static final String UPLOAD_URL = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
	private static final String CREATE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
	private static final String QUERY_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";
	private static final String DELETE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";
	private static final String SEND_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=ACCESS_TOKEN";
	/**
	 * 说明： get请求
	 * 
	 * @param url
	 * @return
	 */
	public static JSONObject doGetStr(String url){
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		JSONObject jsonObject = null;
		try {
			HttpResponse response = defaultHttpClient.execute(get);
			HttpEntity entity = response.getEntity();
			if(entity!=null){
				String result = EntityUtils.toString(entity,"UTF-8");
				jsonObject = JSONObject.fromObject(result);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	/**
	 * 说明： post请求
	 * 
	 * @param url
	 * @param outStr
	 * @return
	 */
	public static JSONObject doPostStr(String url,String outStr){
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		JSONObject jsonObject = null;
		try {
			post.setEntity(new StringEntity(outStr,"UTF-8"));
			HttpResponse response = defaultHttpClient.execute(post);
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity,"UTF-8");
			jsonObject = JSONObject.fromObject(result);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	/**
	 * 说明： 获取access_token
	 * 
	 * @return
	 */
	public static AccessToken getAccessToken(){
		AccessToken at = new AccessToken();
		String url = Access_Token_URL.replace("APPID", AppID).replace("APPSECRET", AppSecret);
		JSONObject jo = doGetStr(url);
		if(jo!=null){
			at.setAccess_token(jo.getString("access_token"));
			at.setExpires_in(jo.getInt("expires_in"));
		}
		return at;
	}
	
	public static String upload(String filePath, String accessToken, String type) throws Exception{
		
		File file = new File(filePath);
		if (!file.exists()||!file.isFile()) {
			throw new IOException("文件不存在");
		}
		
		String url = UPLOAD_URL.replace("ACCESS_TOKEN", accessToken).replace("TYPE", type);
		URL urlObj = new URL(url);
		HttpURLConnection con = (HttpURLConnection)urlObj.openConnection();
		
		con.setRequestMethod("POST");
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false);
		
		//设置请求头信息
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Charset", "UTF-8");
		
		//设置边界
		String BOUNDARY = "---------" + System.currentTimeMillis();
		con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
		
		StringBuffer sb = new StringBuffer();
		sb.append("---");
		sb.append(BOUNDARY);
		sb.append("\r\n");
		sb.append("Content-Disposition:form-data;name=\"file\";filename=\"" + file.getName() + "\"\r\n");
		sb.append("Content-Type:application/octet-stream\r\n\r\n");
		
		byte[] head = sb.toString().getBytes("utf-8");
		
		
		//获得输出流
		OutputStream out = new DataOutputStream(con.getOutputStream());
		
		//输出表头
		out.write(head);
		
		//文件正文部分
		//把文件文件以流文件的形式推入到URL中
		
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		int bytes = 0;
		byte[] bufferOut = new byte[1024];
		while((bytes = in.read(bufferOut)) !=-1){
			
			out.write(bufferOut,0,bytes);
		}
		in.close();
		
		//结尾部分
		byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");//定义最后数据分割线
		
		out.write(foot);
		
		out.flush();
		out.close();
		
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		String result = null;
		
		try {
			//定义BufferedReader 输入流来读取URL的响应
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = null;
			while((line = reader.readLine()) != null){
				buffer.append(line);
			}
			if (result == null) {
				result = buffer.toString();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally{
			if (reader != null) {
				reader.close();
			}
		}
		

		JSONObject jsonobj = JSONObject.fromObject(result);
		System.out.println(jsonobj);
		String mediaId = jsonobj.getString("media_id");
		return mediaId;
	}
	
	/**
	 * 说明： 自定义菜单的实现
	 * 
	 * @return
	 */
	public static Menu initMenu(){
		Menu menu = new Menu();
		
		ClickButton button11 = new ClickButton();
		button11.setKey("11");
		button11.setName("企业概况");
		button11.setType("click");
		
		ViewButton button12 = new ViewButton();
		button12.setName("公司新闻");
		button12.setType("view");
		button12.setUrl("http://www.baidu.com");
		
		ViewButton button13 = new ViewButton();
		button13.setName("联系我们");
		button13.setType("view");
		button13.setUrl("http://www.baidu.com");
		
		Button button1 = new Button();
		button1.setName("关于我们");
		button1.setSub_button(new Button[]{button11,button12,button13});
		
		ViewButton button21 = new ViewButton();
		button21.setName("职位招聘");
		button21.setType("view");
		button21.setUrl("http://www.baidu.com");
		
		Button button2 = new Button();
		button2.setName("加入我们");
		button2.setSub_button(new Button[]{button21});
		
		ClickButton button31 = new ClickButton();
		button31.setKey("31");
		button31.setName("考勤");
		button31.setType("click");
		
		ClickButton button32 = new ClickButton();
		button32.setKey("32");
		button32.setName("公司福利");
		button32.setType("click");
		
		ViewButton button33 = new ViewButton();
		button33.setUrl("http://qiye.163.com/login/");
		button33.setName("邮箱登录");
		button33.setType("view");
		
		ViewButton button34 = new ViewButton();
		button34.setUrl("http://chijiaxin.com.ngrok.cc/SystemStudent/view/login.jsp");
		button34.setName("考核系统登录");
		button34.setType("view");
		
		ViewButton button35 = new ViewButton();
		button35.setUrl("http://yaxun.11.userhostting.com/common/login.aspx");
		button35.setName("公司官网管理系统");
		button35.setType("view");
		
		Button button3 = new Button();
		button3.setName("员工通道");
		button3.setSub_button(new Button[]{button31,button32,button33,button34,button35});
		menu.setButton(new Button[]{button1,button2,button3});
		return menu;
	}
	
	public static int createMenu(String token,String menu){
		int result = 0;
		String url = CREATE_MENU_URL.replace("ACCESS_TOKEN", token);
		JSONObject jo = doPostStr(url, menu);
		if(jo!=null){
			result = jo.getInt("errcode");
		}
		return result;
	}
	
	public static JSONObject queryMenu(String token){
		
		String url = QUERY_MENU_URL.replace("ACCESS_TOKEN", token);
		JSONObject jsonObject = doGetStr(url);
		return jsonObject;
		
	}
	
	/**
	 * 说明： 删除全部菜单
	 * 
	 * @param token
	 * @return
	 */
	public static int deleteMenu(String token){
		
		String url = DELETE_MENU_URL.replace("ACCESS_TOKEN", token);
		JSONObject jsonObject = doGetStr(url);
		int result = 0;
		if (jsonObject != null) {
			result = jsonObject.getInt("errcode");
		}
		return result;
	}
	
	public static void main(String[] args) {
		AccessToken token = WeiXinUntil.getAccessToken();
		String menu = JSONObject.fromObject(WeiXinUntil.initMenu()).toString();
		int result = WeiXinUntil.createMenu(token.getAccess_token(), menu);
		if(result==0){
			System.out.println("success");
		}else{
			System.out.println("错误码："+result);
		}
		
//		JSONObject jsob = WeiXinUntil.queryMenu(token.getAccess_token());
//		System.out.println(jsob);
		
		//System.out.println(WeiXinUntil.deleteMenu(token.getAccess_token()));
		
	}
	
 }
