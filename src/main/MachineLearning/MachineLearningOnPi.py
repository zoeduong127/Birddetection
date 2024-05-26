# ! pip install cv2
# ! pip install numpy
# ! pip instal tensorflow.keras

from cv2 import imread, resize
from numpy import reshape, argmax
from tensorflow.keras import Model
from tensorflow.keras.layers import Dense, GlobalAveragePooling2D, Dropout
from tensorflow.keras.applications import InceptionV3
from json import loads

IMAGE_SHAPE = (1024, 1024)
INPUT_LAYER_SHAPE = IMAGE_SHAPE + (3,)

path_to_image = "48.jpg"
image = imread(path_to_image)
image = resize(image,IMAGE_SHAPE)
image = reshape(image,[1] + [i for i in INPUT_LAYER_SHAPE])

# initialize model
pre_trained_model = InceptionV3(include_top = False, input_shape = INPUT_LAYER_SHAPE)
pre_trained_model.trainable = False
x = GlobalAveragePooling2D()(pre_trained_model.output)
x = Dense(512, activation='relu', kernel_initializer='random_normal',
    bias_initializer='zeros', name='fc1')(x)
x = Dropout(0.5)(x)
predictions = Dense(21, activation='softmax', kernel_initializer='random_normal',
    bias_initializer='zeros', name='last_layer')(x)
classifier = Model(pre_trained_model.input, predictions)
# classifier.load_weights('classifier_balanced_epoch_6.ckpt')

# compile the model
classifier.compile(
    loss = 'categorical_crossentropy',
    optimizer = 'adam',
    metrics = ['accuracy']
)
  
# # reading the data from the file 
# with open('classes.txt') as f: 
#     data = f.read()
#     f.close()
  
# # reconstructing the data as a dictionary 
# classes_dictionary = loads(data) 

classes_dictionary = {"0":"Blackbird",
"1":"Bluetit",
"2":"CarrionCrow",
"3":"Chaffinch",
"4":"CoalTit",
"5":"CollaredDove",
"6":"Dunnock",
"7":"FeralPigeon",
"8":"Goldfinch",
"9":"GreatTit",
"10":"Greenfinch",
"11":"HouseSparrow",
"12":"Jackdaw",
"13":"LongTailedTit",
"14":"Magpie",
"15":"NoBird",
"16":"Robin",
"17":"SongThrush",
"18":"Starling",
"19":"WoodPigeon",
"20":"Wren"}

# predict the class of image
predicted_class = argmax(classifier.predict(image), axis=-1)
predicted_class_name = classes_dictionary[str(predicted_class[0])]
print(predicted_class_name)