
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;


public class HttpUtils {
	
	private final int URL_ERROR=-100;
	
	private int STATE=0;
	private boolean REDIRECT=false;
	private int TIMEOUT=5000;	//ms
	private String RecvEncode="utf-8";
	private String SendEncode="utf-8";
	
	private String userAgent="Mozilla/5.0 (Linux; U; Android 5.1; zh-cn;)";
	private boolean requestEncode=false;
	private boolean responseDecode=false;
	
	private StringBuilder result=null;
	private StringBuilder head=null;
	private int count=0;
	
	/**
	 * 默认构造方法
	 * */
	public HttpUtils(){

	}
	
	/**
	 * 带编码参数构造方法 
	 * @param boolean encode, boolean decode
	 * */
	public HttpUtils(boolean encode, boolean decode){
		this.requestEncode=encode;
		this.responseDecode=decode;
	}
	
	/**
	 * 初始化方法 非必须
	 * 传入参数(超时时间,允许重定向,用户代理声明,编码方式,)
	 * @param
	 * */
	public void init(int timeout,boolean redirect,String userAgent,String encode) {
		
		this.REDIRECT=redirect;
		
		if(timeout > 1000){
			this.TIMEOUT=timeout;
		}
		
		if(strOk(userAgent)){
			this.userAgent=userAgent;
		}
		
		if(strOk(encode)){
			this.RecvEncode=encode;
		}
	}
	
	/**
	 * 设置请求信息是否采用url编码
	 * @param
	 * */
	public void setRequestEncode(boolean encode){
		this.requestEncode=encode;
	}
	
	/**
	 * 设置返回结果是否采用url解码
	 * @param
	 * */
	public void setResponseDecode(boolean decode){
		this.responseDecode=decode;
	}
	
	/**
	 * 返回文本结果
	 * @return string result
	 * */
	public String getResult() {
		return result.toString();
	}
	
	/**
	 * 返回信息头
	 * @return string head
	 * */
	public String getHead() {
		return head.toString();
	}
	
	/**
	 * 返回http状态码
	 * @return int http stateCode
	 * */
	public int getStateCode() {
		return this.STATE;
	}
	
	/**
	 * 返回信息头行数 ,\n分断
	 * @return int lineCount
	 * */
	public int getHeadCount() {
		return this.count;
	}
	
	/**
	 * 提交post 请求
	 * @param 
	 * @return int http stateCode
	 * */
	public int doPost(String url,String uploads,String cookies) {
		
		BufferedReader buffere = null;
		String myUploads=null;
		
		if(!strOk(url)) {
			return this.URL_ERROR;
		}
		
		initStrBuilder();
		
		if(strOk(uploads)) {
			if(requestEncode) {
				try {
					myUploads = URLEncoder.encode(uploads, "utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}else {
				myUploads=uploads;
			}
		}else {
			myUploads="";
		}
		
		try {
			URL myUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) myUrl.openConnection();
            HttpURLConnection.setFollowRedirects(this.REDIRECT);
            
            conn.setRequestProperty("accept", "text/html, application/xhtml+xml, image/jxr, */*");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			if(strOk(cookies)){
				conn.setRequestProperty("Cookie", cookies);
			}
            conn.setRequestProperty("user-agent",this.userAgent);
            conn.setReadTimeout(TIMEOUT);
            conn.setConnectTimeout(TIMEOUT);
			conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            
			OutputStreamWriter opsw = new OutputStreamWriter(conn.getOutputStream(), this.SendEncode);
            BufferedWriter bw = new BufferedWriter(opsw);
            bw.write(myUploads);
            bw.flush();
            
            this.STATE=conn.getResponseCode();
            
            for(count=0;conn.getHeaderField(count)!=null;count++) {
				head.append(conn.getHeaderField(count)+"\n");
			}
            
			buffere = new BufferedReader(new InputStreamReader(conn.getInputStream(),this.RecvEncode));
            
			String line;
            while ((line = buffere.readLine()) != null) {
            	result.append(line+"\n");
            	line=null;
            }
            
            buffere.close();
            bw.close();
            opsw.close();
            conn.disconnect();
            
		} catch (MalformedURLException e) {
			this.STATE=502;
			e.printStackTrace();
			return STATE;
		} catch (IOException e) {
			this.STATE=404;
			e.printStackTrace();
			return STATE;
		}
		finally{
            try{
                if(buffere!=null){
                	buffere.close();
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
		
		if(responseDecode){
			try {
				String tempResult = URLDecoder.decode(result.toString(), "utf-8");
				result.delete(0, result.length());
				result.append(tempResult);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		return STATE;
	}
	
	/**
	 * 提交Get请求
	 * @param
	 * @return int http stateCode
	 * */
	public int doGet(String url,String cookies) {
		
		BufferedReader buffere = null;
		String myUrl=null;
		
		if(!strOk(url)) {
			return this.URL_ERROR;
		}
		
		initStrBuilder();
		
		if(requestEncode){
			//根据是否附带参数进行url编码
			if(url.contains("?")){
				String tempUrl;
				int start=url.indexOf("?")+1;
				myUrl=url.substring(start,url.length());
				tempUrl=url.substring(0,start);
				try {
					myUrl = URLEncoder.encode(myUrl, "utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				myUrl=tempUrl+myUrl;
			}else{
				myUrl=url;
			}
		}else{
			myUrl=url;
		}
		
		try {
			URL getUrl = new URL(myUrl);
			HttpURLConnection conn = (HttpURLConnection) getUrl.openConnection();
            HttpURLConnection.setFollowRedirects(this.REDIRECT);
			
            conn.setRequestProperty("accept", "application/x-www-form-urlencoded");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			if(strOk(cookies)){
				conn.setRequestProperty("Cookie", cookies);
			}
            conn.setRequestProperty("user-agent",this.userAgent);
			conn.setReadTimeout(TIMEOUT);
			conn.setConnectTimeout(TIMEOUT);
            conn.setDoOutput(false);
            conn.setDoInput(true);
            conn.connect();
            
			this.STATE=conn.getResponseCode();
			
			for(count=0;conn.getHeaderField(count)!=null;count++) {
				head.append(conn.getHeaderField(count)+"\n");
			}
			
			buffere = new BufferedReader(new InputStreamReader(conn.getInputStream(),this.RecvEncode));
            String line;
            while ((line = buffere.readLine()) != null) {
            	result.append(line+"\n");
            }
            
            buffere.close();
            conn.disconnect();
            
		} catch (MalformedURLException e) {
			this.STATE=502;
			e.printStackTrace();
			return STATE;
		} catch (IOException e) {
			this.STATE=404;
			e.printStackTrace();
			return STATE;
		}
		finally{
            try{
                if(buffere!=null){
                	buffere.close(); 
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
		
		if(responseDecode){
			try {
				String temp=URLDecoder.decode(result.toString(), "utf-8");
				result.delete(0, result.length());
				result.append(temp);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		return STATE;
		
	}
	
	/**
	 * 初始化stringbuilder
	 * */
	private void initStrBuilder() {
		if(result==null) {
			result = new StringBuilder(10240);
		}else {
			result.delete(0, result.length());
		}
		 
		if(head==null) {
			head = new StringBuilder(2048);
		}else {
			head.delete(0, head.length());
		}
	}
	
	/**
	 * 对string进行判空
	 * */
	private boolean strOk(String args) {
		if(args!=null && !args.equals("")) {
			return true;
		}else {
			return false;
		}
	}
	
}
