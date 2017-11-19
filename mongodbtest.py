import pymongo
from flask import Flask, request
from flask_restful import Resource, Api
from json import dumps, load, loads
from  flask_jsonpify import jsonify
from google.cloud import vision
from google.cloud.vision import types
import io

client = pymongo.MongoClient('35.196.76.140',27017,connect=False)
application = Flask(__name__)
api = Api(application)

class SpenTrack(Resource):
    def get(self):
        print("sup")

    def post(self):

        data = request.get_data()
        client = vision.ImageAnnotatorClient()

        with io.open(data, 'rb') as image_file:
            content = image_file.read()

        image = types.Image(content=content)

        response = client.text_detection(image=image)
        texts = response.text_annotations

        print(texts)


        data = request.get_json()
        db = client['test']
        collection = db['test_collection']
        test = {"name": data['name'],
            "sup": data['sup']}
        id = collection.insert_one(test).inserted_id
        v = { str(x['_id']):x for x in collection.find()}
        print (v)
        return "hey"

api.add_resource(SpenTrack, '/spentrack') # Route_1

if __name__ == '__main__':
    application.run(host='0.0.0.0')