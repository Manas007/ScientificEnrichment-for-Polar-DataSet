package ContentDetection;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mchange.v2.resourcepool.TimeoutException;

//import assignment.count.ProcessFile;

public class GeoTopic {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 * 
	 */
	static JSONArray ja=new JSONArray(); 
	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
 
      
    String ROOT = "/Users/manasranjanmahanta/Desktop/599data/";	
    FileVisitor<Path> fileProcessor = new ProcessFile();	
    Files.walkFileTree(Paths.get(ROOT), fileProcessor);		
    FileWriter file = new FileWriter("GeoTopicJA.json");
	 file.write(ja.toString());
	 file.close();
    
	}

	private static JSONObject callGrobidParser(final String Path) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		
		/*ExecutorService executor = Executors.newCachedThreadPool();
    	Callable<Object> task = new Callable<Object>() {
    	  
    		public Object call() {*/
    		JSONObject jo=new JSONObject();
		
		Runtime rt = Runtime.getRuntime();
		Process pr = null;
		try {
			pr = rt.exec("java  -Dner.corenlp.model=edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz        -Dner.impl.class=org.apache.tika.parser.ner.corenlp.CoreNLPNERecogniser        -classpath /Users/manasranjanmahanta/tika-app-1.12.jar:/Users/manasranjanmahanta/tika-ner-corenlp/target/tika-ner-corenlp-addon-1.0-SNAPSHOT-jar-with-dependencies.jar org.apache.tika.cli.TikaCLI        --config=/Users/manasranjanmahanta/tika-config.xml -m "+Path);
			   		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("java  -Dner.corenlp.model=edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz        -Dner.impl.class=org.apache.tika.parser.ner.corenlp.CoreNLPNERecogniser        -classpath /Users/manasranjanmahanta/tika-app-1.12.jar:/Users/manasranjanmahanta/tika-ner-corenlp/target/tika-ner-corenlp-addon-1.0-SNAPSHOT-jar-with-dependencies.jar org.apache.tika.cli.TikaCLI        --config=/Users/manasranjanmahanta/tika-config.xml -m "+Path);
		BufferedReader reader = 
                new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line = "";	
        FileWriter file = new FileWriter("HW3PolarGeoTopic.txt",true);     
        
      try {
    	  int count=0;
		while ((line = reader.readLine())!= null) {
		        // System.out.println(line);
			 
		         if(line.contains("NER_LOCATION")&&count<=2){
		        	 count++;
		        	 String location=line.split(":")[1];
		        	 Runtime lucenert = Runtime.getRuntime();
		        	 Process lucenep=null;
		        	 try {
		        		 lucenep = lucenert.exec("/Users/manasranjanmahanta/lucene-geo-gazetteer/src/main/bin/lucene-geo-gazetteer -s "+location+"  -json");
		     			   		} catch (IOException e) {
		     			// TODO Auto-generated catch block
		     			e.printStackTrace();
		     		}
		        	 BufferedReader reader1 = 
		                     new BufferedReader(new InputStreamReader(lucenep.getInputStream()));
		        	 String line1=null;
		        	 
		        	 while((line1=reader1.readLine())!=null){
		        		 file.write(line1+"\n");
		        	 System.out.println(line1);
		        		
		        		String tempLine=line1.split(":",2)[0];
		        		Pattern patternLocation = Pattern.compile("\\{\"(.*)\"");
		        		boolean locationFlag=false;
		        		String locationName=null;
		         		 Matcher matcherlocation = patternLocation.matcher(tempLine);
		         		 if (matcherlocation.find())
		         		 {
		         			locationFlag=true;
		        		    System.out.println(matcherlocation.group(1));
		        		    locationName=matcherlocation.group(1);
		         		 
		        		 JSONObject jObject  = new JSONObject(line1); // json
		        		 JSONArray data = jObject.getJSONArray(locationName); 
		        		 for (int i = 0; i < data.length(); i++) {
		        			 if(count==1){
		        			 JSONObject tempobj=data.getJSONObject(i);
		        			 jo.put("Geographic_NAME",tempobj.get("name").toString());
		        			 jo.put("Geographic_LATITUDE",tempobj.get("latitude").toString());
		        			 jo.put("Geographic_LONGITUDE",tempobj.get("longitude").toString());
		        			 System.out.println("Geographic_NAME:"+tempobj.get("name"));
		        			 System.out.println("Geographic_LATITUDE:"+tempobj.get("latitude"));
		        			 System.out.println("Geographic_LONGITUDE:"+tempobj.get("longitude")); 
		        			 }
		        			 if(count==2){
			        			 JSONObject tempobj=data.getJSONObject(i);
			        			 jo.put("Optional_NAME",tempobj.get("name").toString());
			        			 jo.put("Optional_LATITUDE",tempobj.get("latitude").toString());
			        			 jo.put("Optional_LONGITUDE",tempobj.get("longitude").toString());
			        			 System.out.println("Optional_NAME:"+tempobj.get("name"));
			        			 System.out.println("Optional_LATITUDE:"+tempobj.get("latitude"));
			        			 System.out.println("Optional_LONGITUDE:"+tempobj.get("longitude"));
			        			 }
		        			} 		 
		         		 }
		        	 }
		        	 
		        	 
		        	 
		        	 
		        	 
		         }
		       
		 		  
}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
		try {
			pr.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*return true;
    		}
    		
    	};
    	Future<Object> future = executor.submit(task);
    	try {
    	   Object result = future.get(1, TimeUnit.SECONDS); 
    	} catch (InterruptedException e) {
    	   // handle the interrupts
    	} catch (ExecutionException e) {
    	   // handle other exceptions
    	} catch (java.util.concurrent.TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
    	   future.cancel(false); // may or may not desire this
    	}
		*/
		file.close();
	//	id=line.split("\\[")[0].split("Desktop/599data")[1];
		jo.put("id",Path.split("Desktop/599data")[1]);
		return jo;
	}
	public static final class ProcessFile extends SimpleFileVisitor<Path> {
		@Override public FileVisitResult visitFile(
			      final Path aFile, BasicFileAttributes aAttrs
			    ) throws IOException {
		
			 File file = new File(aFile.toString());
	         //System.out.println(aFile.toString());	
		        //Parser method parameters
		        final Parser parser = new AutoDetectParser();
		        final BodyContentHandler handler = new BodyContentHandler();
		        final Metadata metadata = new Metadata();
		        final FileInputStream inputstream = new FileInputStream(file);
		        final ParseContext context = new ParseContext();
		        System.out.println(file.toString());
		          
		        
	    		try {
		        	
		        	
		        			parser.parse(inputstream, handler, metadata, context);
		 				   // System.out.println(aFile.toString()+metadata);
		 					//System.out.println(aFile.toString()+metadata.get("Content-Type"));
		 		        	String ContenType=metadata.get("Content-Type").split(";")[0];
		 		        	String encrypted=null;
		 		        	if((ContenType.equalsIgnoreCase("application/pdf"))||(ContenType.equalsIgnoreCase("application/xhtml+xml"))||(ContenType.equalsIgnoreCase("text/html"))||(ContenType.equalsIgnoreCase("text/plain"))){
		 		        	if(!ContenType.equalsIgnoreCase("application/pdf")){
		 		                
		 		        		JSONObject tempj=new JSONObject ();
		 		        		tempj=(callGrobidParser(aFile.toString()));
		 		        		FileWriter file1 = new FileWriter("GeoTopic.json",true);
		 		        		file1.write(tempj.toString()+",");
		 		        		file1.close();
		 		        		
		 		        	}
		 		     		
		 		            if(metadata.get("pdf:encrypted")!=null)
		 		        		encrypted=metadata.get("pdf:encrypted");
		 		        	if(ContenType.equalsIgnoreCase("application/pdf")&&!encrypted.equals("true"))
		 		        	{
		 		            // System.out.println(metadata);
		 		           // System.out.println(handler.toString().trim());
		 		        	//||(ContenType.equalsIgnoreCase("text/plain"))||ContenType.equalsIgnoreCase("text/html"))
		 		        	
		 		        	  // System.out.println(aFile);
		 		        		//  System.out.println(handler.toString().trim());
		 		        		JSONObject tempk=new JSONObject ();
		 		        		tempk=(callGrobidParser(aFile.toString()));
		 		        		FileWriter file1 = new FileWriter("GeoTopic.json",true);
		 		        		file1.write(tempk.toString()+",");
		 		        		file1.close();
		 		        	   	 
		 		        	}
		 		        	}
		 		        	
		 		        	
		 		        }catch (SAXException e) {
		 	 				// TODO Auto-generated catch block
		 	 				//e.printStackTrace();
		 	 			} catch (TikaException e) {
		 	 				// TODO Auto-generated catch block
		 	 				//e.printStackTrace();
		 	 			} catch (InterruptedException e) {
		 					// TODO Auto-generated catch block
		 					e.printStackTrace();
		 				}
		 		        catch(Exception e){
		 		        	e.printStackTrace();
		 		        
		        		}
						
		        	
					
	 	      return FileVisitResult.CONTINUE;
			
		
		}
		@Override  public FileVisitResult preVisitDirectory(
			      Path aDir, BasicFileAttributes aAttrs
			    ) throws IOException {
			     // System.out.println("Processing directory:" + aDir);
			      return FileVisitResult.CONTINUE;
			    }
	
		
	}

}
