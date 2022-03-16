package PacmanGame.creator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

import PacmanGame.Game;
import PacmanGame.PacmanGamePackage;
import PacmanGame.PositionableEntity;

public class PacmanCreator {

   private static final int GEN_INSTANCES = 5;
   private static final int NO_FIELDS = 64;

   public static void main(final String[] args) {

      final HenshinResourceSet rSet = new HenshinResourceSet();
      rSet.getPackageRegistry().put(PacmanGamePackage.eNS_URI, PacmanGamePackage.eINSTANCE);
      final Resource r = rSet.getResource(URI.createFileURI("models/input_8x8.xmi"), true);
      final Game g = (Game) r.getContents().get(0);
      final List<Game> gameList = new ArrayList<>();

      final Random ra = new Random();
      final int randomNumberOrigin = 0;
      final int randomNumberBound = NO_FIELDS;
      final int size = g.getEntites().size();

      for(int j = 0; j < GEN_INSTANCES; j++) {
         final int[] unique = ra.ints(randomNumberOrigin, randomNumberBound).distinct().limit(size).toArray();
         int i = 0;
         final Map<Integer, String> map = new HashMap<>();
         for(final PositionableEntity pe : g.getEntites()) {
            map.put(unique[i], pe.getId());

            pe.setOn(g.getGridnodes().get(unique[i++]));
         }
         System.out.println(j);
         for(int k = 0; k < 8; k++) {
            for(int z = 0; z < 8; z++) {
               if(map.containsKey(k * 8 + z)) {
                  System.out.print(" " + map.get(k * 8 + z) + " ");
               } else {
                  System.out.print(" o ");
               }
            }
            System.out.println();
         }
         System.out.println("\n");

         // boolean createDup = false;
         // for(final Game curGame : gameList) {
         // System.out.println(curGame.getEntites().toString());
         // if(EcoreUtil.equals(curGame.getEntites(), g.getEntites())) {
         // System.out.println("created duplicate");
         // createDup = true;
         // break;
         // }
         // }

         // if(!createDup) {

         gameList.add(g);

         final Resource oR = rSet.createResource(URI.createFileURI("rand_gen_models/8x8_" + j + ".xmi"));
         oR.getContents().add(g);
         try {
            oR.save(null);
         } catch(final IOException e) {
            e.printStackTrace();
         }
         // } else {
         // System.out.println("was duplicate");
         // j--;
         // }
      }

   }

}
