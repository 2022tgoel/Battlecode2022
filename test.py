import os
import sys
import platform


def main():
    print("Starting Tests!")
    playerA = "pathfindingplayer_OLD_v3"
    playerB = "barbaryfig"
    playAllMaps(playerA, playerB)

def playAllMaps(playerA: str, playerB: str):
    listMaps()
    maps = getMaps()
    winsA = 0
    winsB = 0
    dataList = []
    print(f"{playerA} wins: 0 | {playerB} wins: 0", end="\r")
    for map in maps:
        updateA, updateB = runMatch(playerA, playerB, map)
        winsA += updateA
        winsB += updateB
        dataList.append((map, updateA, updateB))
        print(f"{playerA} wins: {winsA} | {playerB} wins: {winsB}", end="\r")
    print(f"{playerA} wins: {winsA} | {playerB} wins: {winsB}", end="\n")
    printData(playerA, playerB, dataList)

def runMatch(playerA: str, playerB: str, map: str):
    changeGame(playerA, playerB, map)
    executeGame()
    updateA1, updateB1 = parseGameResult()
    changeGame(playerB, playerA, map)
    executeGame()
    updateB2, updateA2 = parseGameResult()
    return (updateA1 + updateA2, updateB1 + updateB2)

def listMaps():
    if (platform.system() == 'Windows'):
        os.system("gradlew listMaps > test/maps.txt")
    else:
        os.system("./gradlew listMaps > test/maps.txt")

def getMaps():
    maps = []
    with open("test/maps.txt", "r") as f:
        f_iter = iter(f)
        next(f_iter, None)
        next(f_iter, None)
        for line in f_iter:
            data = line.rstrip().split()
            if (data != []):
                if (data[0] == "MAP:"):
                    maps.append(data[1])
    return maps

def changeGame(player1: str, player2: str, map: str):
    with open("gradle.properties", "w") as f:
        f.write(f"teamA={player1}\n")
        f.write(f"teamB={player2}\n")
        f.write(f"packageNameA={player1}\n")
        f.write(f"packageNameB={player2}\n")
        f.write(f"maps={map}\n")
        f.write(f"source=src\n")
        f.write(f"profilerEnabled=false\n")
        f.write(f"outputVerbose=false")

def executeGame():
    if (platform.system() == 'Windows'):
        os.system("gradlew run > test/data.txt")
    else:
        os.system("./gradlew run > test/data.txt")

def parseGameResult():
    if (platform.system() == 'Windows'):
        with open("test/data.txt", "r") as f:
            f_iter = iter(f)
            for line in f_iter:
                if ('(A)' in line):
                    return (1, 0)
                elif ('(B)' in line):
                    return (0, 1)
    else:
        with open("test/data.txt", "r") as f:
            f_iter = iter(f)
            for line in f_iter:
                if (line.rstrip() == "[server] -------------------- Match Starting --------------------"):
                    next(f_iter, None)
                    test_line = next(f_iter, "")
                    data = test_line.split()
                    if (data[3] == "wins"):
                        if (data[2] == "(A)"):
                            return (1, 0)
                        elif (data[2] == "(B)"):
                            return (0, 1)
                    elif (data[3] == "loses"):
                        if (data[2] == "(A)"):
                            return (0, 1)
                        elif (data[2] == "(B)"):
                            return (1, 0)

def printData(playerA, playerB, data):
    for datum in data:
        print(f"{datum[0]} -> {playerA}: {datum[1]} | {playerB}: {datum[2]}")

main()