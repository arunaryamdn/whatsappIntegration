/* arun aryasomayajula 
#This program/code reads all the mobile numbers in a list and sends a whatsapp message with a pre defined template

# the format is something like this - 
#a csv file should be provided, first row of the csv file should be the message you want to send and 
#from second row all the mobile numbers you want to send a message. */

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets; 
import java.nio.file.Files; 
import java.nio.file.Path; 
import java.nio.file.Paths; 
import java.util.ArrayList;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import java.util.Scanner;
import java.io.FileWriter;  
//import java.util.Base64;
import javax.xml.bind.DatatypeConverter;

class Variables{
		public static String WhatsAppElementName = null;
		public static String NameSpace = null;
		public static String Values = null;
}

class UserDetails{
	public String Message;
	public String MobileNumber;
	public String MediaAttachment;
	public String WhatsAppElement;
	public ArrayList<String> Values;
	
	public UserDetails(String msg, String mob){
		Message = msg;
		MobileNumber = mob;
	}
	
	public UserDetails(String msg, String mob, String media){
		Message = msg;
		MobileNumber = mob;
		MediaAttachment = media;
	}
	
	public UserDetails(String msg, String mob, ArrayList<String> vals){
		Message = msg;
		MobileNumber = mob;
		Values = vals;
	}
	
	public String getUserMessage(){
		return Message;
	}
	
	public String getUserMobile(){
		return MobileNumber;
	}
	
	public String getUserMediaAttachment(){
		return MediaAttachment;
	}
	
	public ArrayList<String> getValues(){
		return Values;
	}
}

public class WhatsAppSender {
 
	public static void main(String... args) {
	
		System.out.println("1. Enter 1 for sending a text message \n2. Enter 2 for sending an attachment \n3. Enter 3 for sending a HSM");
		
		Scanner scanner = new Scanner(System.in);
		System.out.print("Your input is: ");
		int inputMethod = scanner.nextInt();
		
		switch (inputMethod){
		
			case 1: sendTextMessage();
					break;
			case 2: sendAttachment();
					break;
			case 3: sendHSM();
					break;
		}	
	}
	
	private static String encodeFileToBase64Binary(File file){
            String encodedfile = null;
            try {
                FileInputStream fileInputStreamReader = new FileInputStream(file);
                byte[] bytes = new byte[(int)file.length()];
                fileInputStreamReader.read(bytes);
				encodedfile = DatatypeConverter.printBase64Binary(bytes);
                //encodedfile = Base64.getEncoder().encodeToString(bytes);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return encodedfile;
        }

 private static void sendAttachment(){
 	
	List<UserDetails> userDetails = readFromCSVFromMediaMessage("./sendmediamessage.csv");
	int count = 1;
	File f =  new File(userDetails.get(0).MediaAttachment);
	String encodstring = encodeFileToBase64Binary(f);
	try
		{   
			FileWriter fw = new FileWriter("./senderlog.txt");   
			for (UserDetails eachUserDetail : userDetails)
			{
				URL url = new URL("http://mojo-wabot.mmt.mmt/api/sendMediaStream");
			
				// URL url = new URL("http://172.16.47.51/api/sendMediaStream");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();

				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/json");

				OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());				
				String jsonObj = "{\"message\": \"" + eachUserDetail.Message + "\"," + 
								 "\"phoneNumber\": \"+91" + eachUserDetail.MobileNumber + "\"," + 
								 "\"fileName\": \"contentimage1.png\"," + 
								 "\"fileStream\": \""+ encodstring + "\"}";
				//System.out.println(jsonObj);
				out.write(jsonObj);
				out.flush();
				out.close();

				int res = connection.getResponseCode();
				fw.write("Error code for row: " + count + " is " + res + System.getProperty( "line.separator" ));
				count++;
			}	
			 fw.close(); 
		}
		catch(Exception e)
		{
			System.out.println(e);
		}		 
  }
  
 private static void sendHSM(){
	 
	 List<UserDetails> userDetails = readFromCSVFromHSM("./sendHSMessage.csv");
	 int count = 1;
	 try
		{   
			FileWriter fw = new FileWriter("./senderlog.txt");   
			for (UserDetails eachUserDetail : userDetails)
			{
		
				URL url = new URL("http://mojo-wabot.mmt.mmt/api/sendHSM/");
				//URL url = new URL("http://172.16.47.51/api/sendHSM/");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			   
			    //construct values 
				
			    Variables.Values = "[";
				if(eachUserDetail.Values.size() == 1)
				{
					 Variables.Values = Variables.Values + "\"" + eachUserDetail.Values.get(0) + "\",";
				}
				else
				{
					for (String str : eachUserDetail.Values)
					 {
						Variables.Values = Variables.Values + "\"" + str + "\",";
					 }
				}
				 Variables.Values = Variables.Values.substring(0, Variables.Values.length() - 1);
				 Variables.Values = Variables.Values + "]";
			 
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/json");
				OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
				String jsonObj =   "{\"phoneNumber\": \"+91" + eachUserDetail.MobileNumber + "\"," + 
								    "\"elementName\":" + "\""+ Variables.WhatsAppElementName + "\"," + 
									"\"namespace\":" + "\""+ Variables.NameSpace + "\"," + 
									"\"values\":" + Variables.Values + "}";
				out.write(jsonObj);
				out.flush();
				out.close();
				int res = connection.getResponseCode();
				fw.write("Error code for row: " + count + " is " + res + System.getProperty( "line.separator" ));
				count++;
			}	
			 fw.close(); 
		}
		catch(Exception e)
		{
			System.out.println(e);
		}		 
 }
  
