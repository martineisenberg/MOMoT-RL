import os
import sys
import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns
from tqdm import tqdm


files = os.listdir("../output")


for f in tqdm(files):
    if f.endswith("csv"):
        plt.clf()
        d = pd.read_csv(f"../output/{f}", sep=";").iloc[::, 2:4]
        d['runtime in ms'] = (d['runtime in ms'] - d['runtime in ms'].iloc[0]) / 1000.0
        d.rename(columns={'runtime in ms': 'runtime in s'}, inplace=True)
        d["name"] = "R"
        col = sns.color_palette("Set2")
        sns.set_style("darkgrid", {"axes.facecolor": ".9"})

        with plt.style.context('Solarize_Light2'):
            plt.plot(d.index, d["averageReward"], color=col[2], alpha=0.9, label="R")

        sns.despine()
        plt.xlim([0, d.shape[0]])
        plt.ylim([-60, max(d["averageReward"])])
        plt.yticks(np.arange(-60, max(d["averageReward"]), step=10), fontsize="12")
        plt.xticks(np.arange(0, d.shape[0]+10, step=10),  fontsize="12")

        plt.ylabel("Average Reward", fontsize="12.5")
        plt.xlabel("Time in seconds", fontsize="12.5")
        plt.legend()
        plt.legend(loc="best", fontsize="large")

        plt.savefig(f"avg_rew_{os.path.basename(f)}.png", bbox_inches='tight')
