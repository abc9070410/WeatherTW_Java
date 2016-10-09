package weatherTW;

import java.net.*;
import java.io.*;
import weatherTW.GovData;
    
/**
 * Created by Chien-Yu on 2016/10/1.
 */
public class Common {
    
    private static String gsDistance;
    private static String[] gasStationGPS;
        
    private static double getDistanceFromLatLonInKm(
        double lat1, double lon1, double lat2, double lon2) 
    {
        int R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2-lat1); // deg2rad below
        double dLon = deg2rad(lon2-lon1); 
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * 
                   Math.sin(dLon/2) * Math.sin(dLon/2); 
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
        double d = R * c; // Distance in km
        
        return d;
    }
    
    private static double deg2rad(double deg) 
    {
        return deg * Math.PI / 180;
    }
    
    public static int getNearLocationIndex(int iStationType, double fLat, double fLon)
    {
    	double fMinKM = 1000;
    	int fMinIndex = -1;
        
        double fMinLat = 0;
        double fMinLon = 0;
      
        //Common.DP("getNearLocationIndex:" + asData[0] + ":" + asData.length + "," + fLat + "," + fLon);
        
        int iStationAmount = GovData.getStationAmount(iStationType);
    
    	for (int i = 0; i < iStationAmount; i++)
    	{
            double fStationLat = GovData.getStationLatitude(iStationType, i);
            double fStationLon = GovData.getStationLongitude(iStationType, i);
            
    		double fKM = getDistanceFromLatLonInKm(fStationLat, fStationLon, fLat, fLon);
    		
    		if (fKM < fMinKM)
    		{
    			fMinKM = fKM;
    			fMinIndex = i;
                
                fMinLat = fStationLat;
                fMinLon = fStationLon;
    			
    			//Common.DP("MIN:" + fMinIndex + ":" + fMinKM);
    		}
    	}

        gsDistance = "" + fMinKM;
        gasStationGPS = new String[]{"" + fMinLat, "" + fMinLon};
    	
        Common.DP("Got nearest site " + GovData.getStationType(iStationType) +
                  "[" + fMinIndex + "]: " + 
                  GovData.getStationInfo(iStationType, fMinIndex, GovData.STATION_LOCATION) + "_" + 
                  GovData.getStationInfo(iStationType, fMinIndex, GovData.STATION_NAME) + "_" + 
                  GovData.getStationInfo(iStationType, fMinIndex, GovData.STATION_ID) + 
                  " DIS:" + gsDistance + 
                  " GPS:" + gasStationGPS[0] + "_" + gasStationGPS[1]);
        
    	return fMinIndex;
    }
    
    
        /**
    * Returns the output from the given URL.
    * 
    * I tried to hide some of the ugliness of the exception-handling
    * in this method, and just return a high level Exception from here.
    * Modify this behavior as desired.
    * 
    * @param desiredUrl
    * @return
    * @throws Exception
    */
    private static String doHttpUrlConnectionAction(String desiredUrl)
    {
        URL url = null;
        BufferedReader reader = null;
        StringBuilder stringBuilder;

        try
        {
            // create the HttpURLConnection
            url = new URL(desiredUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // just want to do an HTTP GET here
            connection.setRequestMethod("GET");

            // uncomment this if you want to write output to this url
            //connection.setDoOutput(true);

            // give it 15 seconds to respond
            connection.setReadTimeout(15*1000);
            connection.connect();

            // read the output from the server
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line + "\n");
            }
            
            String sText = stringBuilder.toString();
            
            DP("Download Done : Text length is " + sText.length());
            
            return sText;
        }
        catch (Exception e)
        {
            DP("Download Fail");
            e.printStackTrace();            
        }
        finally
        {
            // close the reader; this can throw an exception too, so
            // wrap it in another try/catch block.
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
        }
        
        return null;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    /*
    public static void parseTownFutureRain(sText)
    {
        var asTokens = sText.split("<tr");

        // 日期, 時間, 天氣狀況, 溫度, 蒲福風級, 風向, 相對溼度, 降雨機率, 舒適度

        var aasFutureRainData = [];

        for (var i = 1; i < asTokens.length; i ++)
        {
            var asRow = asTokens[i].trim().split("<td");

            var asData = [];

            for (var j = 2; j < asRow.length; j ++)
            {
                var sTemp = "<td" + asRow[j];
                var sData = sTemp.replace( /<[^<>]+>/g, " " ).trim().replace(/\s+/g, "_");

                if (sTemp.indexOf("<img") > 0)
                {
                    var asTemp2 = sTemp.split("\"");
                    sData = asTemp2[1] + "_" + asTemp2[3];
                }

                if (sTemp.indexOf("colspan=") > 0)
                {
                    var iColspan = parseInt(sTemp.split("\"")[1]); 

                    for (var k = 0; k < iColspan; k ++)
                    {
                        asData[asData.length] = sData;
                    }
                }
                else
                {
                    asData[asData.length] = sData;
                }
            }

            console.log( i + "," + j + " : " + asData);

            aasFutureRainData[i - 1] = asData;
        }

        return aasFutureRainData;
    }
    
    */
    
    
    
    
    
    // Debug Print
    public static void DP(String str)
    {
        System.out.print(str + "\r\n");
    }
  
    public static void main(String[] args)
    {
        Common.DP("Hello !");
        
        double fLat = 24.822834;//24.839068;//24.081400;
        double fLon = 121.183654;//121.009183;//120.538335;
        
        int iStationIndex = 0;
        String sStationInfo = "";
        String sUrl = "";
        String sText = "";
        
        GovData.initAllStationData();

        iStationIndex = Common.getNearLocationIndex(GovData.TYPE_PAST_STATION, fLat, fLon);
        
        iStationIndex = Common.getNearLocationIndex(GovData.TYPE_PAST_24HR_STATION, fLat, fLon);
        sUrl = GovData.getStationWeatherUrl(GovData.TYPE_PAST_24HR_STATION, iStationIndex);
        sText = doHttpUrlConnectionAction(sUrl);
        //DP(sText);
        GovData.parsePastRain(sText);
        
        
        iStationIndex = Common.getNearLocationIndex(GovData.TYPE_FUTURE_STATION, fLat, fLon);
        sUrl = GovData.getStationWeatherUrl(GovData.TYPE_FUTURE_STATION, iStationIndex);
        sText = doHttpUrlConnectionAction(sUrl);
        //DP(sText);
        //GovData.parseFutureRain(sText);
        
    }
}
    