from googleplaces import GooglePlaces , types, json#, lang
import json

# YOUR_API_KEY = 'INSERT KEY HERE'

google_places = GooglePlaces(YOUR_API_KEY)

# You may prefer to use the text_search API, instead.

#Returns googleplaces.GooglePlacesSearchResult
#GooglePlacesSearchResult is a LIST!
def getPlace(shop_name, postal_code):
    query_result = google_places.nearby_search(
            location="montreal,"+postal_code, #need location input #could specify ,QC
            keyword=shop_name)#'Bonjour Supernarche') #typo in street name still works
    '''
    location: montreal
    examples of keyword: 2087 Saint-Catherine St -> can give an address
                           Bonjour Supernarche  -> so can give exact shop name 
                            Sport Experts -> will find you all the sport experts shops 
                            H3H 1M6 -> gives a list of places for some reason.. 
                            
    '''
    return query_result


# If types param contains only 1 item the request to Google Places API
# will be send as type param to fullfil:
# http://googlegeodevelopers.blogspot.com.au/2016/02/changes-and-quality-improvements-in_16.html

categories = ['restaurant', 'cafe','clothing_store', 'furniture_store', 'hair_care', 'grocery_or_supermarket', 'electronics_store', 'museum', 'pharmacy', 'store']

def getPlaceType(place, categories):
    for type in place.types:
        for category in categories:
            if type == category:
                return category

#pass in a 'googleplaces.GooglePlacesSearchResult'
def getPlaceInfo(query_result):
    for place in query_result.places:
        # Returned places from a query are place summaries.
        # print (("Place Name: ") + (place.name))
        # print (("Place ID: ") + (place.place_id))

        # The following method has to make a further API call.
        place.get_details()
        # Referencing any of the attributes below, prior to making a call to
        # get_details() will raise a googleplaces.GooglePlacesAttributeError.

        # print (place.details) # A dict matching the JSON response from Google.

        #some of the information below might not exist for a particular place. ie they don't have a website

        # print (place.types)
        mycategory = getPlaceType(place,categories)

        # print ("Address: " + place.formatted_address)
        # print ("Telephoner number: " + place.local_phone_number)
        # print ("Website: " + place.website)
        # print ("See place on google maps: " + place.url) #opens place on google maps
        # print ("Rating: " + str(place.rating))

        #have to extract opening hours from place.details ourselves.
        #place object doesn't have opening hours attribute
        #print ("Opening hours: " + str(place.opening_hours))

        placeInfoJson = {}
        placeInfoJson['Shop Name']=place.name
        placeInfoJson['Address']=place.formatted_address
        placeInfoJson['Category']=mycategory
        placeInfoJson['Telephone number']=place.local_phone_number
        placeInfoJson['Website']=place.website
        placeInfoJson['See place on google maps']=place.url
        placeInfoJson['Rating']=str(place.rating)

        # print(placeInfoJson) #print dict of the place's information

        return placeInfoJson

# query_result = getPlace()
# getPlaceInfo(query_result)

