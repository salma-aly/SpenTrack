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


class SpenTrack(Resource):    
    def get(self):
        r = request.get_json()
        if 'notes' in r:
            id = validate_token(r['notes'])
            return(id)
        if 'id' in r and 'request_type' in r and r['request_type']=='spending_query':
            #result=get_multifield_spending(userid,r)
            #return result
            if 'shop_name' in r:
                print(r['shop_name'])
                return getSpending.find_by_shop_name(r['id'],r[shop_name])

            if 'category_name' in r:
                print(r['category_name'])
                return getSpending.find_by_shop_name(r['id'],r[shop_name])

                #call the find_by_shop_name_function
            elif 'date_from' in r  and 'date_to' in r and 'id' in r:
                print(r['date_from'] ,r['date_to'])
                return getSpending.find_by_date(r['id'],r['date_from'] ,r['date_to'])
                #call find_by_date
                # elif r['category']:
                #     # call find by category
                # else:
                #     # find all spending

        else:
            return "Unauthorized"
    def post(self):
        id = "121232334234534523543453452324545432234"
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
        getSpending.insert_spending_record(id,loads(result)) 
        return result
        
api.add_resource(SpenTrack, '/spentrack') # Route_1

if __name__ == '__main__':
    application.run()
