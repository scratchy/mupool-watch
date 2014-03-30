package com.mupool;

import java.io.IOException;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
    static SharedPreferences settings = null;
    
    static GetWorkerInfos wt = null; 
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		settings = getSharedPreferences("Einstellungen", 0);

		wt = new GetWorkerInfos();		
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		  IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		  if (scanResult != null && scanResult.getContents().contains("https://mupool.com")) {
			  String[] data = scanResult.getContents().split("\\|");
		      SharedPreferences.Editor editor = settings.edit();
			  editor.putString("api-hash", data[2]);
			  editor.apply();
			  editor.commit();			  			  
		  }
		  mSectionsPagerAdapter.notifyDataSetChanged();
		}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		public void onResume() {
			super.onResume();
		};
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
			String apiKey = settings.getString("api-hash", null);
		    final SharedPreferences.Editor editor = settings.edit();
			String selectedCoin = settings.getString("selectedCoin", "BTC");
			
			View rootView = null;
			int val = getArguments().getInt(ARG_SECTION_NUMBER);
			switch(val)
			{
				case 1: //Settings Tab
				{
					rootView = inflater.inflate(R.layout.settings,container, false);

					Button b1 = (Button) rootView.findViewById(R.id.button1);
					b1.setOnClickListener(new View.OnClickListener() {
					    @Override
					    public void onClick(View v) {					    
					    	IntentIntegrator integrator = new IntentIntegrator(getActivity());
					    	integrator.initiateScan();
					    	((View) v.getParent()).invalidate();
					    }
					});

					TextView t = (TextView) rootView.findViewById(R.id.textView1);
					
					if(apiKey != null)
					{
						ImageView iv = (ImageView) rootView.findViewById(R.id.imageView1);
						iv.setImageResource(R.drawable.ok);
						if(apiKey.length() > apiKey.length()-15)
						{
							apiKey = "..." + apiKey.substring(apiKey.length()-15,apiKey.length());
						}
						t.setText(apiKey);
					}
					else
					{
						t.setText("Not set");
					}
					

				    // Get Selected Radio Button and display output
				    RadioGroup rgOpinion  = (RadioGroup) rootView.findViewById(R.id.selectedCoin);
				    rgOpinion.setOnCheckedChangeListener(new OnCheckedChangeListener()
			        {
			            @Override
			            public void onCheckedChanged(RadioGroup group, int checkedId)
			            {
			                switch(checkedId)
			                {
				                case R.id.radioBTC:
				      			  	editor.putString("selectedCoin", "BTC");
				                    break;
				                case R.id.radioLTC:
				      			  	editor.putString("selectedCoin", "LTC");
				                    break;
				                case R.id.radioFTC:
				      			  	editor.putString("selectedCoin", "FTC");
				                    break;
				                case R.id.radioDOGE:
				      			  	editor.putString("selectedCoin", "DOGE");
				                    break;
				                case R.id.radioPPC:
				      			  	editor.putString("selectedCoin", "PPC");
				                    break;
				                default:
				      			  	editor.putString("selectedCoin", "BTC");
			                }

		      			  	editor.apply();
		      			  	editor.commit();
			            }
			        });
				    
				    
				       
				}
					break;
				case 2:
				{
					rootView = inflater.inflate(R.layout.poolstats,container, false);
					
				    wt.setRootView(rootView);
					wt.execute("https://mupool.com/index.php?page=api&action=getuserworkers&coin="+selectedCoin+"&api_key="+apiKey);
					
				}
					break;
				default:
				{
					rootView = inflater.inflate(R.layout.fragment_main_dummy,container, false);
					TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
					dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
				}
					break;
			}
			
			return rootView;
		}
	}
	class GetWorkerInfos extends AsyncTask<String, Void, JSONObject> {
		View rootView = null;
	    public void setRootView(View rootView2) {
			this.rootView = rootView2;
			
		}
		@Override
	    protected JSONObject doInBackground(String... urls) {
	          
	        // params comes from the execute() call: params[0] is the url.
	        try {
				return JSONReader.readJsonFromUrl(urls[0]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return null;
	    }
	    // onPostExecute displays the results of the AsyncTask.
	    @Override
	    protected void onPostExecute(JSONObject result) {
	    	try {
				if(result == null)
					return;
				TableLayout table = (TableLayout)rootView.findViewById(R.id.tableSample);
				table.removeViews(1, table.getChildCount()-1);

				JSONArray workers = result.getJSONObject("getuserworkers").getJSONArray("data");
				for(int i=0; i < workers.length();i++)
				{
					JSONObject worker = (JSONObject) workers.get(i);

					String username = (String) worker.get("username");
					Integer hashrate = (Integer) worker.get("hashrate");
					String online = hashrate == 0 ? "Offline" : "Online";
				    TableRow row = (TableRow)LayoutInflater.from(getApplication().getBaseContext()).inflate(R.layout.workerrow, null);
				    ((TextView)row.findViewById(R.id.textItem1)).setText(username);
				    ((TextView)row.findViewById(R.id.textItem2)).setText(hashrate.toString());
				    ((TextView)row.findViewById(R.id.textItem3)).setText(online);
				    table.addView(row);
				    table.requestLayout();
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   }
	}

}
