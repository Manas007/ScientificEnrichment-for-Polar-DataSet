Instructions:

i)place tika-ner-resources in tika folder, and run measurement parser.java
ii)Run grobidParser.java to generate tei.xml files which is combined into Grobidtei.txt
iii)Run ScholarParser.java to take input from Grobidtei.txt and generate scholar.json
iv)Run GeoTopic.java to generate GeoTopic.json 
v)Run SweetExtraction.py to generate Ontologies.json
vi)Run qualityscore.java 
vii)Schema.xml is provided for solr indexing 
viii)Run similarity.py to generate cluster-d3.html





Contributions:

Sohil Gogri : Tag Ratio , Solr Indexing ,TIKA Similiarity
Anuj Shah : URL shortener , SWEET ontology , FFMPEG Parser
Manas Mahanta : Grobid & Scholar Parser,GeoTopic Parser,Memex GeoParser ,Solr Indexing 
Nupoor Chavan : Metadata Quality Score , D3 Visualizations
