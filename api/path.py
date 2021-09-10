from database import getPathNodes, getEdges, initDB, closeDB
from ddUtils import *
import heapq
import sys

TAG = "path.py"

class Vertex:
    def __init__(self, node):
        self.id = node
        self.adjacent = {}
        # Set distance to infinity for all nodes
        self.distance = sys.maxsize
        # Mark all nodes unvisited        
        self.visited = False  
        # Predecessor
        self.previous = None

    def add_neighbor(self, neighbor, weight=0):
        self.adjacent[neighbor] = weight

    def get_connections(self):
        return self.adjacent.keys()  

    def get_id(self):
        return self.id

    def get_weight(self, neighbor):
        return self.adjacent[neighbor]

    def set_distance(self, dist):
        self.distance = dist

    def get_distance(self):
        return self.distance

    def set_previous(self, prev):
        self.previous = prev

    def set_visited(self):
        self.visited = True

    def __lt__(self, other):
        return self.distance < other.distance

    def __str__(self):
        return str(self.id) + ' adjacent: ' + str([x.id for x in self.adjacent])

class Graph:
    def __init__(self):
        self.vert_dict = {}
        self.num_vertices = 0

    def __iter__(self):
        return iter(self.vert_dict.values())

    def add_vertex(self, node):
        self.num_vertices = self.num_vertices + 1
        new_vertex = Vertex(node)
        self.vert_dict[node] = new_vertex
        return new_vertex

    def get_vertex(self, n):
        if n in self.vert_dict:
            return self.vert_dict[n]
        else:
            return None

    def add_edge(self, frm, to, cost = 0):
        if frm not in self.vert_dict:
            self.add_vertex(frm)
        if to not in self.vert_dict:
            self.add_vertex(to)

        self.vert_dict[frm].add_neighbor(self.vert_dict[to], cost)
        self.vert_dict[to].add_neighbor(self.vert_dict[frm], cost)

    def get_vertices(self):
        return self.vert_dict.keys()

    def set_previous(self, current):
        self.previous = current

    def get_previous(self, current):
        return self.previous

g = Graph()

def initializePathfinding(pathNodes, edges, buildingNodesDict, doFuzz):
    log(TAG, "Initializing pathfinding data")
    buildingNodes = []
    for key in buildingNodesDict:
        selectedBuilding = buildingNodesDict[key]
        thisBuilding = {
            "id" : selectedBuilding['id'],
            "latitude" : selectedBuilding['latitude'],
            "longitude" : selectedBuilding['longitude']
        }
        buildingNodes.append(thisBuilding)

    allNodes = pathNodes + buildingNodes

    for node in allNodes:
        g.add_vertex(node['id'])

    roundNum = 0
    multiplier = 1
    for edge in edges:
        roundNum += 1
        if (doFuzz and roundNum % 10 > 7):
            multiplier = 100
        g.add_edge(edge['from'], edge['to'], distanceBetween(allNodes, edge['to'], edge['from']) * multiplier)
        multiplier = 1

    return allNodes

def nearestNeighbor(nodeList, latitude, longitude):
    bestDistance = sys.maxsize
    bestNode = None
    for node in nodeList:
        thisDistance = pointsDistance(node['latitude'], node['longitude'], latitude, longitude)
        if (thisDistance < bestDistance):
            bestDistance = thisDistance
            bestNode = node

    return bestNode['id']

def distanceBetween(nodeList, toID, fromID):
    toNode = None
    fromNode = None
    for node in nodeList:
        if node['id'] == toID:
            toNode = node
            break
    for node in nodeList:
        if node['id'] == fromID:
            fromNode = node
            break
    if (toNode is None or fromNode is None):
        return 0.001

    dist = pointsDistance(fromNode['latitude'], fromNode['longitude'], toNode['latitude'], toNode['longitude'])
    return dist

def idsToCoords(idList, nodeList):
    pendingCoords = []
    for idEach in idList:
        for nodeEach in nodeList:
            if nodeEach['id'] == idEach:
                pendingCoords.append((nodeEach['latitude'], nodeEach['longitude']))

    return pendingCoords

def printGraphData():
    print ("Graph data:")
    for v in g:
        for w in v.get_connections():
            vid = v.get_id()
            wid = w.get_id()
            print(f"( {vid} , {wid}, {v.get_weight(w)})")

def pathFind(startID, endID):
    dijkstra(g, g.get_vertex(startID), g.get_vertex(endID)) 

    target = g.get_vertex(endID)
    path = [target.get_id()]
    shortest(target, path)
    return path

def shortest(v, path):
    if v.previous:
        path.append(v.previous.get_id())
        shortest(v.previous, path)
    return

def dijkstra(aGraph, start, target):
    # Set the distance for the start node to zero 
    start.set_distance(0)

    # Put tuple pair into the priority queue
    unvisited_queue = [(v.get_distance(),v) for v in aGraph]
    heapq.heapify(unvisited_queue)

    while len(unvisited_queue):
        # Pops a vertex with the smallest distance 
        uv = heapq.heappop(unvisited_queue)
        current = uv[1]
        current.set_visited()

        #for next in v.adjacent:
        for next in current.adjacent:
            # if visited, skip
            if next.visited:
                continue
            new_dist = current.get_distance() + current.get_weight(next)
            
            if new_dist < next.get_distance():
                next.set_distance(new_dist)
                next.set_previous(current)
        # Rebuild heap
        # 1. Pop every item
        while len(unvisited_queue):
            heapq.heappop(unvisited_queue)
        # 2. Put all vertices not visited into the queue
        unvisited_queue = [(v.get_distance(),v) for v in aGraph if not v.visited]
        heapq.heapify(unvisited_queue)