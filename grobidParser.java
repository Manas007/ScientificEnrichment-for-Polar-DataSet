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

import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mchange.v2.resourcepool.TimeoutException;

import assignment.count.ProcessFile;

public class grobidParser {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub

    String ROOT = "/Users/manasranjanmahanta/Desktop/599data/";	
    FileVisitor<Path> fileProcessor = new ProcessFile();	
    Files.walkFileTree(Paths.get(ROOT), fileProcessor);		
		
	}

	private static void callGrobidParser(final String Path) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		
		ExecutorService executor = Executors.newCachedThreadPool();
    	Callable<Object> task = new Callable<Object>() {
    	  
    		public Object call() {
    		
		
		Runtime rt = Runtime.getRuntime();
		Process pr = null;
		try {
			pr = rt.exec("java -classpath /Users/manasranjanmahanta/grobidparser-resources/:/Users/manasranjanmahanta/tika-app-1.12.jar org.apache.tika.cli.TikaCLI --config=/Users/manasranjanmahanta/grobidparser-resources/tika-config.xml -J "+Path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("java -classpath /Users/manasranjanmahanta/grobidparser-resources/:/Users/manasranjanmahanta/tika-app-1.12.jar org.apache.tika.cli.TikaCLI --config=/Users/manasranjanmahanta/grobidparser-resources/tika-config.xml -J "+Path);
		BufferedReader reader = 
                new BufferedReader(new InputStreamReader(pr.getInputStream()));

            String line = "";			
      try {
		while ((line = reader.readLine())!= null) {
		         System.out.println(line);
		          FileWriter file = new FileWriter("HW3Polar.txt",true);
		 		  file.write("\n"+Path+line+"\n\n");
		 		  file.close();
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
		
		return true;
    		}
    		
    	};
    	Future<Object> future = executor.submit(task);
    	try {
    	   Object result = future.get(3, TimeUnit.SECONDS); 
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
		        
		        if(!file.toString().contains("DS_Store"))
	    		try {
		        	
		        	
		        			parser.parse(inputstream, handler, metadata, context);
		 				   // System.out.println(aFile.toString()+metadata);
		 					//System.out.println(aFile.toString()+metadata.get("Content-Type"));
		 		        	String ContenType=metadata.get("Content-Type").split(";")[0];
		 		        	String encrypted=null;
		 		        	if((ContenType.equalsIgnoreCase("application/pdf"))){
		 		        	if(metadata.get("pdf:encrypted")!=null)
		 		        		encrypted=metadata.get("pdf:encrypted");
		 		        	if(!encrypted.equals("true"))
		 		        	{
		 		            System.out.println(metadata);
		 		           // System.out.println(handler.toString().trim());
		 		        	//||(ContenType.equalsIgnoreCase("text/plain"))||ContenType.equalsIgnoreCase("text/html"))
		 		        	
		 		        	  // System.out.println(aFile);
		 		        		  System.out.println(handler.toString().trim());
		 		        	      callGrobidParser(aFile.toString());
		 		        	   	 
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
