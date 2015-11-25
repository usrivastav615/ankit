package com.example.smartcart;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smartcart.record.ParsedNdefRecord;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class loginPage extends ActionBarActivity {
	
	public Button myCart;
	public EditText phoneNo;
	public TextView mTextView = null;
	public TextView mWifiList = null;

	public static WifiManager wifiManager;
	public WifiBroadcastReceiver wifiBroadcastReceiver;
	public IntentFilter wifiIntentFilter;
	public Date lastWifiScanTime;
	private final static String LOCATION_KEY                         = "location-key";
	private final static String LAST_LOCATION_UPDATE_TIME_STRING_KEY = "last-location-update-time-string-key";
	private final static String LAST_WIFI_SCAN_TIME_STRING_KEY       = "last-wifi-scan-time-string-key";
	private final static String LOGGING_ENABLED_KEY                  = "logging-enabled-key";

	public static final long WIFI_SCAN_DELAY_MILLIS = 2000;

	public static final String MIME_TEXT_PLAIN = "text/plain";

	private static final DateFormat TIME_FORMAT = SimpleDateFormat.getDateTimeInstance();
	private LinearLayout mTagContent;

	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private NdefMessage mNdefPushMessage;

	private AlertDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTitle("iCart");
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginpage);
		
		phoneNo = (EditText) findViewById(R.id.editText1);
		mTagContent = (LinearLayout) findViewById(R.id.list);
		
		myCart = (Button) findViewById(R.id.myCartButtonId);
		mWifiList = (TextView)findViewById(R.id.wifiList);
		mTextView = (TextView)findViewById(R.id.minWifi);
		
		myCart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String message = phoneNo.getText().toString();
				Intent i = new Intent("second");
				Bundle bundle = new Bundle();
				bundle.putString("send_data", message);
				i.putExtras(bundle);
				startActivity(i);

			}
		});

		if (savedInstanceState != null) {
			if (savedInstanceState.keySet().contains(LAST_WIFI_SCAN_TIME_STRING_KEY)) {
				lastWifiScanTime = new Date(savedInstanceState.getLong(LAST_WIFI_SCAN_TIME_STRING_KEY));
			}
		}
		initWifiScan();
		wifiManager.startScan();
		mAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mAdapter == null) {
			showMessage("error", "no nfc");
			finish();
			return;
		}

		mPendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		mNdefPushMessage = new NdefMessage(new NdefRecord[] { newTextRecord(
				"Message from NFC Reader :-)", Locale.ENGLISH, true) });
		//handleIntent(getIntent());
	}

	private void showMessage(String title, String message) {
		mDialog.setTitle(title);
		mDialog.setMessage(message);
		mDialog.show();
	}

	private NdefRecord newTextRecord(String text, Locale locale, boolean encodeInUtf8) {
		byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

		Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
		byte[] textBytes = text.getBytes(utfEncoding);

		int utfBit = encodeInUtf8 ? 0 : (1 << 7);
		char status = (char) (utfBit + langBytes.length);

		byte[] data = new byte[1 + langBytes.length + textBytes.length];
		data[0] = (byte) status;
		System.arraycopy(langBytes, 0, data, 1, langBytes.length);
		System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
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
		if (mAdapter != null) {
			if (!mAdapter.isEnabled()) {
				showWirelessSettingsDialog();
			}
			mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
			mAdapter.enableForegroundNdefPush(this, mNdefPushMessage);
		}
	}

	private void showWirelessSettingsDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("nfc disabled");
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int i) {
				Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
				startActivity(intent);
			}
		});
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int i) {
				finish();
			}
		});
		builder.create().show();
		return;
	}

	private void resolveIntent(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefMessage[] msgs;
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
			} else {
				// Unknown tag type
				byte[] empty = new byte[0];
				byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
				Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
				byte[] payload = dumpTagData(tag).getBytes();
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				msgs = new NdefMessage[] { msg };
			}
			// Setup the views
			buildTagViews(msgs);
		}
	}

	private String dumpTagData(Parcelable p) {
		StringBuilder sb = new StringBuilder();
		Tag tag = (Tag) p;
		byte[] id = tag.getId();
		sb.append("Tag ID (hex): ").append(getHex(id)).append("\n");
		sb.append("Tag ID (dec): ").append(getDec(id)).append("\n");
		sb.append("ID (reversed): ").append(getReversed(id)).append("\n");

		String prefix = "android.nfc.tech.";
		sb.append("Technologies: ");
		for (String tech : tag.getTechList()) {
			sb.append(tech.substring(prefix.length()));
			sb.append(", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		for (String tech : tag.getTechList()) {
			if (tech.equals(MifareClassic.class.getName())) {
				sb.append('\n');
				MifareClassic mifareTag = MifareClassic.get(tag);
				String type = "Unknown";
				switch (mifareTag.getType()) {
					case MifareClassic.TYPE_CLASSIC:
						type = "Classic";
						break;
					case MifareClassic.TYPE_PLUS:
						type = "Plus";
						break;
					case MifareClassic.TYPE_PRO:
						type = "Pro";
						break;
				}
				sb.append("Mifare Classic type: ");
				sb.append(type);
				sb.append('\n');

				sb.append("Mifare size: ");
				sb.append(mifareTag.getSize() + " bytes");
				sb.append('\n');

				sb.append("Mifare sectors: ");
				sb.append(mifareTag.getSectorCount());
				sb.append('\n');

				sb.append("Mifare blocks: ");
				sb.append(mifareTag.getBlockCount());
			}

			if (tech.equals(MifareUltralight.class.getName())) {
				sb.append('\n');
				MifareUltralight mifareUlTag = MifareUltralight.get(tag);
				String type = "Unknown";
				switch (mifareUlTag.getType()) {
					case MifareUltralight.TYPE_ULTRALIGHT:
						type = "Ultralight";
						break;
					case MifareUltralight.TYPE_ULTRALIGHT_C:
						type = "Ultralight C";
						break;
				}
				sb.append("Mifare Ultralight type: ");
				sb.append(type);
			}
		}

		return sb.toString();
	}

	private String getHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = bytes.length - 1; i >= 0; --i) {
			int b = bytes[i] & 0xff;
			if (b < 0x10)
				sb.append('0');
			sb.append(Integer.toHexString(b));
			if (i > 0) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}

	private long getDec(byte[] bytes) {
		long result = 0;
		long factor = 1;
		for (int i = 0; i < bytes.length; ++i) {
			long value = bytes[i] & 0xffl;
			result += value * factor;
			factor *= 256l;
		}
		return result;
	}

	private long getReversed(byte[] bytes) {
		long result = 0;
		long factor = 1;
		for (int i = bytes.length - 1; i >= 0; --i) {
			long value = bytes[i] & 0xffl;
			result += value * factor;
			factor *= 256l;
		}
		return result;
	}

	void buildTagViews(NdefMessage[] msgs) {
		if (msgs == null || msgs.length == 0) {
			return;
		}
		LayoutInflater inflater = LayoutInflater.from(this);
		LinearLayout content = mTagContent;

		// Parse the first message in the list
		// Build views for all of the sub records
		Date now = new Date();
		List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0], this);
		final int size = records.size();
		for (int i = 0; i < size; i++) {
			TextView timeView = new TextView(this);
			timeView.setText(TIME_FORMAT.format(now));
			content.addView(timeView, 0);
			ParsedNdefRecord record = records.get(i);
//			TextView t = (TextView)findViewById(R.id.rfidtext);
//			t.setText(record.toString());
			//content.addView(inflater.inflate(R.layout.tag_divider, content, false), 2 + i);
		}
	}


	@Override
	protected void onPause() {
		super.onPause();
		this.unregisterReceiver(wifiBroadcastReceiver);
		if (mAdapter != null) {
			mAdapter.disableForegroundDispatch(this);
			mAdapter.disableForegroundNdefPush(this);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		/**
		 * This method gets called, when a new Intent gets associated with the current activity instance.
		 * Instead of creating a new activity, onNewIntent will be called. For more information have a look
		 * at the documentation.
		 *
		 * In our case this method gets called, when the user attaches a Tag to the device.
		 */
		setIntent(intent);
		resolveIntent(intent);
	}

	private void handleIntent(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

			String type = intent.getType();
			if (MIME_TEXT_PLAIN.equals(type)) {

				Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
				new NdefReaderTask().execute(tag);

			} else {
				Log.d("abc", "Wrong mime type: " + type);
			}
		} else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

			// In case we would still use the Tech Discovered Intent
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			String[] techList = tag.getTechList();
			String searchedTech = Ndef.class.getName();

			for (String tech : techList) {
				if (searchedTech.equals(tech)) {
					new NdefReaderTask().execute(tag);
					break;
				}
			}
		}
	}

	public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
		final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

		IntentFilter[] filters = new IntentFilter[1];
		String[][] techList = new String[][]{};

		// Notice that this is the same filter as in our manifest.
		filters[0] = new IntentFilter();
		filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
		filters[0].addCategory(Intent.CATEGORY_DEFAULT);
		try {
			filters[0].addDataType(MIME_TEXT_PLAIN);
		} catch (IntentFilter.MalformedMimeTypeException e) {
			throw new RuntimeException("Check your mime type.");
		}

		adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
	}

	/**
	 * @param activity The corresponding {@link BaseActivity} requesting to stop the foreground dispatch.
	 * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
	 */
	public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
		adapter.disableForegroundDispatch(activity);
	}

	private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

		@Override
		protected String doInBackground(Tag... params) {
			Tag tag = params[0];

			Ndef ndef = Ndef.get(tag);
			if (ndef == null) {
				// NDEF is not supported by this Tag.
				return null;
			}

			NdefMessage ndefMessage = ndef.getCachedNdefMessage();

			NdefRecord[] records = ndefMessage.getRecords();
			for (NdefRecord ndefRecord : records) {
				if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
					try {
						return readText(ndefRecord);
					} catch (UnsupportedEncodingException e) {
						Log.e("abc", "Unsupported Encoding", e);
					}
				}
			}

			return null;
		}

		private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

			byte[] payload = record.getPayload();

			// Get the Text Encoding
			String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

			// Get the Language Code
			int languageCodeLength = payload[0] & 0063;

			// String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
			// e.g. "en"

			// Get the Text
			return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				mTextView.setText("Read content: " + result);
			}
		}
	}

}
