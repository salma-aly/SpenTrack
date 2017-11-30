import pymongo
from flask import Flask, request
from flask_restful import Resource, Api
import json
from json import dumps, load, loads
from  flask_jsonpify import jsonify
from google.cloud import vision
from google.cloud.vision import types
import io
from werkzeug.utils import secure_filename
import os
from google.oauth2 import id_token
from google.auth.transport import requests
from receiptInfo import main
from getSpending import find_by_shop_name 
import datetime

dbclient = pymongo.MongoClient('35.196.76.140',27017,connect=False)
application = Flask(__name__)
api = Api(application)
UPLOAD_FOLDER = '/opt/app/SpenTrack/uploads'
ALLOWED_EXTENSIONS = set(['txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif'])
application.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

def validate_token(token):
    try:
        print("test")
        idinfo = id_token.verify_oauth2_token(token, requests.Request(), os.environ['CLIENT_ID'])

        # Or, if multiple clients access the backend server:
        # idinfo = id_token.verify_oauth2_token(token, requests.Request())
        # if idinfo['aud'] not in [CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3]:
        #     raise ValueError('Could not verify audience.')

        if idinfo['iss'] not in ['accounts.google.com', 'https://accounts.google.com']:
            raise ValueError('Wrong issuer.')

        # If auth request is from a G Suite domain:
        # if idinfo['hd'] != GSUITE_DOMAIN_NAME:
        #     raise ValueError('Wrong hosted domain.')

        # ID token is valid. Get the user's Google Account ID from the decoded token.
        userid = idinfo['sub']
        return userid
    except ValueError:
        return "Invalid token"

def get_date_object(date_string):
    #11\/28\/17
    datearray=date_string.split("\/")
    day=int(datearray[1])
    month=int(datearray[0])
    year=int('20'+datearray[2])
    return datetime.date(year,month,day)

class SpenTrack(Resource):    
    def get(self):
        r = request.get_json()
        if r['notes']:
            id = validate_token(r['notes'])
            return(id)
        if r['request_type']=='spending_query':
            #result=get_multifield_spending(userid,r)
            #return result
            if r['shop_name']:
                print(r['shop_name'])
                return getSpending.find_by_shop_name(r['id'],r['shop_name'])

            if r['category_name']:
                print(r['category_name'])
                return getSpending.find_by_shop_name(r['id'],r['shop_name'])

                #call the find_by_shop_name_function
            elif r['date_from'] and r['date_to']:
                print(r['date_from'] ,r['date_to'])
                date_f=get_date_object(r['date_from'])
                date_t=get_date_object(r['date_to'])
                return getSpending.find_by_shop_name(r['id'],date_f,date_t)
                #call find_by_date
                # elif r['category']:
                #     # call find by category
                # else:
                #     # find all spending

        else:
            return "Unauthorized"

        
    def post(self):
        file = request.files['media']
        if file:
            filename = secure_filename(file.filename)
            file.save(os.path.join(application.config['UPLOAD_FOLDER'], filename))
        client = vision.ImageAnnotatorClient()
        print(filename)
        result =  main('uploads/' + filename)
        #db = dbclient['test']
        #collection = db['test_collection']
        #v = { str(x['_id']):x for x in collection.find()}
        #print (v)
        return result

api.add_resource(SpenTrack, '/spentrack') # Route_1

if __name__ == '__main__':
    application.run()
