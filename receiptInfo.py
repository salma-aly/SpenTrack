import io
import os
import re

# Imports the Google Cloud client library
from google.cloud import vision
from google.cloud.vision import types

#import the google places methods
from findPlace import getPlace, getPlaceInfo


# image_path=os.path.abspath(r"C:\Users\Salma\Documents\CloudProject\SpenTrack\receipts\receipt13.jpg")

#change this path accordingly!
image_path=os.path.abspath(r"C:\Users\Ying-Chen\Documents\COEN424\SpenTrack\receipt4.jpg")

def is_total(number):
	number=number.replace("$","")
	try:
		float(number)
		return True
	except ValueError:
		return False
    	

def detect_text(path):

    """Detects text in the file."""
    receipt_info={}
    client = vision.ImageAnnotatorClient()

    with io.open(path, 'rb') as image_file:
        content = image_file.read()

    image = types.Image(content=content)

    response = client.text_detection(image=image)
    texts = response.text_annotations
    # print(texts[0].description)
    # print(texts[0].description.lower().split())
    all_words=texts[0].description.lower().split()
    total_index=0
    for i in range(len(all_words)):
    	if all_words[i]=="total":
    		total_index=i

    total_value=0
    found=False

    for j in all_words[total_index:]:
    	if is_total(j):
    		found=True
    		j=j.replace("$","")
    		total_value=float(j)
    		print("Total value is:",total_value)
    		receipt_info['total']=total_value
    		break

    reg_date="[0-9]{2}/[0-9]{2}/[0-9]{2}|[0-9]{2}/[0-9]{2}/[0-9]{4}|[0-9]{4}/[0-9]{2}/[0-9]{2}"
    reg_zipcode="[A-Z][0-9][A-Z][-\s]?[0-9][A-Z][0-9]"
    date=re.search(reg_date, texts[0].description)
    postal_code=re.search(reg_zipcode, texts[0].description)
    shop_name=texts[0].description.split("\n")[0]

    receipt_info["shop_name"]=shop_name
    if(date):
    	print ("Date of purchase is : ",date.group(0))
    	receipt_info["date"]=date.group(0)

    if(postal_code):
    	print ("Store postal code is : ",postal_code.group(0))
    	receipt_info["postal_code"]=postal_code.group(0)

    print ("Store name is : ",shop_name)

    print(receipt_info)

    return receipt_info

#MAIN execution

#process receipt using google vision api to obtain a JSON containing: total, shop_name, date, postal_code
new_receipt= detect_text(image_path)

#total, shop_name, date, postal_code
# print (new_receipt['total'])

#process receipt data using google places api to obtain precise and more information about the place
query_result = getPlace(new_receipt['shop_name'], new_receipt['postal_code'])
getPlaceInfo(query_result)