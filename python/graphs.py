#!/bin/python
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import csv
import numpy as np
import math
import powerlaw

matplotlib.rcParams['figure.figsize'] = [8.0, 6.0]
matplotlib.rcParams['figure.dpi'] = 80
matplotlib.rcParams['savefig.dpi'] = 100

matplotlib.rcParams['font.size'] = 18
matplotlib.rcParams['legend.fontsize'] = 'large'
matplotlib.rcParams['figure.titlesize'] = 'medium'

plt.rc('text', usetex=True)
plt.rc('font', family='serif')

# DEGREE DISTRIBUTION

for filename, name in [('wikipedia_pt.outdegree', 'out'), ('wikipedia_pt.indegree', 'in')]:

    y = []

    with open(filename,'r') as csvfile:
        plots = csv.reader(csvfile, delimiter='\t')
        i = 0
        for row in plots:
            i += 1
            y += [i] * int(row[0])

    result = powerlaw.Fit(y)

    R, p = result.distribution_compare('power_law', 'exponential', normalized_ratio=True)
    print(name, "R:",R,"P:",p)

    plt.clf()

    fig = result.plot_ccdf()
    result.power_law.plot_ccdf(linestyle='--', ax=fig,  label="$ \gamma = " + str("{0:.2f}".format(result.power_law.alpha)) + "$")

    fig.set_xlabel(name.capitalize() + ' Degree')
    fig.set_ylabel('Distribution')
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

# NEIGHBOURHOOD FUNCTION

size = []
pdf = []
pdf_err = []
cdf = []
cdf_err = []

with open('wikipedia_pt_anf_stats.out','r') as csvfile:
    plots = csv.reader(csvfile, delimiter=' ')
    for row in plots:
        if row[0] == "PMF":
            size.append(int(row[1]))
            pdf.append(float(row[2]))
            pdf_err.append(float(row[3]))
        if row[0] == "CDF":
            cdf.append(float(row[2]))
            cdf_err.append(float(row[3]))

plt.clf()

plt.errorbar(size, pdf, yerr=pdf_err, label="Distribution")
plt.errorbar(size, cdf, yerr=cdf_err, label="Cumulative Distribution")
plt.ylabel('Distribution')
plt.xlabel('Distance')
plt.legend()
plt.savefig('wikipedia_pt_neighbourhood_function.pdf')

# CLOSENESS CENTRALITY
import struct

cc = []
with open('wikipedia_pt_closeness_centrality.out', 'br') as data:
    d = data.read(4)
    while d:
        (n,) = struct.unpack('!f', d)
        cc.append(n)
        d = data.read(4)

plt.clf()
plt.hist(cc, 20)
plt.ylabel('Absolute Frequency')
plt.xlabel('Closeness Centrality')
plt.savefig('wikipedia_pt_closeness_centrality.pdf')

# HARMONIC CENTRALITY
import struct

cc = []
with open('wikipedia_pt_harmonic_centrality.out', 'br') as data:
    d = data.read(4)
    while d:
        (n,) = struct.unpack('!f', d)
        cc.append(n)
        d = data.read(4)

cc = np.array(cc) / len(cc)

plt.clf()
plt.hist(cc, 35)
plt.ylabel('Absolute Frequency')
plt.xlabel('Harmonic Centrality')
plt.savefig('wikipedia_pt_harmonic_centrality.pdf')

# SUM OF DISTANCES
import struct

cc = []
with open('wikipedia_pt_sum_of_distances.out', 'br') as data:
    d = data.read(4)
    while d:
        (n,) = struct.unpack('!f', d)
        cc.append(n)
        d = data.read(4)

plt.clf()
plt.hist(cc, 20)
plt.savefig('wikipedia_pt_sum_of_distances.pdf')
