from urllib2 import *
import simplejson
import difflib
import math
import csv
import itertools
ids=[]



connection = urlopen('http://localhost:8983/solr/PolarDump/select?q=*:*&fl=id&wt=json')
response = simplejson.load(connection)
totalRows=response['response']['numFound']
rows=100
start=0
while(start)<=totalRows:
	connection = urlopen('http://localhost:8983/solr/PolarDump/select?q=*:*&fl=id&wt=json&rows='+str(rows)+'&start='+str(start))
	response=simplejson.load(connection)
	for vals in response["response"]["docs"]:
		ids.append(vals["id"].split(":")[-1])
	'''
	connection = urlopen('http://localhost:8983/solr/PolarDump/select?q=*:*&fl=id&wt=json&rows=20&start=0')
	response=simplejson.load(connection)
	for vals in response["response"]["docs"]:
		ids.append(vals["id"].split(":")[-1])
	'''
	files_tuple = itertools.combinations(ids, 2)

	with open("simil.csv", "wb") as outF:
		a = csv.writer(outF, delimiter=',')
		a.writerow(["x-coordinate","y-coordinate","Similarity_score"])

		for file1,file2 in files_tuple:
			rowSim=[file1,file2]

			item1={}
			item2={}
			connection=urlopen('http://localhost:8983/solr/PolarDump/select?q=id:"'+file1+'"&fl=id,ontology,Related,Citations,measurement,Geographic_LONGITUDE,Geographic_LATITUDE&wt=json')
			response=simplejson.load(connection)
			for vals in response["response"]["docs"]:
				for va in vals:
					item1[va]=vals[va]
			connection=urlopen('http://localhost:8983/solr/PolarDump/select?q=id:"'+file2+'"&fl=id,ontology,Related,Citations,measurement,Geographic_LONGITUDE,Geographic_LATITUDE&wt=json')
			response=simplejson.load(connection)
			for vals in response["response"]["docs"]:
				for va in vals:
					item2[va]=vals[va]
			features1=[]
			features2=[]
			features=[]
			for i in item1:
				if ((item1[i]!=[u'']) and (i!='id')):
					features1.append(i)
			for i in item2:
				if ((item2[i]!=[u'']) and (i!='id')):
					features2.append(i)	
			features=list(set(features1).intersection(features2))
			similarity=0.0

			for feature in features:
				file1Value=item1[feature]
				file2Value=item2[feature]
				countf=0.0

				if(feature=="ontology"):
					sim=0.0
					tot=0.0
					for i in file1Value:
						if(i in file2Value):
							sim+=1
						tot+=1
					for i in file2Value:
						if(i in file1Value):
							sim+=1
						tot+=1
					similarity+=(sim/tot)
					countf+=1

				if(feature=="measurement"):
					file1Meas={}
					file2Meas={}
					f1k=[]
					f2k=[]
					for i in file1Value:
						num=i.split(" ")[0]
						unit=i.split(" ")[-1]
						if not(unit in file1Meas):
							file1Meas[unit]=[num]
						else:
							file1Meas[unit].append(num)

					for i in file2Value:
						num=i.split(" ")[0]
						unit=i.split(" ")[-1]
						if not(unit in file2Meas):
							file2Meas[unit]=[num]
						else:
							file2Meas[unit].append(num)
					for i in file1Meas:
						f1k.append(i)
					for i in file2Meas:
						f2k.append(i)
					fk=list(set(f1k).intersection(f2k))
					similar=0.0
					cnt=0.0
					for i in fk:
						sim=difflib.SequenceMatcher(None,file1Meas[i],file2Meas[i])
						similar+=sim.ratio()
						cnt+=1
					if(cnt!=0):
						similarity+=(similar/cnt)
						countf+=1

				if(feature=="Geographic_LATITUDE"):
					glong1=float(item1["Geographic_LONGITUDE"][0])
					glong2=float(item2["Geographic_LONGITUDE"][0])
					glat1=float(item1["Geographic_LATITUDE"][0])
					glat2=float(item2["Geographic_LATITUDE"][0])
					dist=math.sqrt(((glat2-glat1)*(glat2-glat1))+((glong2-glong1)*(glong2-glong1)))
					if(dist<=500):
						similarity+=1
					elif(dist>500 and dist<=2500):
						similarity+=0.75
					elif(dist>2500 and dist<=5000):
						similarity+=0.5
					elif(dist>5000 and dist<=7500):
						similarity+=0.25
					countf+=1
				if(countf!=0):
					similarity/=countf
			if(similarity>=0.8):
				rowSim.append(similarity)
				a.writerow(rowSim)
	start+=rows

