package com.pianist.battlelasers.tasks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.pianist.battlelasers.activities.BattleLaserActivity;

import android.os.AsyncTask;
import android.util.Log;

public class SendRegistrationIdTask extends AsyncTask<Void, Void, String>
{
	private BattleLaserActivity mActivity;
	private String mRegId;
	private int mRating;
	private String mUserName;
	
	public SendRegistrationIdTask(BattleLaserActivity activity, String regId, int rating, String userName) {
		mRegId = regId;
		mRating = rating;
		mActivity = activity;
		mUserName = userName;
	}
	
	@Override
	protected String doInBackground(Void... args)
	{	
		HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = "none";
        String uri = BattleLaserActivity.BASE_URL + "/player";
        try {
        	HttpPut method = new HttpPut(uri);
        	
        	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("registrationId", mRegId));
            nameValuePairs.add(new BasicNameValuePair("rating", "" + mRating));
            nameValuePairs.add(new BasicNameValuePair("userName", mUserName));
            method.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        	
            response = httpclient.execute(method);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        	mActivity.checkNetworkConnection();
        }
        return responseString;
	}
	
	@Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        int id = getId(result);
        if (id >= 0) {
        	mActivity.registeredUser(id);
        }
        Log.d("RESPONSE", result);
    }
	
	private static int getId(String s) {
		int id = -1;
	    try { 
	        id = Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return -1; 
	    }
	    // only got here if we didn't return false
	    return id;
	}
}

