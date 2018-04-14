package com.zz.safeCall;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class Post {
	public static String  getJSONFromUrl (String url,List <NameValuePair> params,boolean issent) throws JSONException, IOException {
		//进行 HTTP request Post 操作
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
		//进行 Response 处理
		HttpResponse httpResponse1 = new DefaultHttpClient().execute(httpPost);
		String retSrc = EntityUtils.toString(httpResponse1.getEntity(),"UTF-8");
		System.out.println("返回的字符串为"+retSrc);
		if(retSrc!=null&&!retSrc.equals("")){
		/*while(retSrc.charAt(0)!='{'&&retSrc.length()>0){
			retSrc=retSrc.substring(1);
		}		*/
		//转换为 JSON 对象
		/*JSONObject jObj=null;
		try {
		jObj = new JSONObject(retSrc.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("在JSONParser中生成json失败了~");
			e.printStackTrace();
			return null;
		}
		int  success=jObj.getInt("success");//获取是否返回成功
		System.out.println("在函数中返回为"+success);
		return jObj;*/
		return retSrc.toString();
		}
return null;
		
        }
}
