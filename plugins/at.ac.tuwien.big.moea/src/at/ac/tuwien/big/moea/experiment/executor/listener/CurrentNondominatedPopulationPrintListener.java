package at.ac.tuwien.big.moea.experiment.executor.listener;

import at.ac.tuwien.big.moea.experiment.executor.SearchExecutor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.util.progress.ProgressEvent;

public class CurrentNondominatedPopulationPrintListener extends AbstractProgressListener {

   private final String resultPath;
   FileOutputStream fOut;
   ObjectOutputStream oos;
   private int nfeCount = 0;
   private final int printInterval;
   private final Map<String, Integer> offsetMap = new HashMap<>();

   public CurrentNondominatedPopulationPrintListener(final String resultPath, final List<String> algorithmNames,
         final int runs, final int printInterval) {
      this.resultPath = resultPath;
      this.printInterval = printInterval;
      for(final String a : algorithmNames) {
         if(!Files.exists(Paths.get(resultPath + "/" + a))) {
            offsetMap.put(a, 0);
            new File(resultPath + "/" + a).mkdir();
            new File(resultPath + "/" + a + "/runs").mkdir();
            for(int i = 1; i <= runs; i++) {
               new File(resultPath + "/" + a + "/runs/" + i).mkdir();
            }
         } else {
            final File curRunFolder = new File(resultPath + "/" + a + "/runs");
            final int curRunFolderSize = curRunFolder.list().length;
            offsetMap.put(a, curRunFolderSize);
            for(int i = 1 + curRunFolderSize; i <= runs + curRunFolderSize; i++) {
               new File(resultPath + "/" + a + "/runs/" + i).mkdir();
            }
         }
      }

   }

   @Override
   public void update(final ProgressEvent event) {
      final int currentNFE = event.getCurrentNFE();
      if(event.getCurrentAlgorithm() != null && currentNFE / printInterval > nfeCount) {
         nfeCount++;

         final NondominatedPopulation ndp = event.getCurrentAlgorithm().getResult();
         final String algName = ((SearchExecutor) event.getExecutor()).getName();

         final int noOfRun = event.getCurrentSeed() + offsetMap.get(algName);

         try {

            fOut = new FileOutputStream(
                  resultPath + "/" + algName + "/runs/" + noOfRun + "/" + currentNFE + "_pop.pop");
            oos = new ObjectOutputStream(fOut);
            final List<double[]> popList = new ArrayList<>();
            for(final Solution s : ndp) {
               popList.add(s.getObjectives());
            }
            oos.writeObject(popList);

         } catch(final FileNotFoundException e) {
            e.printStackTrace();
         } catch(final IOException e) {
            e.printStackTrace();
         } catch(final IllegalArgumentException e) {
            e.printStackTrace();
         }
      }
      if(event.isSeedFinished()) {
         nfeCount = 0;
      }

   }

}
