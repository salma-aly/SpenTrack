import io
import os
import re
import pymongo

# Imports the Google Cloud client library
from google.cloud import vision
from google.cloud.vision import types

#connect to db ?

# mongo_hello_world.py
# Author: Bruce Elgort
# Date: March 18, 2014
# Purpose: To demonstrate how to use Python to
# 1) Connect to a MongoDB document collection
# 2) Insert a document
# 3) Display all of the documents in a collection</code>
 
from pymongo import MongoClient
 
# connect to the MongoDB on MongoLab
# to learn more about MongoLab visit http://www.mongolab.com
# replace the "" in the line below with your MongoLab connection string
# you can also use a local MongoDB instance

dbclient=pymongo.MongoClient('35.196.76.140',27017,connect=False)
db=dbclient['spentrack']

# connect to the students database and the ctec121 collection
 
# # close the connection to MongoDB
# connection.close()

#open db

# insert new record 

#query by date (take userid)
def find_by_date(userid, date_from,date_to):
	values=[]
	# 3cases
	for post in db[userid].find({"date": {"$lt": date_to, "$gt":date_from}}):
		values.append(post)
		pprint.pprint(post)
	return values

#query by shop name
def find_by_shopName(userid, shopname):
	values=[]
	for post in db[userid].find({'shop':shopname}):
		values.append(post)
		pprint.pprint(post)
	return values

def find_by_categoryName(userid, categoryname):
	values=[]
	for post in db[userid].find({'user':userid, 'category':categoryname}):
		values.append(post)
		pprint.pprint(post)
	return values

def find_by_date_and_categoryName(userid, categoryname):
	values=[]
	for post in db[userid].find({'user':userid, ,"date": {"$lt": date_to, "$gt":date_from},'category':categoryname}):
		values.append(post)
		pprint.pprint(post)
	return values

def find_by_date_and_shopName(userid,shopname):
	values=[]
	for post in db[userid].find({'user':userid, ,"date": {"$lt": date_to, "$gt":date_from},'shop':shopname}):
		values.append(post)
		pprint.pprint(post)
	return values

#query by area ?
#query by category ?

def get_all_spending(userid):
	values=[]
	for post in db[userid].find({'user':userid}):
		values.append(post)
		pprint.pprint(post)
	return values

# def get_multifield_spending(userid, params):
# 	values=[]
# 	if(params["date_to"]and params["date_from"]and params["shopname"]):
# 		# db.collection.find( { field: { $gt: value1, $lt: value2 } } );
# 		# db.collection.find({"foo":{"$ne":"bar", "$exists":true}})
# 		for post in db.find({"date": {"$lt": params["date_to"], "$gt":params["date_from"]}, "shop":params["shopname"]})
# 		values.append(post)
# 		pprint.pprint(post)
# 	elif (params["date_to"]and params["date_from"]):
# 		for post in db.find({"date": {"$lt": params["date_to"], "$gt":params["date_from"]}})
# 		values.append(post)
# 		pprint.pprint(post)
# 	elif(params["shopname"]):
# 		for post in db.find({'user':userid, 'shop':shopname}):
# 		values.append(post)
# 		pprint.pprint(post)
# 	else:
# 		for post in db.find({'user':userid}):
# 		values.append(post)
# 		pprint.pprint(post)
# 	return values


def insert_sepnding_record(userid,record):
	collection=db[userid]
	collection.insert(record)




def close_db():
	# close the connection to MongoDB
	connection.close()

#close db