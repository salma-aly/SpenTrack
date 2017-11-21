import pymongo
from flask import Flask, request
from flask_restful import Resource, Api
from json import dumps, load, loads
from  flask_jsonpify import jsonify
from google.cloud import vision
from google.cloud.vision import types
import io
from werkzeug.utils import secure_filename
import os
dbclient = pymongo.MongoClient('35.196.76.140',27017,connect=False)
application = Flask(__name__)
api = Api(application)
UPLOAD_FOLDER = '/opt/app/SpenTrack/uploads'
ALLOWED_EXTENSIONS = set(['txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif'])
application.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

class SpenTrack(Resource):
    def get(self):
        return("sup")

    def post(self):
        print("supp")
        file = request.files['media']
        if file:
            filename = secure_filename(file.filename)
            file.save(os.path.join(application.config['UPLOAD_FOLDER'], filename))
        client = vision.ImageAnnotatorClient()
        print(filename)
        with io.open('uploads/' + filename, 'rb') as image_file:
            content = image_file.read()

        image = types.Image(content=content)

        response = client.text_detection(image=image)
        texts = response.text_annotations
        print(texts)
        db = dbclient['test']
        collection = db['test_collection']
        v = { str(x['_id']):x for x in collection.find()}
        print (v)
        return {'value': texts}

api.add_resource(SpenTrack, '/spentrack') # Route_1

if __name__ == '__main__':
    application.run(host='0.0.0.0')
