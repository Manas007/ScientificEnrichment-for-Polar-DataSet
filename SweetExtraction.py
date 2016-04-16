import os
count=0
with open("finalList.txt","r") as file:
	for line in file:
		filePath,mime=line.split(",")
		path="/users/sohilgogri/desktop/csci599/data"+filePath
		os.system('echo .'+path+' >> output.txt')
		os.system('java -Dner.impl.class=org.apache.tika.parser.ner.regex.RegexNERecogniser -classpath /Users/sohilgogri/Downloads/tika-1.12/tika-ner-resources/:/Users/sohilgogri/Downloads/tika-1.12/tika-app/target/tika-app-1.12.jar org.apache.tika.cli.TikaCLI --config=/Users/sohilgogri/Downloads/tika-1.12/tika-ner-resources/tika-config.xml -m '+path+'>> output.txt')
		count+=1
		print count