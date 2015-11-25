package com.example.smartcart.parsers;

import com.example.smartcart.R;
import com.example.smartcart.model.sparkFun;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class sparkFunJSONParser {

	public static List<sparkFun> parseFeed(String content) {

		try {
			JSONArray ar = new JSONArray(content);
			List<sparkFun> itemsList = new ArrayList<>();

			for (int i = 0; i < ar.length(); i++) {

				JSONObject obj = ar.getJSONObject(i);

				sparkFun SparkFun = new sparkFun();

				SparkFun.setPhonenumber(obj.getString("phonenumber"));
				SparkFun.setProductname(obj.getString("productname"));
				SparkFun.setProductprice(obj.getString("productprice"));
				SparkFun.setKaand(obj.getString("kaand"));
				SparkFun.setTimestamp(obj.getString("timestamp"));
				SparkFun.setNoOfItems(ar.length());

				String product = obj.getString("productname").toLowerCase();
				
				
//				switch (product) {
//				case  product.contains("oreo"):

//				}
				
				if (product.contains("oreo")){
					SparkFun.setIconID(R.drawable.ic_launcher);
				}
				if (product.contains("jam")){
					SparkFun.setIconID(R.drawable.ic_launcher);
				}
				if (product.contains("sauce")){
					SparkFun.setIconID(R.drawable.ic_launcher);
				}
				if (product.contains("pil")){
					SparkFun.setIconID(R.drawable.ic_launcher);
				}
				if (product.contains("milk")){
					SparkFun.setIconID(R.drawable.ic_launcher);
				}
				if (product.contains("horlic")){
					SparkFun.setIconID(R.drawable.ic_launcher);
				}
				if (product.contains("invalid")){
					SparkFun.setIconID(R.drawable.ic_launcher);
				}
//


				itemsList.add(SparkFun);

			}
			
			
			

			return itemsList;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

	}
}
