import matplotlib.pyplot as plt
import csv
import numpy as np
from scipy import stats
import math

x = []
y = []

with open('output.outdegree','r') as csvfile:
    plots = csv.reader(csvfile, delimiter=',')
    i = 0
    for row in plots:
        x.append(i)
        i += 1
        y.append(int(row[0]))

x = x[1:]
y = y[1:]

for i in range(len(y)-1, 0, -1):
    y[i-1] += y[i]

max_error = 0.985

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

plt.plot(np.log(x), np.log(y), ".")
plt.plot(np.log(x), np.log(x) * slope + intercept)
plt.xlabel('Cumulative Out Degree')
plt.ylabel('Frequency')
plt.title('Cumulative Out Degree Distribution')

plt.show()
