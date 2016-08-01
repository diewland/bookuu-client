package com.diewland.comic.hunter;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.diewland.comic.hunter.bean.Item;
import com.diewland.comic.hunter.bean.Storage;
import com.diewland.comic.hunter.task.AddTask;
import com.diewland.comic.hunter.util.ListUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends ActionBarActivity {

	String TAG = "DIEWLAND";

    static ArrayList<Item> core_data = new ArrayList<Item>();
    static ArrayList<String> list_data  = new ArrayList<String>();
    static ArrayAdapter<String> adapter;
    static ListUtil listUtil;
	static int current_index;
	static String new_code;
	static String new_format;

	Spinner spn_title;
	List<String> spn_data;
    ArrayAdapter<String> spn_adapter;

	Storage storage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		// initialize storage
		storage = new Storage(getPreferences(MODE_PRIVATE));

		// load core_data
		try {
			core_data = storage.load();
		}
		catch (Exception e){
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public void onActivityResult(int request, int result, Intent i) {
        IntentResult scan=IntentIntegrator.parseActivityResult(request, result, i);
        if (scan!=null) {
            new_format  = scan.getFormatName();
            new_code = scan.getContents();

            if(new_format != null && new_code != null){
            	// find code from core data
            	int existing_index = -1;
            	for(int index=0; index<core_data.size(); index++){
            		Item item = core_data.get(index);
					if(new_code.equals(item.getCode())){
						existing_index = index;
						break;
					}
				}
            	if(existing_index == -1){ // new item
            		final Dialog add_dialog = new Dialog(this);
            		add_dialog.setTitle("New Item !");
            		add_dialog.setContentView(R.layout.add_dialog);
            		Button btn_add = (Button) add_dialog.findViewById(R.id.btn_add);
            		Button btn_cancel = (Button) add_dialog.findViewById(R.id.btn_cancel);

                    // (async) load server titles to spn_data
            		spn_data = new ArrayList<String>();
                    new ListTask().execute(new_code); // update spinner at callback

            		// bind spinner w/ spn_data
            		spn_title = (Spinner)add_dialog.findViewById(R.id.server_title);
                    spn_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spn_data);
                    spn_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spn_title.setAdapter(spn_adapter);

            		btn_add.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							String title = null;
            				String client_title = ((TextView)add_dialog.findViewById(R.id.client_title)).getText().toString();
            				Object spinnerItem = spn_title.getSelectedItem();
            				String server_title = spinnerItem == null ? "" : spinnerItem.toString();

            				// client_title, server_title by sequence
            				if(!client_title.equals("")){
            					title = client_title;
            					new AddTask().execute(new_code, title); // send title to server
            				}
            				else if(!server_title.equals("")){
            					title = server_title;
            				}
            				else {
            					// do nothing
            				}
            				if(title != null){
                                // add item to listview
                                core_data.add(new Item(new_code, new_format, title));
                                listUtil.reload(core_data);
            				}
							add_dialog.dismiss();
						}
					});
            		btn_cancel.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							add_dialog.dismiss();
						}
					});
            		add_dialog.show();
            	}
            	else { // existing item
            		Item item = core_data.get(existing_index);
            		String msg  = "Title: " + item.getTitle()
            					+ "\nISBN: " + item.getCode()
            					+ "\nFormat: " + item.getFormat()
            					+ "\nCaptured: " + item.getStringTime()
            					;
            		// alert info dialog
                    new AlertDialog.Builder(this)
                    .setTitle("Item found")
                    .setMessage(msg)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) { 
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                   	.show();
                }

            }
        }
    }

	@Override
	protected void onPause() {
		super.onPause();
		try {
            storage.save(core_data);
			// Toast.makeText(this, "Items are saved", Toast.LENGTH_LONG).show();
		}
		catch(Exception e){
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		ListView list;
		ImageButton btn_scan;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);

			// ########## INIT STAGE ##########

			// create view
			list = (ListView)rootView.findViewById(R.id.list);
			btn_scan = (ImageButton)rootView.findViewById(R.id.btn_scan);

			// create data adapter & set adapter to list
			// ( bind data w/ list_data variable )
			adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list_data);
			list.setAdapter( adapter );

			// initialize list utility
			listUtil = new ListUtil(list, adapter, list_data);

			// load data
			listUtil.reload(core_data);

			// ########### BIND VIEW EVENT ###########

			// hold key on list to delete
			list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				 public boolean onItemLongClick(AdapterView<?> arg0, View v, int index, long id) {
                     String text = list.getItemAtPosition(index).toString();
                     current_index = index;

                     // alert delete dialog
                     new AlertDialog.Builder(getActivity())
                     .setTitle("Confirm Delete ?")
                     // .setMessage("Do you want to delete " + text + " ?")
                     .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                         public void onClick(DialogInterface dialog, int which) { 
                             listUtil.remove(current_index);
                             core_data.remove(core_data.size() - current_index - 1); // show in revert mode
                         }
                      })
                     .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                         public void onClick(DialogInterface dialog, int which) { 
                             // do nothing
                         }
                      })
                     .setIcon(android.R.drawable.ic_dialog_alert)
                     .show();

	                 return true;
	             }
			});
			// scan button
			btn_scan.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// call bar code scanner
					IntentIntegrator integrator = new IntentIntegrator(getActivity());
					// integrator.addExtra(key, value) --- how to set vertical scanner
					integrator.initiateScan();
                }
			});

			return rootView;
		}
	}

	class ListTask extends AsyncTask<String, String, String>{

		String url = "http://www.diewland.com/bookuu/index.php?isbn=";

	    @Override
	    protected String doInBackground(String... codes) {
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpResponse response;
	        String responseString = null;
	        try {
	            response = httpclient.execute(new HttpGet(url + codes[0]));
	            responseString = conv_is2text(response.getEntity().getContent());
	        }
	        catch (Exception e) {
	        	Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_LONG).show();
	        }
	        return responseString;
	    }

	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        
	        String[] titles = conv_str2arr(result);
	        for (String title : titles) {
	        	spn_data.add(title);
	        }

            spn_adapter.notifyDataSetChanged(); // refresh spinner after reload spn_data
	    }
	}

	private String conv_is2text(java.io.InputStream is){
        List<String> result = new ArrayList<String>();
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
	}

    private String[] conv_str2arr(String text) {
        return text.replace("[", "")
        			.replace("]", "")
        			.replaceAll("\"", "")
        			.split(",");
    }

}