from ddUtils import *
TAG = "density.py"


def assignDensityPerBuilding(buildings, densityData):
    pendingBuildings = buildings
    randoms = [75, 7, 70, 40, 45, 43, 68, 99, 29, 59, 21, 46, 5, 47, 92, 86, 88, 42, 37, 78, 33, 55, 1, 30, 53]
    randomIndex = 24
    for key in buildings:
        people = []
        cumulator = 0
        for i in range(buildings[key]['floors']):
            randomIndex += 1
            if (randomIndex >= len(randoms) - 1):
                randomIndex = 0
            people.append(randoms[randomIndex])
            cumulator += randoms[randomIndex]
        densityPercent = cumulator / len(people)
        densityLevel = 1
        if (densityPercent >= 40): 
            densityLevel = 2
        if (densityPercent >= 70): 
            densityLevel = 3
        pendingBuildings[key]['density'] = densityPercent
        pendingBuildings[key]['densityLevel'] = densityLevel
        pendingBuildings[key]['people'] = people
    # for entry in densityData:
    #     for key in buildings:
    #         building = buildings[key]
    #         latitude = entry['latitude']
    #         latitudeMin = building['latitudeMin']
    #         latitudeMax = building['latitudeMax']
    #         longitude = entry['longitude']
    #         longitudeMin = building['longitudeMin']
    #         longitudeMax = building['longitudeMax']

    #         if (latitude > latitudeMin):
    #             if (latitude < latitudeMax):
    #                 if (longitude > longitudeMin):
    #                     if (longitude < longitudeMax):
    #                         buildingId = building['id']
    #                         for floor in buildings[buildingId]['people']:
    #                             #buildings[buildingId]['people'][floor] += 1
    #                             True
    #                         break

    return pendingBuildings