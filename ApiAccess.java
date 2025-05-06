package battleshipdemo;
        
import java.net.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import java.util.*;

/**
 *
 * @author indya
 */
public class ApiAccess {

 
    // The connection to the API has recursion to ensure we consistently get a value back from it as it almost always causes a reset connection error
    //if you connect to the site over your web browser you get the same connectivity issues
    public static String getTime(String IP){
        return convertWorldDateTime(connectToWorldClock(0, IP));
    }
    
    public static String getTime(){
        return convertWorldDateTime(connectToWorldClock(0));
    }
    
    //the string containing the time also has the date, this code starts reading at the letter T (for time) 
    //and only returns the hour and minute digits
    public static String convertWorldDateTime(String dateTime){
        //convert datetime string to char array
        char[] DTArray = dateTime.toCharArray();
        
        //create a new blank string
        String timeString = "";
        
        // see code below
        int i = 0;
        
        //for each char in the array do the following
        for(char c : DTArray){
            
            //If i is greater than 0 (because it is one of the first 5 characters after the letter T) add it to the string
            if( i > 0){
                timeString += c;
                i--;
            }
            //If the char is T make I 5 so that the loop knows to add the next five characters to the string
            if( c == 'T'){
                i=5;
            }
        }
        //return the new string
        return timeString;
    }
    
    public static String connectToWorldClock(int r, String IP){
        //the function retries the connection because this api frequently resets it
        //the if statement limits the recursion to around 50 tries
        if(r < 50){
        try {
            //Using URI because apparently URL is depricated
            //world time api http://worldtimeapi.org/
            URI worldClockByIP = new URI("http://worldtimeapi.org/api/ip/"+IP);
            
            //Create a class for the connection
            HttpURLConnection ClockConnection = (HttpURLConnection)worldClockByIP.toURL().openConnection();
            
            //set the connection settings
            ClockConnection.setRequestMethod("GET");
            ClockConnection.setConnectTimeout(10000);
            
            //connect to the api
            ClockConnection.connect();
            
            //get the response code for the api 
            int responseCode = ClockConnection.getResponseCode();
            
            //200 means sucess, anything else is an error
            if(responseCode != 200){
                return "time error";
            }else{
                //make a new empty string
                String apiData = "";
                
                //Make a scanner on the stream from the api
                Scanner clockScanner = new Scanner(worldClockByIP.toURL().openStream());
                
                //append all lines from scanner to string
                while (clockScanner.hasNext()){
                    apiData += clockScanner.nextLine();
                }
                
                //close the scanner
                clockScanner.close();
                
                //instance a new JSON Parser
                JSONParser dataParser = new JSONParser();
                //Parse the text back into a JSONObject
                JSONObject dataObject = (JSONObject) dataParser.parse(apiData);
                //get the datetime field explicit convert to string
                String dataField = (String) dataObject.get("datetime");
                // return the datetime field as a string
                return dataField;
                
            }
           
            
        }catch (Exception e){
            //e.printStackTrace();
            
            //if there was an error try again and up the recursion count
            return connectToWorldClock(r+1, IP);
        }
        }else{
            //any error gets returned as a string stating the error
            return("time error");
        }
    }
    
     public static String connectToWorldClock(int r){
        //the function retries the connection because this api frequently resets it
        //the if statement limits the recursion to around 50 tries
        if(r < 50){
        try {
            //Using URI because apparently URL is depricated
            //world time api http://worldtimeapi.org/
            URI worldClockByIP = new URI("http://worldtimeapi.org/api/ip");
            
            //Create a class for the connection
            HttpURLConnection ClockConnection = (HttpURLConnection)worldClockByIP.toURL().openConnection();
            
            //set the connection settings
            ClockConnection.setRequestMethod("GET");
            ClockConnection.setConnectTimeout(10000);
            
            //connect to the api
            ClockConnection.connect();
            
            //get the response code for the api 
            int responseCode = ClockConnection.getResponseCode();
            
            //200 means sucess, anything else is an error
            if(responseCode != 200){
                return "time error";
            }else{
                //make a new empty string
                String apiData = "";
                
                //Make a scanner on the stream from the api
                Scanner clockScanner = new Scanner(worldClockByIP.toURL().openStream());
                
                //append all lines from scanner to string
                while (clockScanner.hasNext()){
                    apiData += clockScanner.nextLine();
                }
                
                //close the scanner
                clockScanner.close();
                
                //instance a new JSON Parser
                JSONParser dataParser = new JSONParser();
                //Parse the text back into a JSONObject
                JSONObject dataObject = (JSONObject) dataParser.parse(apiData);
                //get the datetime field explicit convert to string
                String dataField = (String) dataObject.get("datetime");
                // return the datetime field as a string
                return dataField;
                
            }
           
            
        }catch (Exception e){
            //e.printStackTrace();
            
            //if there was an error try again and up the recursion count
            return connectToWorldClock(r+1);
        }
        }else{
            //any error gets returned as a string stating the error
            return("time error");
        }
    }
    
