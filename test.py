import os
import sys
import platform
import random

JDK_PATH = None # set to None if u don't care / old test.py was working

def main():
    print("Starting Tests!")

    playerA = sys.argv[1].strip() if len(sys.argv) >= 3 else "rebutia_pathfinding_archon"
    playerB = sys.argv[2].strip() if len(sys.argv) >= 3 else "rebutia"

    init(playerA, playerB)

    maps = sys.argv[3:] if len(sys.argv) >= 4 else None
    if maps is None:
        listMaps()
        maps = getMaps()
        # select fiftenn random maps
        maps = random.sample(maps, 15)

    play(playerA, playerB, maps)

def init(playerA, playerB):
    changeGame(playerA, playerB, 'other') # init gradle.properties before running any commands

def play(playerA, playerB, maps):
    winsA = 0
    winsB = 0
    dataList = []

    for map in maps:
        print(f"{playerA} wins: {winsA} | {playerB} wins: {winsB} | running: {map}" + ' '*25, end="\r")
        updateA, updateB = runMatch(playerA, playerB, map)
        winsA += updateA
        winsB += updateB
        dataList.append((map, updateA, updateB))

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
    with open("./test/maps.txt", "r") as f:
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
        f.write(f"outputVerbose=false\n")
        if JDK_PATH is not None:
            f.write(f"org.gradle.java.home={JDK_PATH}")

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