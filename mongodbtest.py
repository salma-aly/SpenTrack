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
#from getSpending import find_by_shop_name, insert_spending_record
import getSpending
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
    date_string = str(date_string)
    datearray=date_string.split("/")
    day=int(datearray[1])
    month=int(datearray[0])
    year=int('20'+datearray[2])
    return datetime.datetime(year,month,day,0,0)

def date_handler(x):
    if isinstance(x,datetime.datetime):
        return x.strftime("%Y/%m/%d")

class SpenTrack(Resource):    
    def get(self):
        r = request.get_json()
        if 'notes' in r:
            id = validate_token(r['notes'])
            return(id)
        if 'id' in r and 'request_type' in r and r['request_type']=='spending_query':
            #result=get_multifield_spending(userid,r)
            #return result
            id = str(r['id']
            if 'shop_name' in r:
                print(r['shop_name'])
                return getSpending.find_by_shop_name(id,r['shop_name'])

            if 'category_name' in r:
                print(r['category_name'])
                return getSpending.find_by_category_name(id,r['category_name'])

                #call the find_by_shop_name_function
            elif 'date_from' in r  and 'date_to' in r and 'id' in r:
                print(r['date_from'] ,r['date_to'])
                date_f=get_date_object(r['date_from'])
                date_t=get_date_object(r['date_to'])
                print (date_f)
                get = getSpending.find_by_date(id),date_f ,date_t) 
                date_conv =  dumps(get,default=date_handler)
                result = loads(date_conv)
                return result
                #call find_by_date
                # elif r['category']:
                #     # call find by category
                # else:
                #     # find all spending

        else:
            return "Unauthorized"
    def post(self):
        id = "1"
       # r = request.get_data()
       # if r['notes']:
       #     id = validate_token(r['notes'])
       # else:
       #     return "Unauthorized"
        file = request.files['media']
        if file:
            filename = secure_filename(file.filename)
            file.save(os.path.join(application.config['UPLOAD_FOLDER'], filename))
        client = vision.ImageAnnotatorClient()
        print(filename)
        result =  main('uploads/' + filename)
        #for key,value in result.items():
        #    if key == 'Date':
        #        tmp = value.split('/')
        #        date = datetime.date(int(y[0]),int(y[1]),int(y[2]))
        #        result[key] = date
        getSpending.insert_spending_record(id,loads(result))
        #return dumps(result,default=date_handler)
        return result
api.add_resource(SpenTrack, '/spentrack') # Route_1

if __name__ == '__main__':
    application.run()
