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
connection = MongoClient("yourmongodbconnectionstring")
 
# connect to the students database and the ctec121 collection
db = connection.spentrack.collectionname
 
# # close the connection to MongoDB
# connection.close()

#open db

# insert new record 

#query by date (take userid)
def find_by_date(userid, date):
	for post in db.find({'user':userid, 'date':date}):
		pprint.pprint(post)

#query by shop name
def find_by_shopName(userid, shopname):
	for post in db.find({'user':userid, 'shop':shopname}):
		pprint.pprint(post)

#query by area ?

def get_all_spending(userid):
	for post in db.find({'user':userid}):
		pprint.pprint(post)

def insert_sepnding_record(userid,record):
	db.insert(record)

def close_db():
	# close the connection to MongoDB
	connection.close()

#close db