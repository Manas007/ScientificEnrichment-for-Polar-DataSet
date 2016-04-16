package ContentDetection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;



public class scholarParser {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub

		

		// TODO Auto-generated method stub
		
		File fl=new File("/Users/manasranjanmahanta/Documents/training1/codechef/HW3Polar.txt");
		FileInputStream fis = new FileInputStream(fl);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        
		String line = null;
		JSONArray ja=new JSONArray();
		while ((line = br.readLine()) != null) {
			//System.out.println(line);
			String id=null;
			if(!line.isEmpty())
			 id=line.split("\\[")[0].split("Desktop/599data")[1];
			Pattern pattern = Pattern.compile("\"grobid:header_Title\":\"(.*?)\",");
			Matcher matcher = pattern.matcher(line);
			String header=null;
			String author=null;
			String creationDate=null;
			int count=0;
			if (matcher.find())
			{
			   if(matcher.group(1).length()>=4)	
			   {
			       count++;
				   header = (matcher.group(1));
			    
			   }
			}
			
			Pattern patternauthor = Pattern.compile("\"meta:author\":\"(.*?)\",");
			Matcher matcherauthor = patternauthor.matcher(line);
			if (matcherauthor.find())
			{
			   if(matcherauthor.group(1).length()>=2)	
			   {
			       count++;
				   author=matcherauthor.group(1);
			   }
			}
			
			Pattern patterncreationDate = Pattern.compile("\"Creation-Date\":\"(.*?)\",");
			Matcher matcherCreationDate = patterncreationDate.matcher(line);
			if (matcherCreationDate.find())
			{
			   if(matcherCreationDate.group(1).length()>=2)	
			   {
			       count++;
			       //System.out.println(matcherCreationDate.group(1));
				   creationDate=matcherCreationDate.group(1).split("-")[0];
			   }
			}
			String company=null;
			Pattern patterncompany = Pattern.compile("\"Company\":\"(.*?)\",");
			Matcher matcherCompany = patterncreationDate.matcher(line);
			if (matcherCompany.find())
			{
			   if(matcherCompany.group(1).length()>=2)	
			   {
			       
			       //System.out.println(matcherCreationDate.group(1));
				   company=matcherCompany.group(1);
			   }
			}
			
			
			String Keywords=null;
			Pattern patternKeywords = Pattern.compile("\"Keywords\":\"(.*?)\",");
			Matcher matcherKeywords = patterncreationDate.matcher(line);
			if (matcherKeywords.find())
			{
			   if(matcherKeywords.group(1).length()>=2)	
			   {
			       
			       //System.out.println(matcherCreationDate.group(1));
				   Keywords=matcherKeywords.group(1);
			   }
			}
			if(count==3){
			JSONObject obj = new JSONObject();
			obj.put("Title",header);
			obj.put("Author", author);
			obj.put("Year",creationDate);
			obj.put("id", id);
			
			if(company!=null)
			 obj.put("Company",company);
			if(Keywords!=null)
				obj.put("Keywords",Keywords);
			ja.put(obj);
			}
			if(count==3)
			{
				System.out.println(header+"\n"+author);
				//.put("Title", header);
				header=header.substring(0,10);
				Runtime rt = Runtime.getRuntime();
				final Process pr = rt.exec("python /Users/manasranjanmahanta/scholar.py -c 20 --author \""+author+"\" --phrase \""+header+"\"");
				System.out.println("python /Users/manasranjanmahanta/scholar.py -c 20 --author \""+author+"\" --phrase \""+header+"\"");
				     BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				     String line1 = null; 
				     try {
				    	JSONObject objR = new JSONObject();
				    	
				    	boolean flag=false;
				    	String fel=input.readLine();
				    	System.out.println(fel);
				    	if(input.readLine()!=null)
				        while ((line1 = input.readLine()) != null && !line1.isEmpty()){
				            System.out.println(line1.trim());
				            String details=line1.trim();
				            //String title=null;
				          /* 
				          String[] listPublications=line1.trim().split("\n\r");
				          for(String temppub:listPublications){        	  
				        	  String[] insidePub=temppub.split("\n");*/
				            
					       
				            if(!details.isEmpty()){
				              flag=true;	
				             
				              String[] keyValue=details.split(" ",2);
				              
				              if(keyValue[0].equals("Versions")||keyValue[0].equals("Citations"))
				            	  if(keyValue[1].contains(" ")){
				            	     keyValue[1]=keyValue[1].split(" ")[1];
				                     if(keyValue[0].equals("Versions"))
				                     {
				                    	boolean flagAuthor=false; 
				                    	String Author=null;
				                    	URLConnection openConnection = new URL(keyValue[1]).openConnection();	
				                 		openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
				                 		InputStream is = openConnection.getInputStream();
				                     
				                         BufferedReader in = new BufferedReader(new InputStreamReader(is));
				                         String inputLine;        
				                         while ((inputLine = in.readLine()) != null) 
				                         {
				                            // System.out.println(inputLine);
				                         	Pattern patternauthor1 = Pattern.compile("<div class=\"gs_a\">(.*)</div>");
				                     		Matcher matcherauthor1 = patternauthor1.matcher(inputLine);
				                     		if (matcherauthor1.find())
				                     		{
				   
				                     		   if(matcherauthor1.group(1).contains("</div>"))	{
				                     			  flagAuthor=true;
				                     		      Author=matcherauthor1.group(1).split(" - ")[0].split("&hell")[0];
				                     		      objR.put("Author",Author);	     
				                     		}
				                     	
				                     		}
				                        
				                        }
				                         in.close();   	 
				                    	 	
				                       if(!flagAuthor)  
				                    	 objR.put("Author",author);
				                    	 
				                     }
				                     
				            }
				              if(keyValue[0].equals("Title"))
				              {
				            	  objR.put("id",id+"/"+keyValue[1]);
				              }
				        	  //for(String details:insidePub){				
				        	  objR.put(keyValue[0],keyValue[1]);				        		      
				        	  //
				        	  }
				            else
				            {
				            	if(flag){
				            		objR.put("Related", id);
				            		ja.put(objR);
				            		objR=new JSONObject();
				            		flag=false;
				            	}
				            	
				            }
				            
				        	    
				          }   
				        
				        	//  obj.put("Related Publications", ja1);  
						System.out.println("************************************");
						//ja.put(obj);
				     } catch (IOException e) {
				            e.printStackTrace();
				     }
				    }
				}

				
				
			
		
	 
		br.close();
		 FileWriter file = new FileWriter("Scholar.json");
		 file.write(ja.toString());
		 file.close();
		
	
		
		
		/*BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
	     String line = null; 

	     try {
	        while ((line = input.readLine()) != null)
	            System.out.println(line);
	     } catch (IOException e) {
	            e.printStackTrace();
	     }*/
		
	}

}
