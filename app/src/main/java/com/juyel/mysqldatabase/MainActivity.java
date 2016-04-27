package com.juyel.mysqldatabase;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

public class MainActivity extends Activity implements OnClickListener {

	private EditText etTitle, etAuthor, etIsbn, etCategory, etPrice;
	Button btnAdd;
	Book book;
	ProgressDialog pd;
	public static final int SUCCESS = 1, FAILURE = 0;
	public static final String ADD_URL = "http://192.168.56.1/library_demo/addBook.php";
	public String serverResponse;
	private Uri outputFileUri;

	private static final int CAPTURE_PHOTO = 0;

	private ImageView imgPreview;

	ListView listView;

	ArrayList<Book> bookArrayList;
	ArrayAdapter<Book> bookArrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			//System.out.println("*** My thread is now configured to allow connection");
		}

		btnAdd = (Button) findViewById(R.id.btnAdd);
		//btnSearch = (Button) findViewById(R.id.btnSearch);
		//btnCapture = (Button) findViewById(R.id.btnCapture);

		etTitle = (EditText) findViewById(R.id.etTitle);
		etAuthor = (EditText) findViewById(R.id.etAuthorName);
		etIsbn = (EditText) findViewById(R.id.etIsbn);
		etCategory = (EditText) findViewById(R.id.etCategory);
		etPrice = (EditText) findViewById(R.id.etPrice);
		listView = (ListView)findViewById(R.id.listView);
		//imgPreview = (ImageView) findViewById(R.id.imgPreview);

		btnAdd.setOnClickListener(this);
		//btnSearch.setOnClickListener(this);
		//btnCapture.setOnClickListener(this);


	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.btnAdd:

			// Validation

			String title = etTitle.getText().toString();
			String author = etAuthor.getText().toString();
			String isbn = etIsbn.getText().toString();
			String category = etCategory.getText().toString();
			String price = etPrice.getText().toString();

			book = new Book(title, author, isbn, category,
					Double.parseDouble(price));
			bookArrayList.add(book);

			// network state

			if (isNetworkAvailable()) {

				// thread start
				PostThread thread = new PostThread();
				thread.start();

			} else {

				// Send error message
				showAlert("Error", "Internet not available");

			}

			break;

		default:
			break;
		}
	}

	public boolean isNetworkAvailable() {

		// network

		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null) {

			if (info.isAvailable() && info.isConnected()) {

				pd = ProgressDialog.show(this, "",
						"Posting the book information...");
				return true;

			} else {

				return false;

			}

		} else {

			return false;
		}

	}

	class PostThread extends Thread {

		public void run() {

			try {

				DefaultHttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(ADD_URL);
				HttpGet httpGet = new HttpGet(ADD_URL);

				List<NameValuePair> prams = new ArrayList<NameValuePair>();
				prams.add(new BasicNameValuePair("title", book.getTitle()));
				prams.add(new BasicNameValuePair("author", book.getAuthor()));
				prams.add(new BasicNameValuePair("isbn", book.getIsbn()));
				prams.add(new BasicNameValuePair("category", book.getCategory()));
				prams.add(new BasicNameValuePair("price", book.getPrice() + ""));
				//prams.add(new BasicNameValuePair("image", imageStr));

				post.setEntity(new UrlEncodedFormEntity(prams));

				HttpResponse response = client.execute(post);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					String jsonStr = EntityUtils.toString(entity);
					Log.d("JSON Response", jsonStr);

					JSONObject obj = new JSONObject(jsonStr);
					String success = obj.getString("success");
					serverResponse = obj.getString("message");
					if (success.equals("1")) {
						handler.sendEmptyMessage(SUCCESS);
					} else {
						handler.sendEmptyMessage(FAILURE);
					}

				} else {
					serverResponse = "Server Error: "
							+ response.getStatusLine().getStatusCode();
					handler.sendEmptyMessage(FAILURE);
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				serverResponse = e.getMessage();
				handler.sendEmptyMessage(FAILURE);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				serverResponse = e.getMessage();
				handler.sendEmptyMessage(FAILURE);
			} catch (IOException e) {
				e.printStackTrace();
				serverResponse = e.getMessage();
				handler.sendEmptyMessage(FAILURE);
			} catch (JSONException e) {
				e.printStackTrace();
				serverResponse = e.getMessage();
				handler.sendEmptyMessage(FAILURE);
			}

		}
	}

	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			pd.dismiss();

			if (msg.what == SUCCESS) {

				showAlert("Success", serverResponse);

			} else {

				showAlert("Error", serverResponse);

			}

		}

	};

	public void showAlert(String title, String message) {

		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(title);
		alert.setMessage(message);
		alert.show();

	}

}
