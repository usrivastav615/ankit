package com.example.smartcart.parsers;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import com.example.smartcart.model.Flowers;



import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;


public class FlowerXMLParser {
	public static List<Flowers> parseFeed(String content) {

		try {

			boolean inDataItemTag = false;
			String currentTagName = "";
			Flowers flower = null;
			List<Flowers> flowerList = new ArrayList<>();

			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new StringReader(content));

			int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {

				switch (eventType) {
					case XmlPullParser.START_TAG:
						currentTagName = parser.getName();
						if(currentTagName.equals("product")){
							inDataItemTag = true;
							flower = new Flowers();
							flowerList.add(flower);
						}
						break;
						
					case XmlPullParser.END_TAG:
						if(parser.getName().equals("product")){
							inDataItemTag = false;
						}
						currentTagName = "";
						break;
						
					case XmlPullParser.TEXT:
						if(inDataItemTag && flower != null){
							switch(currentTagName){
							case "productId":
								flower.setProductId(Integer.parseInt(parser.getText()));
								break;
							case "name" :
								flower.setName(parser.getText());
								break;
							case "instructions":
								flower.setInstructions(parser.getText());
								break;
							case "category":
								flower.setCategory(parser.getText());
								break;
							case "price":
								flower.setPrice(Double.parseDouble(parser.getText()));
								break;
							case "photo":
								flower.setPhoto(parser.getText());
							default:
								break;
							}
						}
						break;

				}
				
				eventType = parser.next();
			}
			
			return flowerList;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}


