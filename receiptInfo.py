import io
import os
import re

# Imports the Google Cloud client library
from google.cloud import vision
from google.cloud.vision import types

#import the google places methods
from findPlace import getPlace, getPlaceInfo
import json

# image_path=os.path.abspath(r"C:\Users\Salma\Documents\CloudProject\SpenTrack\receipts\receipt13.jpg")

#change this path accordingly!
image_path=os.path.abspath(r"C:\Users\Ying-Chen\Documents\COEN424\SpenTrack\receipt22.jpg")

def is_total(number):
        number=number.replace("$","")
        number=number.replace(",",".")
        try:
            # print(number)
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
    found_total_index =0;
    for i in range(len(all_words)):
    	if all_words[i]==("total" or "total:"):
            total_index=i
            found_total_index = 1;
            break;  #stop searching after finding the first 'total' text
    total_value=0
    found=False #NOT being used usefully..

    if (found_total_index==1):
        for j in all_words[total_index:]: #starts looping from the next index of 'total_index' to the end of the array
            # print ("j: "+ j)
            if is_total(j):
                found=True
                j=j.replace("$","")
                j = j.replace(",", ".")
                total_value=float(j)
                # print("Total value is:",total_value)
                receipt_info['total']=total_value
                break

    # support any combination of dd/mm/yyyy or dd-mm-yyyy . 2 more possibilies:  yyyy/dd/mm or yy/dd/mm.
    # only length matters so with yy/dd/mm we also could have dd/mm/yy, dd/yy/mm..there is no distinction
    # 12 Jan(uary), 2017 or 12 Jan(uary). 2017
    # 12 Jan(uary) 2017
    # Nov(ember). 12 2017 Nov(ember), 12 2017
    reg_date="[0-9]{2}/[0-9]{2}/[0-9]{2}|[0-9]{2}/[0-9]{2}/[0-9]{4}|[0-9]{4}/[0-9]{2}/[0-9]{2}" \
             "|[0-9]{2}-[0-9]{2}-[0-9]{2}|[0-9]{2}-[0-9]{2}/[0-9]{4}|[0-9]{4}-[0-9]{2}/[0-9]{2}" \
             "|\d{1,2}\s+(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember))[\.,]\s+\d{4}" \
             "|\d{1,2}\s+(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember))\s+\d{4}" \
             "|(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember))\s+\d{1,2}\s+\d{4}"
    reg_zipcode="[A-Z][0-9][A-Z][-\s]?[0-9][A-Z][0-9]"
    date=re.search(reg_date, texts[0].description)
    postal_code=re.search(reg_zipcode, texts[0].description)
    shop_name=texts[0].description.split("\n")[0]

    receipt_info["shop_name"]=shop_name
    if(date):
    	# print ("Date of purchase is : ",date.group(0))
    	receipt_info["date"]=date.group(0)

    if(postal_code):
    	# print ("Store postal code is : ",postal_code.group(0))
    	receipt_info["postal_code"]=postal_code.group(0)

    # print ("Store name is : ",shop_name)

    # print(receipt_info)

    return receipt_info

#MAIN execution

#process receipt using google vision api to obtain a DICT containing: total, shop_name, date, postal_code
new_receipt= detect_text(image_path)
print ("Dict from Receipt - vision api: ")
print (new_receipt)
print ("\n")
#total, shop_name, date, postal_code
# print (new_receipt['total'])

#process receipt data using google places api to obtain precise and more information about the place
query_result = getPlace(new_receipt['shop_name'], new_receipt['postal_code'])
placesInfoDict = getPlaceInfo(query_result)
print ("Dict from Place - places api: ")
print(placesInfoDict)
print ("\n")

#Obtain final JSON with spending and place data
#only get total and date from result of vision API dict
new_receipt.pop('shop_name', None)
new_receipt.pop('postal_code', None)
# print (new_receipt)

new_receipt.update(placesInfoDict) #appends the places data to the remaining receipt dict data
receipt_and_place_data_JSON = json.dumps(new_receipt) #Transform final dict with data to store to db to JSON
print ("JSON of data combined to be stored to db: ")
print (json.dumps(new_receipt, indent=4, ensure_ascii=False))
