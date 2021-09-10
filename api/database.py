from ddUtils import *
import time
import mysql.connector

TAG = "database.py"

# Initialize DB connection
def initDB():
    log(TAG, "Opening database connection to " + Config.get("db_host"))
    db = mysql.connector.connect(
        host = Config.get("db_host"),
        user = Config.get("db_username"),
        password = Config.get("db_password"),
        database = Config.get("db_database"),
        ssl_disabled = True
    )

    return db

def closeDB(db):
    log(TAG, "Closing database connection to " + Config.get("db_host"))
    db.close()

def addDensityEntry(db, latitude, longitude):
    log(TAG, "Adding density entry at lat:" + latitude + ", long:" + longitude)

    cursor = db.cursor()
    epoch = int(time.time())

    cursor.execute(f"INSERT INTO sightings (epoch_timestamp, latitude, longitude) VALUES ({epoch}, {latitude}, {longitude})")
    db.commit()
    cursor.close()


def getBuildings(db):
    log(TAG, "Creating list of buildings")
    cursor = db.cursor()
    cursor.execute("SELECT * FROM nodes")

    pendingBuildings = {}
    for loc in cursor:
        pendingLocation = {}
        pendingLocation['id'] = loc[0]
        pendingLocation['buildingName'] = loc[1]
        middleLatitude = ((loc[2] + loc[4]) / 2)
        middleLongitude = ((loc[3] + loc[5]) / 2)
        pendingLocation['latitude'] = middleLatitude
        pendingLocation['longitude'] = middleLongitude
        pendingLocation['floors'] = loc[7]
        pendingLocation['people'] = []
        pendingLocation['densityLevel'] = 0
        pendingLocation['type'] = loc[6]
        pendingLocation['latitudeMin'] = loc[2]
        pendingLocation['latitudeMax'] = loc[4]
        pendingLocation['longitudeMin'] = loc[3]
        pendingLocation['longitudeMax'] = loc[5]
        pendingLocation['url'] = loc[8]

        pendingBuildings[loc[0]] = pendingLocation

    cursor.close()

    return pendingBuildings

def getDensity(db):
    timeThreshhold = int(time.time()) - Config.get("epoch_offset")
    log(TAG, "Getting density data")
    cursor = db.cursor()
    cursor.execute("SELECT * FROM sightings WHERE epoch_timestamp > " + str(timeThreshhold))

    pendingSightings = []
    for sighting in cursor:
        pendingSighting = {}
        pendingSighting['latitude'] = sighting[2]
        pendingSighting['longitude'] = sighting[3]
        pendingSightings.append(pendingSighting)

    cursor.close()

    return pendingSightings

def getPathNodes(db):
    log(TAG, "Getting path node data")
    cursor = db.cursor()
    cursor.execute("SELECT * FROM route")

    routeNodes = []

    for routeNode in cursor:
        routeNodeEach = {}
        routeNodeEach['id'] = routeNode[0]
        routeNodeEach['latitude'] = routeNode[1]
        routeNodeEach['longitude'] = routeNode[2]
        routeNodes.append(routeNodeEach)

    return routeNodes

    cursor.close()

def getEdges(db):
    log(TAG, "Getting edge data")
    cursor = db.cursor()
    cursor.execute("SELECT * FROM edges")

    edges = []

    for edge in cursor:
        edgeEach = {}
        edgeEach['to'] = edge[1]
        edgeEach['from'] = edge[0]
        edgeEach['distance'] = 3
        edges.append(edgeEach)

    return edges

    cursor.close()