 private static void sendTextMessage(){
		
		List<UserDetails> userDetails = readFromCSVFromTextMessage("./sendtextmessage.csv");
		int count = 1;
		try
		{   
			FileWriter fw = new FileWriter("./senderlog.txt");   
			for (UserDetails eachUserDetail : userDetails)
			{
		
				URL url = new URL("http://mojo-wabot.mmt.mmt/api/send/");
				//URL url = new URL("http://172.16.47.51/api/send/");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/json");
				OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
				String jsonObj =   "{ \"message\": \"" + eachUserDetail.Message + "\"," + "\"phoneNumber\": \"+91" + eachUserDetail.MobileNumber + "\"}";
				System.out.println(jsonObj);
				out.write(jsonObj);
				out.flush();
				out.close();

				int res = connection.getResponseCode();
				fw.write("Error code for row: " + count + " is " + res + System.getProperty( "line.separator" ));
				count++;
			}	
			 fw.close(); 
		}
		catch(Exception e)
		{
			System.out.println(e);
		}		 
	}

 private static List<UserDetails> readFromCSVFromMediaMessage(String fileName) { 

	List<UserDetails> ursDts = new ArrayList<>(); 
	Path pathToFile = Paths.get(fileName); 
	try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {
		 String message = br.readLine();
		 if(message.charAt(message.length() - 1) == ','){
		 	message = message.substring(0, message.length() - 1);
		 }
		String line = br.readLine();
		 while (line != null) {
		 	String[] splitLine = line.split(",");
		 	UserDetails usrDt = new UserDetails(message, splitLine[0], splitLine[1]);
			ursDts.add(usrDt);
			line = br.readLine();
		 }
	}
	catch (IOException ioe) {
            ioe.printStackTrace();
    }
		return ursDts;
 }
 
 private static List<UserDetails> readFromCSVFromHSM(String fileName) { 

	List<UserDetails> ursDts = new ArrayList<>(); 
	Path pathToFile = Paths.get(fileName); 

	try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {
		 
		 String element = br.readLine();
		  if(element.charAt(element.length() - 1) == ','){
		 	element = element.substring(0, element.length() - 1);
		 }
		 Variables.WhatsAppElementName = element;
		 
		 element = br.readLine();
		 if(element.charAt(element.length() - 1) == ','){
		 	element = element.substring(0, element.length() - 1);
		 }
		 Variables.NameSpace = element;
		 
		 String line = br.readLine();
		 while (line != null) {
			 ArrayList<String> lineValues = new ArrayList<>();
			 String[] lineElements = line.split(",");
			 String mobNum = lineElements[0];
			 
			 if(lineElements.length == 2)
			 {
				 lineValues.add(lineElements[1]);
			 }
			 else
			 {
				 for (int i = 1; i < lineElements.length; i++)
				 {
					 if(i == 1)
					 {
						 lineElements[i] = lineElements[i].substring(1, lineElements[i].length());
					 }
					 
					 if(i == lineElements.length - 1)
					 {
						 lineElements[i] = lineElements[i].substring(0, lineElements[i].length() - 1);
					 }
					 lineValues.add(lineElements[i]);
				 }
			 }
		 	UserDetails usrDt = new UserDetails("", mobNum, lineValues);
			ursDts.add(usrDt);
			line = br.readLine();
		 }
	}
	catch (IOException ioe) {
            ioe.printStackTrace();
    }
		return ursDts;
 }
 
 private static List<UserDetails> readFromCSVFromTextMessage(String fileName) { 

	List<UserDetails> ursDts = new ArrayList<>(); 
	Path pathToFile = Paths.get(fileName); 

	try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {
		 String message = "Yay! The first long weekend of January 2018 is here. If you haven’t planned it yet, our Long Weekend Calendar for 2018 will help you do that in a jiffy. Get ideas here! Link: https://www.makemytrip.com/blog/long-weekend-calendar-2018/";
		 String line = br.readLine();
		 while (line != null) {
		 	UserDetails usrDt = new UserDetails(message, line);
			ursDts.add(usrDt);
			line = br.readLine();
		 }
	}
	catch (IOException ioe) {
            ioe.printStackTrace();
    }
		return ursDts;
 }
}




