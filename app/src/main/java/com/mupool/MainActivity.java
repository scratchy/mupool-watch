package com.mupool;

import java.io.IOException;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.ads.AdSize;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class MainActivity extends FragmentActivity {
   
    static SharedPreferences settings = null;
	static JSONObject WorkerData = null;
	static Integer LAST_CHECKED = null;
	
	SectionsPagerAdapter mSectionsPagerAdapter;	
	ViewPager mViewPager;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());	
		settings = getSharedPreferences("Einstellungen", 0);

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		updateWorkerStats();
	}

	private void updateWorkerStats()
	{
		final String apiKey = settings.getString("api-hash", null);
		final String selectedCoin = settings.getString("selectedCoin", "BTC");
		if(apiKey != null)
		{
			GetWorkerInfos wt = new GetWorkerInfos();		
			wt.execute("https://mupool.com/index.php?page=api&action=getuserworkers&coin="+selectedCoin+"&api_key="+apiKey);
		}
	}
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		  IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		  if (scanResult != null && scanResult.getContents() != null && scanResult.getContents().contains("https://mupool.com")) {
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
	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
		public SectionsPagerAdapter(FragmentManager fm) {			
			super(fm);
		}

	    
		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return POSITION_NONE;
		}
		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				final String selectedCoin = settings.getString("selectedCoin", "BTC");
				return getString(R.string.title_section2).toUpperCase(l) + " (" + selectedCoin + ")";
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

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		    final SharedPreferences.Editor editor = settings.edit();			
			int val = getArguments().getInt(ARG_SECTION_NUMBER);
			switch(val)
			{
				case 1: //Settings Tab
						{
							final View rootView = inflater.inflate(R.layout.settings,container, false);
		
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

							final String apiKey = settings.getString("api-hash", null);
							if(apiKey != null)
							{
								ImageView iv = (ImageView) rootView.findViewById(R.id.imageView1);
								iv.setImageResource(R.drawable.ok);
								
								String key = apiKey;
								if(apiKey.length() > apiKey.length()-15)
								{
									key = "..." + apiKey.substring(apiKey.length()-15,apiKey.length());
								}
								t.setText(key);
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
                                            case R.id.radioVTC:
                                                editor.putString("selectedCoin", "VTC");
                                                break;
                                            case R.id.radioDRK:
                                                editor.putString("selectedCoin", "DRK");
                                                break;
                                            case R.id.radioTIT:
                                                editor.putString("selectedCoin", "TIT");
                                                break;
							                default:
							      			  	editor.putString("selectedCoin", "BTC");
							      			  	break;
						                }
			
					      			  	editor.apply();
					      			  	editor.commit();				      			  
					      			  	
					            }					            
					        });	
							return rootView;
						}
				case 2:
						{
							final View rootView = inflater.inflate(R.layout.poolstats,container, false);

                            AdView adView = (AdView)rootView.findViewById(R.id.adView);
                            AdRequest adRequest = new AdRequest.Builder().build();
                            adView.loadAd(adRequest);

							if(WorkerData != null)
							{
								TableLayout table = (TableLayout)rootView.findViewById(R.id.tableSample);
								table.removeViews(1, table.getChildCount()-1);

								try {
									JSONArray workers = WorkerData.getJSONObject("getuserworkers").getJSONArray("data");
									for(int i=0; i < workers.length();i++)
									{
										JSONObject worker = (JSONObject) workers.get(i);
	
										String username = (String) worker.get("username");
										Integer hashrate = (Integer) worker.get("hashrate");
										String online = hashrate == 0 ? "Offline" : "Online";
									    TableRow row = (TableRow)LayoutInflater.from((FragmentActivity)getActivity()).inflate(R.layout.workerrow, null);
									    ((TextView)row.findViewById(R.id.textItem1)).setText(username);
									    ((TextView)row.findViewById(R.id.textItem2)).setText(hashrate.toString());
									    ((TextView)row.findViewById(R.id.textItem3)).setText(online);				    
									    if(hashrate == 0)
									    	((TextView)row.findViewById(R.id.textItem3)).setTextColor(Color.RED);
									    else
									    	((TextView)row.findViewById(R.id.textItem3)).setTextColor(Color.GREEN);
									    if(i%2 == 0)
									    {
									    	row.setBackgroundColor(Color.LTGRAY);
									    }
									    else
									    	row.setBackgroundColor(Color.WHITE);
									    table.addView(row);
									    table.requestLayout();
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							return rootView;
						}
				default:
						{
							final View rootView = inflater.inflate(R.layout.fragment_main_dummy,container, false);
							TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
							dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
							return rootView;
						}
			}			
		}
	}
	class GetWorkerInfos extends AsyncTask<String, Void, JSONObject> {
		@Override
	    protected JSONObject doInBackground(String... urls) {
	          
	        // params comes from the execute() call: params[0] is the url.
	        try {
				return JSONReader.readJsonFromUrl(urls[0]);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
	        return null;
	    }
	    // onPostExecute displays the results of the AsyncTask.
	    @Override
	    protected void onPostExecute(JSONObject result) {
	    	if(result != null)
	    	{
	    		WorkerData = result;
	    		mSectionsPagerAdapter.notifyDataSetChanged();
	    	}
	   }
	}
	
	public void refreshWorker(View arg0) {
		TableLayout table = (TableLayout)findViewById(R.id.tableSample);
		table.removeViews(1, table.getChildCount()-1);
		ProgressBar pg = new ProgressBar(this);
		table.addView(pg);
		updateWorkerStats();
    }

}
