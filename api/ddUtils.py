import json
import math
with open('config.json') as config_file:
    config = json.load(config_file)

class Config:
    def get(property):
        return config[property]

def log(tag, message):
    if (Config.get("environment") == "dev"):
        print(f"[{tag}] {message}")

def pointsDistance(xOne, yOne, xTwo, yTwo):
    result = math.sqrt( ((xOne-xTwo)**2)+((yOne-yTwo)**2) )
    return result

def buildingsDictToData(buildingsDict):
    buildingsArr = []
    print(buildingsDict)
    for building in buildingsDict.items():       
        buildingEach = {
            "id": building[1]['id'],
            "buildingName": building[1]['buildingName'],
            "latitude": building[1]['latitude'],
            "longitude": building[1]['longitude'],
            "floors": building[1]['floors'],
            "people": building[1]['people'],
            "densityLevel": building[1]['densityLevel'],
            "type": building[1]['type'],
            "url": building[1]['url'],
            "density" : building[1]['density']
        }
        buildingsArr.append(buildingEach)
    
    return buildingsArr
