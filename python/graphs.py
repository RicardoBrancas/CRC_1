#!/bin/python
import matplotlib.pyplot as plt
import csv
import numpy as np
from scipy import stats
import math

plt.rc('text', usetex=True)
plt.rc('font', family='serif')

# DEGREE DISTRIBUTION

for filename, name in [('wikipedia_pt.outdegree', 'out'), ('wikipedia_pt.indegree', 'in')]:

    x = []
    y = []

    with open(filename,'r') as csvfile:
        plots = csv.reader(csvfile, delimiter='\t')
        i = 0
        for row in plots:
            x.append(i)
            i += 1
            y.append(int(row[0]))

    x = x[1:]
    y = y[1:]

    for i in range(len(y)-1, 0, -1):
        y[i-1] += y[i]

    y = np.array(y) / y[0]

    max_error = 0.981

    logmiddle_index = int(math.sqrt(len(x)))

    slope = 0
    intercept = 0
    p_value = 0
    r_value = 0
    x_min = 1
    x_max = len(x)
    while abs(r_value) < max_error:
        slope, intercept, r_value, _, _ = stats.linregress(np.log(x[x_min:x_max]),np.log(y[x_min:x_max]))

        if np.log(x[logmiddle_index]) * slope + intercept < np.log(y[logmiddle_index]):
            x_min += max(int(np.log(x_min)), 1)
        else:
            x_max -= int(np.log(x_max)*10)

    print(x_min, x_max, slope)

    plt.clf()

    plt.loglog(x, y)
    plt.loglog(x[x_min:x_max], np.array(x[x_min:x_max]) ** slope * np.exp(intercept), label="$ \gamma = " + str("{0:.2f}".format(abs(slope) + 1)) + "$")
    plt.xlabel('Cumulative ' + name.capitalize() + ' Degree')
    plt.ylabel('Frequency')
    plt.legend()

    plt.savefig("wikipedia_pt_" + name + ".pdf")

# SCC DISTRIBUTION

size = []
count = []

with open('wikipedia_pt.sccdistr','r') as csvfile:
    plots = csv.reader(csvfile, delimiter='\t')
    for row in plots:
        size.append(row[0])
        count.append(int(row[1]))

size.reverse()
count.reverse()

plt.clf()

plt.bar(size, count)
plt.ylabel('Absolute Frequency')
plt.xlabel('Size of Strongly Connected Component')
plt.savefig('wikipedia_pt_sccdistr.pdf')
