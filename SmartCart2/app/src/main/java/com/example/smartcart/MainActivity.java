package com.example.smartcart;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.smartcart.fragments.CartsFragment;
import com.example.smartcart.fragments.NotificationFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//Array of options -- > ArrayAdapter --> ListView

//List view : {views : da_items.xml}

public class MainActivity extends FragmentActivity {



	public static WifiManager wifiManager;
	public WifiBroadcastReceiver wifiBroadcastReceiver;
	public IntentFilter wifiIntentFilter;
	public Date lastWifiScanTime;
	private final static String LOCATION_KEY                         = "location-key";
	private final static String LAST_LOCATION_UPDATE_TIME_STRING_KEY = "last-location-update-time-string-key";
	private final static String LAST_WIFI_SCAN_TIME_STRING_KEY       = "last-wifi-scan-time-string-key";
	private final static String LOGGING_ENABLED_KEY                  = "logging-enabled-key";

	private ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTitle("iCart");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// get references

		// tvData = (TextView) findViewById(R.id.tvDataID);
		// pb = (ProgressBar) findViewById(R.id.progressBar1);
		// pb.setVisibility(View.INVISIBLE);
		// etPollingInterval = (EditText)
		// findViewById(R.id.etPollingIntervalID);



		mViewPager = (ViewPager)findViewById(R.id.mainActivityViewPager);
		android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
		mViewPager.setAdapter(new MyAdapter(fm));

		if (savedInstanceState != null) {
			if (savedInstanceState.keySet().contains(LAST_WIFI_SCAN_TIME_STRING_KEY)) {
				lastWifiScanTime = new Date(savedInstanceState.getLong(LAST_WIFI_SCAN_TIME_STRING_KEY));
			}
		}

		initWifiScan();
		wifiManager.startScan();
	}

	private void initWifiScan() {
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiBroadcastReceiver = new WifiBroadcastReceiver(this);
		wifiIntentFilter = new IntentFilter();
		wifiIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		this.registerReceiver(wifiBroadcastReceiver, wifiIntentFilter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.registerReceiver(wifiBroadcastReceiver, wifiIntentFilter);
		wifiManager.startScan();
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.unregisterReceiver(wifiBroadcastReceiver);
	}

	private String showNumber() {

		Bundle bundle = getIntent().getExtras();
		String message = bundle.getString("send_data");
		return message;

	}

	private void showList() {
		// Create list of items

		List<String> where = new ArrayList<String>();

		where.add(0, "11");
		where.add(1, "21");

		String[] simpleArray = new String[where.size()];
		where.toArray(simpleArray);

		String[] myItems = { "blue", "green", "red", "blue", "green", "red",
				"blue", "green", "red", "blue", "green", "red", "blue",
				"green", "red", "blue", "green", "red" };

		// Build Adapter

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.da_items, simpleArray);

		// Configure the list view.

		ListView list = (ListView) findViewById(R.id.listViewMain);
		list.setAdapter(adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		// getMenuInflater().inflate(R.menu., menu);
		// MenuInflater inflater = getMenuInflater();
		// inflater.inflate(R.menu.mmain_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.startShopping_id:
			// startQuery();
			return true;
		case R.id.stopShopping_id:
			//stopQuery();
			return true;

		default:
			return super.onOptionsItemSelected(item);

		}
	}



	class MyAdapter extends FragmentPagerAdapter{

		public MyAdapter(android.support.v4.app.FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			switch (position)
			{
				case 0 : fragment = new CartsFragment();
					break;
				case 1 : fragment = new NotificationFragment();
					break;
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			String string = new String();
			switch (position)
			{
				case 0 : return "Carts";
				case 1 : return "Notifications";
			}
			return string;
		}
	}

}
