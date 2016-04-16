package assign2;


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


public class qualityscore {
	
	static JSONObject obj = new JSONObject();
	public static void main(final String[] args) throws IOException,TikaException, SAXException {
		
		
			String ROOT = "C:/Users/nupoorc/Documents/Masters/big data/599/";
		    FileVisitor<Path> fileProcessor = new ProcessFile();	
		    Files.walkFileTree(Paths.get(ROOT), fileProcessor);	
		    FileWriter fp = new FileWriter("C:/Users/nupoorc/Documents/finaljson.json", true);
		    fp.write(obj.toString());
		    fp.close();
		    
	}
	
	
	public static final class ProcessFile extends SimpleFileVisitor<Path> {
		@Override public FileVisitResult visitFile(
			      final Path aFile, BasicFileAttributes aAttrs
			    ) throws IOException {
			
		  
			
		  File file = new File(aFile.toString());
	      BodyContentHandler handler = new BodyContentHandler();
	      Metadata metadata = new Metadata();
	      FileInputStream inputstream = new FileInputStream(file);
	      ParseContext pcontext = new ParseContext();
	      
	      //parsing the document using PDF parser
	      AutoDetectParser autodetect = new AutoDetectParser(); 
	      try {
			autodetect.parse(inputstream, handler, metadata,pcontext);
		} catch (SAXException | TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
	      //getting the content of the document
	     // System.out.println("Contents of the PDF :" + handler.toString());
	      
	      //getting metadata of the document
	     //	 System.out.println("Metadata of the PDF:");
	      String[] metadataNames = metadata.names();
	      
	      int count = 2, dflag = 0, vflag = 0;
	      
	      CharSequence voc1 = "dc:";
	      CharSequence voc2 = "cp:";
	      CharSequence voc3 = "xmp:";
	      CharSequence voc4 = "lom:";
	      CharSequence voc5 = "xmpTPg:";
	      CharSequence voc6 = "dcterms:";
	      
	      /*for(String name : metadataNames) {
	    	 
	         System.out.println(name+ " : " + metadata.get(name));
	      }*/
	      
	      
	      
	      for(String name : metadataNames) {
	    	  if((name.equals("description") || name.equals("title") || name.equals("version") || name.equals("Author")) && dflag == 0)
	    	  {	 
	    		  count++;
	    		  dflag = 1;
	    	  }
	    	  if(name.equals("alias"))
	    		  count++;
		      if((name.contains(voc1) || name.contains(voc2) || name.contains(voc3) || name.contains(voc4) || name.contains(voc5) || name.contains(voc6)) && vflag == 0)
		      {
		    	  count++;
		    	  vflag = 1;
		      }
		      if(name.contains("license"))
		    		 count++;
		    	 
		      }
	      String name = file.getName().toString();
	      obj.put(name, count);
	      
	      System.out.println(file);
	      System.out.println("\n");
	      System.out.println(obj);
	      
	      return FileVisitResult.CONTINUE;
	   }
		
		
    }
	
	

}