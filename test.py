import os
import platform
import random
import re
import sys

import pandas as pd

JDK_PATH = None # set to None if u don't care / old test.py was working

def main():
    print("Starting Tests!")

    _, a, b = sys.argv

    init(a, b)

    random.seed(42)

    maps = sys.argv[3:] if len(sys.argv) >= 4 else None
    if maps is None:
        store_maps()
        maps = random.sample(read_maps(), 15)
        # maps = read_maps()

    play(a, b, maps)

def init(a, b):
    initialize_gradle_properties(a, b, 'other') # init gradle.properties before running any commands

def play(a, b, maps):
    a_wins = 0
    b_wins = 0
    history = []

    for map in maps:
        print(f"{a} wins: {a_wins} | {b} wins: {b_wins} | running: {map}" + ' '*25, end="\r", flush=True)
        a_won, b_won = run_set(a, b, map)
        a_wins += a_won
        b_wins += b_won
        history.append((map, a_won, b_won))

    print(f"{a} wins: {a_wins} | {b} wins: {b_wins}")
    print_history(a, b, history)

def run_set(a: str, b: str, map: str):
    initialize_gradle_properties(a, b, map)
    run_game()
    a_won, b_won, a_df, b_df = parse_results()
    initialize_gradle_properties(b, a, map)
    run_game()
    b_won2, a_won2, a_df2, b_df2 = parse_results()
    return (a_won + a_won2, b_won + b_won2)

def store_maps():
    if platform.system() == 'Windows':
        os.system("gradlew listMaps > test/maps.txt")
    else:
        os.system("./gradlew listMaps > test/maps.txt")

def read_maps():
    maps = []
    with open("./test/maps.txt", "r") as f:
        f_iter = iter(f)
        next(f_iter, None)
        next(f_iter, None)
        for line in f_iter:
            data = line.rstrip().split()
            if data:
                if data[0] == "MAP:":
                    maps.append(data[1])
    return maps

def initialize_gradle_properties(a: str, b: str, map: str):
    with open("gradle.properties", "w") as f:
        f.write(f"teamA={a}\n")
        f.write(f"teamB={b}\n")
        f.write(f"packageNameA={a}\n")
        f.write(f"packageNameB={b}\n")
        f.write(f"maps={map}\n")
        f.write(f"source=src\n")
        f.write(f"profilerEnabled=false\n")
        f.write(f"outputVerbose=true\n")
        if JDK_PATH is not None:
            f.write(f"org.gradle.java.home={JDK_PATH}")

def run_game():
    if platform.system() == 'Windows':
        os.system("gradlew run > test/data.txt")
    else:
        os.system("./gradlew run > test/data.txt")

def parse_results():
    dfs = {
        team: pd.DataFrame(columns=['round_number']) for team in 'AB'
    }

    for v in dfs.values():
        v.set_index('round_number', inplace=True)
        
    match_started = False
    a_won = 0
    b_won = 0

    with open("test/data.txt", "r") as f:
        i = iter(f)
        for line in i:
            if line.startswith('[server]'):
                if 'match starting' in line.lower():
                    match_started = True
            
                if match_started:
                    if 'wins' in line.lower():
                        a_won = int('(A)' in line)
                        b_won = int('(B)' in line)
                        break
                    elif 'loses' in line.lower():
                        a_won = 1 - int('(A)' in line)
                        b_won = 1 - int('(B)' in line)
                        break
            elif line.startswith('[A:') or line.startswith('[B:'):
                team, troop_type, troop_id, round_number = re.split(r'[:#@]', line[1:line.index(']')])
                df = dfs[team]
                troop_id = int(troop_id)
                round_number = int(round_number)
                message = line[line.index(']') + 2:]
                if message[:1] == '$' and ':' in message:
                    stat_name, stat_value = re.split(r':', message[1:])
                    stat_value = stat_value.strip()
                    df.loc[round_number, stat_name] = stat_value

    return (a_won, b_won, dfs['A'], dfs['B'])

def print_history(a, b, history):
    for name, a_won, b_won in history:
        print(f"{name} -> {a}: {a_won} | {b}: {b_won}")

main()
