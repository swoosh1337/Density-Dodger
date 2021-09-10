from ddUtils import *
from database import initDB, closeDB, getBuildings, getDensity, addDensityEntry, getPathNodes, getEdges
from density import *
from path import initializePathfinding, pathFind, idsToCoords, nearestNeighbor
from flask import Flask
from flask import jsonify
from flask import request

app = Flask(__name__)
TAG = "start.py"

log(TAG, "Starting API")
db = initDB()
buildings = getBuildings(db)
densityData = getDensity(db)
pathNodes = getPathNodes(db)
edges = getEdges(db)
buildings = assignDensityPerBuilding(buildings, densityData)


@app.route('/api/buildings/all')
def serveBuildings():
    return jsonify(buildingsDictToData(buildings))

@app.route('/api/buildings/<id>')
def serveBuilding(id):
    return jsonify(buildings[id])

@app.route('/api/density/add')
def addDensity():
    latitude = (request.args.get('latitude'))
    longitude = (request.args.get('longitude'))
    addDensityEntry(db, latitude, longitude)
    return '200 OK', 200

@app.route('/api/navigation/route')
def findRoute():
    doFuzz = False
    doFuzzArg = request.args.get('safe')
    if (doFuzzArg is not None and doFuzzArg.lower() == 'true'):
        doFuzz = True

    allNodes = initializePathfinding(pathNodes, edges, buildings, doFuzz)

    fromID = request.args.get('from')
    appendStart = False
    if fromID is None:
        appendStart = True
        from_latitude = float(request.args.get('from_latitude'))
        from_longitude = float(request.args.get('from_longitude'))
        fromID = nearestNeighbor(allNodes, from_latitude, from_longitude)
    print(fromID)

    toID = request.args.get('to')
    if toID is None:
        to_latitude = float(request.args.get('to_latitude'))
        to_longitude = float(request.args.get('to_longitude'))
        toID = nearestNeighbor(allNodes, to_latitude, to_longitude)
    print(toID)

    ids = pathFind(fromID, toID)
    coords = idsToCoords(ids, allNodes)
    if (appendStart):
        coords.append((from_latitude, from_longitude))
    return jsonify(coords)
