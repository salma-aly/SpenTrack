import io
import os

# Imports the Google Cloud client library
from google.cloud import vision
from google.cloud.vision import types

# # Instantiates a client
# client = vision.ImageAnnotatorClient()
#
# # The name of the image file to annotate
# file_name = os.path.join(
#     os.path.dirname(__file__),
#     'C:/Users/Ying-Chen/Documents/COEN424/receipt.jpg')
#
# # Loads the image into memory
# with io.open(file_name, 'rb') as image_file:
#     content = image_file.read()
#
# image = types.Image(content=content)
#
# # Performs label detection on the image file
# response = client.label_detection(image=image)
# labels = response.label_annotations
#
# print('Labels:')
# for label in labels:
#     print(label.description)

def detect_text(path):
    """Detects text in the file."""
    client = vision.ImageAnnotatorClient()

    with io.open(path, 'rb') as image_file:
        content = image_file.read()

    image = types.Image(content=content)

    response = client.text_detection(image=image)
    texts = response.text_annotations
    print('Texts:')

    #for text in texts:
    print('\n"{}"'.format(texts[0].description))

    #started trying to solve for TOTAL:
    # receipt_text = '\n"{}"'.format(texts[0].description).lower()
    # foundTotal = receipt_text.find("total")
    # print (foundTotal)

        # vertices = (['({},{})'.format(vertex.x, vertex.y)
        #             for vertex in text.bounding_poly.vertices])

        # print('bounds: {}'.format(','.join(vertices)))

detect_text("C:/Users/Ying-Chen/Documents/COEN424/receipt.jpg")
