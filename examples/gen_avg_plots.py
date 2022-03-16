import seaborn as sns
import pandas as pd
import scipy.stats as st
import os
import sys
import numpy as np
import matplotlib.pyplot as plt
import argparse
sns.set_style("whitegrid", {'axes.grid' : True})
sns.set_style("ticks")

col = sns.color_palette("husl", 8)


def plot(x_q, mean_q, x_pg, mean_pg, quartiles1_q, quartiles3_q, quartiles1_pg, quartiles3_pg, savepath=None, alg_names=[]):
    plt.figure(figsize=(10, 6.5))

    ax1 = sns.lineplot(x_q, mean_q, linewidth=2, color=col[4], label=alg_names[0])
    ax2 = sns.lineplot(x_pg, mean_pg, linewidth=2, color=col[6], label=alg_names[1])
    ax1.fill_between(x_q, quartiles1_q, quartiles3_q, alpha=0.35, color=col[4])
    ax2.fill_between(x_pg, quartiles1_pg, quartiles3_pg, alpha=0.35, color=col[6])

    # plt.xlim([0, x_axis_seconds])
    # plt.ylim(-40, 5)
    plt.yticks(fontsize="16")
    plt.xticks(fontsize="16", color='black')

    plt.ylabel("Average Cumulative Reward", fontsize="18")
    plt.xlabel("Episode", fontsize="18")
    plt.legend()
    plt.legend(loc="lower right", fontsize="16")

    sns.despine(left=True, bottom=True)
    if savepath is not None:
        plt.savefig(f'{savepath}.pdf', dpi=300)


CLI = argparse.ArgumentParser()
CLI.add_argument(
  "--folders",  # name on the CLI - drop the `--` for positional/required parameters
  nargs="*",  # 0 or more values expected => creates a list
  type=str,
    default=[]
)

CLI.add_argument(
  "--out",  # name on the CLI - drop the `--` for positional/required parameters
  type=str,
    default=[]
)

CLI.add_argument(
  "--names",  # name on the CLI - drop the `--` for positional/required parameters
   nargs="*",
  type=str,
    default=[]
)

CLI.add_argument(
  "--filestartswith",  # name on the CLI - drop the `--` for positional/required parameters
   nargs=2,
  type=str,
    default=[None, None]
)

CLI.add_argument(
  "--plotepochs",  # name on the CLI - drop the `--` for positional/required parameters
   nargs=1,
  type=int,
  default=None
)
args = CLI.parse_args()

folders = args.folders
out = args.out
alg_names = args.names
startswith = args.filestartswith
max_x = args.plotepochs

files1 = os.listdir(folders[0])
files2 = os.listdir(folders[1])

if startswith[0] is not None and startswith[1] is not None:
    files1 = [f for f in files1 if f.startswith(startswith[0])]
    files2 = [f for f in files2 if f.startswith(startswith[1])]

df1 = pd.DataFrame()
df2 = pd.DataFrame()

N = 10

for f in files1:
    if f.endswith("csv"):

        d = pd.read_csv(os.path.join(folders[0] ,f), sep=";").iloc[::, 2:4]
        d = d.groupby(d.index // N).mean()
        d['epoch'] = np.arange(N, (len(d) + 1) * N, N)

        df1 = df1.append(d)

for f in files2:
    if f.endswith("csv"):
        d = pd.read_csv(os.path.join(folders[1] ,f), sep=";").iloc[::, 2:4]
        d = d.groupby(d.index // N).mean()
        d['epoch'] = np.arange(N, (len(d) + 1) * N, N)

        df2 = df2.append(d)

if max_x is not None:
    df1 = df1[df1['epoch'] <= max_x[0]]
    df2 = df2[df2['epoch'] <= max_x[0]]

stats_1 = df1.groupby(['epoch']).describe()
stats_2 = df2.groupby(['epoch']).describe()

#plt.figure(figsize=(12,8))
#sns.set_style("whitegrid")

## QLearn
x_1 = stats_1.index
mean_1 = stats_1[('averageReward', 'mean')]
mean_1.name = 'averageReward'
#quartiles1_1 = mean_1 - stats_1[('averageReward', 'std')]
#quartiles3_1 = mean_1 + stats_1[('averageReward', 'std')]

## PG
x_2 = stats_2.index
mean_2 = stats_2[('averageReward', 'mean')]
mean_2.name = 'averageReward'
#quartiles1_2 = mean_2 - stats_2[('averageReward', 'std')]
#quartiles3_2 = mean_2 + stats_2[('averageReward', 'std')]

cis_1 = []
for _,data in df1.groupby('epoch'):
    cis_1.append(st.t.interval(alpha=0.95, df=len(data)-1, loc=np.mean(data['averageReward']), scale=st.sem(data['averageReward'])))
cis_2 = []
for _,data in df2.groupby('epoch'):
    cis_2.append(st.t.interval(alpha=0.95, df=len(data)-1, loc=np.mean(data['averageReward']), scale=st.sem(data['averageReward'])))

plot(x_1, mean_1, x_2, mean_2, [x[0] for x in cis_1], [x[1] for x in cis_1], [x[0] for x in cis_2], [x[1] for x in cis_2], out, alg_names)

