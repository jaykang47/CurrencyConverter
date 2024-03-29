package test;
//necessary components are imported
import java.io.IOException;
import java.util.Scanner;
import java.text.DecimalFormat;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class CurrencyConverter{
	public static final String ACCESS_KEY = "Access_Code";	//Input access code
	public static final String BASE_URL = "http://apilayer.net/api/";
	public static final String ENDPOINT = "live";

	// this object is used for executing requests to the (REST) API
	static CloseableHttpClient httpClient = HttpClients.createDefault();
	
	public static void sendConvertRequest(String from, String to, double amount, boolean USSource){	
		//USSource true if from US source

		// the "from", "to" and "amount" can be set as variables
		HttpGet get = new HttpGet(BASE_URL + ENDPOINT + "?access_key=" + ACCESS_KEY + "&currencies=" + from +","+to);
		try {
			CloseableHttpResponse response =  httpClient.execute(get);
			HttpEntity entity = response.getEntity();
			JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
			
			System.out.println("\nSingle-Currency Conversion");
			
			// access the parsed JSON objects
			System.out.println("From : " + from);
			System.out.println("To : " + to);
			System.out.println("Amount : " + amount + " " + from);
			
			//source currency to USD rate
			double sourceToUSD = jsonObject.getJSONObject("quotes").getDouble("USD"+to);
			//rate from USD to destination currency
			double USToRate=jsonObject.getJSONObject("quotes").getDouble("USD"+from);
			DecimalFormat df = new DecimalFormat("#.##");
			
			if(USSource) {	
				System.out.println("Conversion Result : " + df.format(amount*USToRate) + " "+to);//jsonObject.getDouble("result"));
			}
			else {	//If converting from non USD
				amount = amount * (1/USToRate) * sourceToUSD;
				System.out.println("Conversion Result : " + df.format(amount) + " "+to);
			}
			System.out.println("\n");
			response.close();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException{
		Scanner sc = new Scanner(System.in);
		boolean USSource;
		String src, dest;
		double amount;
		
		//Main function
		System.out.println("Currency Converter");
		System.out.println("Enter Source Currency Code: (i.e CAD, USD, etc)");
		src = sc.nextLine().toUpperCase();
		
		System.out.println("Enter Destination Currency Code: (i.e CAD, USD, etc)");
		dest = sc.nextLine().toUpperCase();
		
		System.out.println("Enter amount to convert: ");
		amount = sc.nextDouble();
		
		if(src == "USD")
			USSource=true;
		else
			USSource=false;
		
		sendConvertRequest(src, dest, amount, USSource);
		httpClient.close();
		sc.close();
		return;
	}
}
