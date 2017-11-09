import pymongo
from flask import Flask, request
from flask_restful import Resource, Api
from json import dumps, load, loads
from  flask_jsonpify import jsonify

client = pymongo.MongoClient('35.196.76.140',27017,connect=False)
app = Flask(__name__)
api = Api(app)

class SpenTrack(Resource):
    def get(self):
        pass

    def post(self):
        data = request.get_json()
        db = client['test']
        collection = db['test_collection']
        test = {"name": data['name'],
            "sup": data['sup']}
        id = collection.insert_one(test).inserted_id
        return [x for x in collection.find()]

api.add_resource(SpenTrack, '/spentrack') # Route_1