    //same idea as getTime, make sure the r count starts a 0 with a second function
    public static String getCountry(String IP){
        return(connectToCountryIs(0, IP));
    }
    
    public static String getCountry(){
        return(connectToCountryIs(0));
    }
    
    // this was originally supposed to be using the https://maxmind.github.io/GeoIP2-java/ API but there was a class not found error when I ran the code
    //this api is easier and does what i need it to do anyway
    
    public static String connectToCountryIs(int r, String IP){
        //recursion count set at 9 since the api is rate limited to 10 requests per second
        if(r < 9){
        try {
            //country.is api https://country.is/
            URI LocationByIP = new URI("https://api.country.is"+IP);
            
            //Create connection class
            HttpURLConnection LocationConnection = (HttpURLConnection)LocationByIP.toURL().openConnection();
            
            //alter connection settings
            LocationConnection.setRequestMethod("GET");
            LocationConnection.setConnectTimeout(10000);
            
            //connect to api
            LocationConnection.connect();
            
            //get response code
            int responseCode = LocationConnection.getResponseCode();
            
            //200 = sucess all else is failure
            if(responseCode != 200){
                //if failed return the word error
                return "ERROR";
            }else{
                //start with empty string
                String apiData = "";
                //set up scanner for the api connection
                Scanner LocationScanner = new Scanner(LocationByIP.toURL().openStream());
                
                //append all lines from the scanner to the string
                while (LocationScanner.hasNext()){
                    apiData += LocationScanner.nextLine();
                }
                
                //close the scanner
                LocationScanner.close();
                
                //instance new json parser
                JSONParser dataParser = new JSONParser();
                
                //parse data into a new json object
                JSONObject dataObject = (JSONObject) dataParser.parse(apiData);
                
                //get the field we need out and explicit convert to string
                String dataField = (String) dataObject.get("country");
                
                //return string
                return dataField;
                
            }
           
            
        }catch (Exception e){
            //e.printStackTrace();
            //recurse and tick up the count if there is a error
            return connectToWorldClock(r+1, IP);
        }
        
        }else{
            //if recursion count is too high return "error"
            return("ERROR");
        }
    }
    
    
    public static String connectToCountryIs(int r){
        //recursion count set at 9 since the api is rate limited to 10 requests per second
        if(r < 9){
        try {
            //country.is api https://country.is/
            URI LocationByIP = new URI("https://api.country.is");
            
            //Create connection class
            HttpURLConnection LocationConnection = (HttpURLConnection)LocationByIP.toURL().openConnection();
            
            //alter connection settings
            LocationConnection.setRequestMethod("GET");
            LocationConnection.setConnectTimeout(10000);
            
            //connect to api
            LocationConnection.connect();
            
            //get response code
            int responseCode = LocationConnection.getResponseCode();
            
            //200 = sucess all else is failure
            if(responseCode != 200){
                //if failed return the word error
                return "ERROR";
            }else{
                //start with empty string
                String apiData = "";
                //set up scanner for the api connection
                Scanner LocationScanner = new Scanner(LocationByIP.toURL().openStream());
                
                //append all lines from the scanner to the string
                while (LocationScanner.hasNext()){
                    apiData += LocationScanner.nextLine();
                }
                
                //close the scanner
                LocationScanner.close();
                
                //instance new json parser
                JSONParser dataParser = new JSONParser();
                
                //parse data into a new json object
                JSONObject dataObject = (JSONObject) dataParser.parse(apiData);
                
                //get the field we need out and explicit convert to string
                String dataField = (String) dataObject.get("country");
                
                //return string
                return dataField;
                
            }
           
            
        }catch (Exception e){
            //e.printStackTrace();
            //recurse and tick up the count if there is a error
            return connectToCountryIs(r+1);
        }
        
        }else{
            //if recursion count is too high return "error"
            return("ERROR");
        }
    }
    
    //MAIN function for unit testing
    /* 
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println(getTime());
        System.out.println(getCountry());
    }
    */
    
}
