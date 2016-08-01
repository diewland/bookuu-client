package com.diewland.comic.hunter.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

public class AddTask extends AsyncTask<String, Integer, Double>{

	String url = "http://www.diewland.com/bookuu/index.php";
	 
	@Override
	protected Double doInBackground(String... params) {
		postData(params[0], params[1]); // isbn, title
		return null;
	}

	protected void onPostExecute(Double result){
		// pb.setVisibility(View.GONE);
		// Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
	}
	protected void onProgressUpdate(Integer... progress){
		// pb.setProgress(progress[0]);
	}

	public void postData(String isbn, String title) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("isbn", isbn));
			nameValuePairs.add(new BasicNameValuePair("title", title));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}
}