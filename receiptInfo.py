import io
import os
import re

# Imports the Google Cloud client library
from google.cloud import vision
from google.cloud.vision import types

#import the google places methods
from findPlace import getPlace, getPlaceInfo
import json
import datetime
import dateutil.parser

# image_path=os.path.abspath(r"C:\Users\Salma\Documents\CloudProject\SpenTrack\receipts\receipt13.jpg")

#change this path accordingly!
image_path=os.path.abspath(r"C:\Users\Ying-Chen\Documents\COEN424\SpenTrack\receipt23.jpg")

possible_total_list = []
receipt_info = {}

def is_total(number):
    if (re.search("[\.,][0-9]{2}", number)!= None): #to be a valid total, it must be in the form of ie. 40.00$ or 40,00$..not only 40 or 40, 40. or 40.1
    # if(("." in number) or ("," in number)): #to be a valid total, it must be in the form of ie. 40.00$ or 40,00$..not only 40
    #     print ("j: "+(number))
        number=number.replace("$","")
        number=number.replace(",",".")
        try:
            float(number)  #check if the string can be transformed into integer -> thus possible valid total amount found
            return True
        except ValueError:
            return False
        else:
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
        # print ("the words:"  + all_words[i])
        # if (all_words[i]=="total" or  all_words[i]=="total:" or all_words[i]=="sous-total" or all_words[i]=="subtotal"):
        if ("total" in all_words[i]):
            # print ("total word: "+(all_words[i]))
            total_index=i
            found_total_index = 1
            break  #stop searching after finding the first 'total' text
    total_value=0
    found=False #NOT being used usefully..

    if (found_total_index==1):
        for j in all_words[total_index:]: #starts looping from the next index of 'total_index' to the end of the array
            # print ("j: "+ j)
            if is_total(j):
                # print ("possible j: "+ j)
                found=True
                j=j.replace("$","")
                j = j.replace(",", ".")
                total_value=float(j)
                possible_total_list.append(total_value)


    # print("Total value is:",total_value)
    total_value = max(possible_total_list)
    # print("Total value is:",total_value)
    receipt_info['Total'] = total_value


    # support any combination of mm/dd/yyyy or mm-dd-yyyy . 2 more possibilies:  yyyy/dd/mm or yy/dd/mm.  (can switch dd and mm)
    # only length matters so with yy/dd/mm we also could have dd/mm/yy, dd/yy/mm..there is no distinction
    # 12 Jan(uary), 2017 or 12 Jan(uary). 2017
    # 12 Jan(uary) 2017
    # Nov(ember) 12 2017
    # Nov(ember 12, 2017 or  Nov(ember) 12. 2017
    # Nov(ember). 12, 2017 or Nov(ember). 12. 2017 or Nov(ember), 12, 2017
    reg_date="[0-9]{2}/[0-9]{2}/[0-9]{2}|[0-9]{2}/[0-9]{2}/[0-9]{4}|[0-9]{4}/[0-9]{2}/[0-9]{2}" \
             "|(?<![0-9])[0-9]{2}-[0-9]{2}-[0-9]{2}(?![0-9])|(?<![0-9])[0-9]{2}-[0-9]{2}-[0-9]{4}(?![0-9])|(?<![0-9])[0-9]{4}-[0-9]{2}-[0-9]{2}(?![0-9])" \
             "|\d{1,2}\s+(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember))[\.,]\s+\d{4}" \
             "|\d{1,2}\s+(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember))\s+\d{4}" \
             "|(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember))\s+\d{1,2}\s+\d{4}" \
             "|(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember))\s+\d{1,2}[\.,]\s+\d{4}" \
             "|(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember))[\.,]\s+\d{1,2}[\.,]\s+\d{4}"

    reg_zipcode="[A-Z][0-9][A-Z][-\s]?[0-9][A-Z][0-9]"
    date=re.search(reg_date, texts[0].description)
    postal_code=re.search(reg_zipcode, texts[0].description)
    shop_name=texts[0].description.split("\n")[0]

    receipt_info["Shop Name"]=shop_name
    if(date is not None):
    	# print ("Date of purchase is : ",date.group(0))
        #convert date found on receipt to yyyy-mm-dd format
        formatted_date = dateutil.parser.parse(date.group(0)).strftime('%Y/%m/%d')
        print (dateutil.parser.parse(date.group(0)).date())
        receipt_info["Date"]=formatted_date

    if (postal_code is not None):
        # print ("Store postal code is : ",postal_code.group(0))
        receipt_info["Postal Code"] = postal_code.group(0)
    else:
        receipt_info["Postal Code"] = ""


    # print ("Store name is : ",shop_name)

    # print(receipt_info)

    return receipt_info

#MAIN execution --------------------------------------------------------------------------------------------------

#process receipt using google vision api to obtain a DICT containing: total, shop_name, date, postal_code
def main(path):
    new_receipt= detect_text(path)
    print ("Dictionary from Receipt - vision api: ")
    print (new_receipt)
    print ("\n")
    #total, shop_name, date, postal_code
    # print (new_receipt['total'])

    #process receipt data using google places api to obtain precise and more information about the place

    # if(new_receipt["postal_code"]!=""):
    query_result = getPlace(new_receipt['Shop Name'], new_receipt["Postal Code"])
    # else:



    placesInfoDict = getPlaceInfo(query_result)
    print ("Dict from Place - places api: ")
    print(placesInfoDict)
    print ("\n")

    if (placesInfoDict!=None):

        #Obtain final JSON with spending and place data
        #only get total and date from result of vision API dict
        new_receipt.pop('Shop Name', None)
        new_receipt.pop('Postal Code', None)
        # print (new_receipt)

        new_receipt.update(placesInfoDict) #appends the places data to the remaining receipt dict data
        receipt_and_place_data_JSON = json.dumps(new_receipt) #Transform final dict with data to store to db to JSON
        print ("JSON of data combined to be stored to db: ")
        return (json.dumps(new_receipt,ensure_ascii=False))
    else:
        new_receipt.pop('Postal Code', None)
        receipt_and_place_data_JSON = json.dumps(new_receipt)  # Transform final dict with data to store to db to JSON
        print ("JSON of data combined to be stored to db: ")
        return (json.dumps(new_receipt,ensure_ascii=False))
