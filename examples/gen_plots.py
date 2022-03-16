import os
import sys
import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns
import argparse
import glob
import re

CLI = argparse.ArgumentParser()
CLI.add_argument(
  "--files",  # name on the CLI - drop the `--` for positional/required parameters
  nargs="*",  # 0 or more values expected => creates a list
  type=str,
    default=[]
)

CLI.add_argument(
  "--algnames",  # name on the CLI - drop the `--` for positional/required parameters
  nargs="*",  # 0 or more values expected => creates a list
  type=str,
    default=[]
)

CLI.add_argument(
  "--maxX",  # name on the CLI - drop the `--` for positional/required parameters
  type=int,
)

CLI.add_argument(
  "--stepsizeX",  # name on the CLI - drop the `--` for positional/required parameters
  type=int,
)

CLI.add_argument(
  "--folder",  # name on the CLI - drop the `--` for positional/required parameters
  type=str,

)

CLI.add_argument(
  "--out",  # name on the CLI - drop the `--` for positional/required parameters
  type=str,

)
#path1 = sys.argv[1]
#name1 = sys.argv[2]
#path2 = sys.argv[3]
#name2 = sys.argv[4]

args = CLI.parse_args()
files = args.files
algnames = args.algnames
max_epochs = args.maxX
stepsize_x = args.stepsizeX
folder = args.folder
outpath = args.out



plot_algs = []

extract_names = not bool(algnames)

if os.path.isdir(folder):
    for f in glob.glob(os.path.join(folder, "*.csv")):
        print(f)

        a = pd.read_csv(f, sep=";").iloc[:, 2:4]
        plot_algs.append(a)
        if extract_names:
            fname = os.path.basename(f)

            algnames.append(fname)



else:
    for file, algname in zip(files, algnames):
        print(file)
        a = pd.read_csv(file, sep=";").iloc[:, 2:4]
        #a['runtime in ms'] = (a['runtime in ms'] - a['runtime in ms'].iloc[0]) / 1000.0

        #a.rename(columns={'runtime in ms': 'runtime in s'}, inplace=True)
       # a["name"] = algname
        plot_algs.append(a)


#alg2 = pd.read_csv(path2, sep=";").iloc[:, 2:4]
#alg2['runtime in ms'] = (alg2['runtime in ms'] - alg2['runtime in ms'].iloc[0])/1000.0
#alg2.rename(columns={'runtime in ms':'runtime in s'}, inplace=True)
#alg2["name"] = name2

#max_time_x = max(max(alg1["runtime in s"]),max(alg2["runtime in s"]))


print("-> Creating plot...")

col = sns.color_palette("Set2")
sns.set_style("darkgrid", {"axes.facecolor": ".9"})

with plt.style.context('Solarize_Light2'):
    for i, a in enumerate(plot_algs):
        print(algnames[i])
        plt.plot(list(range(0, a.shape[0])), a["averageReward"], color=col[i], alpha=0.9, label=algnames[i])
    #plt.plot(alg2["runtime in s"], alg2["averageReward"], color=col[1], alpha=0.9, label=name2)

sns.despine()
plt.xlim([0, max_epochs])
#plt.ylim([-60, 35])
#plt.yticks(np.arange(-60, 35, step=10), fontsize="12")
plt.xticks(np.arange(0, max_epochs, step=stepsize_x),  fontsize="12")

plt.ylabel("Average Reward", fontsize="12.5")
plt.xlabel("Epochs", fontsize="12.5")
plt.legend()
plt.legend(loc="best", fontsize="large")

plt.savefig(outpath, bbox_inches='tight')
print(f"-> Plot saved to '{outpath}'")