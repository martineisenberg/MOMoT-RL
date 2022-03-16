package PacmanGame.creator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

import PacmanGame.Food;
import PacmanGame.Game;
import PacmanGame.Ghost;
import PacmanGame.GridNode;
import PacmanGame.Pacman;
import PacmanGame.PacmanGamePackage;
import PacmanGame.PositionableEntity;
import PacmanGame.Scoreboard;
import PacmanGame.impl.FoodImpl;
import PacmanGame.impl.GhostImpl;
import PacmanGame.impl.PacmanImpl;

public class PacmanRandCreator {
   private final static int WIDTH = 10;
   private final static int NO_INSTANCES_TO_GEN = 5;
   private final static int NO_GHOSTS = 20;
   private final static int NO_FOODS = 20;

   private final static Random RNG = new Random();
   private final static boolean VERBOSE = true;

   public static void main(final String[] args) {
      final HenshinResourceSet rSet = new HenshinResourceSet();
      rSet.getPackageRegistry().put(PacmanGamePackage.eNS_URI, PacmanGamePackage.eINSTANCE);

      // final List<EClassifier> c = ContainerPackage.eINSTANCE.getEClassifiers();
      //
      // final EClassifier ec = ContainerPackage.eINSTANCE.getEClassifier("ContainerModel");
      final EClassifier foodClass = PacmanGamePackage.eINSTANCE.getEClassifier("Food");
      final EClassifier gameClass = PacmanGamePackage.eINSTANCE.getEClassifier("Game");
      final EClassifier ghostClass = PacmanGamePackage.eINSTANCE.getEClassifier("Ghost");
      final EClassifier gridnodeClass = PacmanGamePackage.eINSTANCE.getEClassifier("GridNode");
      final EClassifier pacmanClass = PacmanGamePackage.eINSTANCE.getEClassifier("Pacman");
      final EClassifier scoreBoardClass = PacmanGamePackage.eINSTANCE.getEClassifier("Scoreboard");

      for(int i = 0; i < NO_INSTANCES_TO_GEN; i++) {

         final Game game = (Game) EcoreUtil.create((EClass) gameClass);
         final Scoreboard sb = (Scoreboard) EcoreUtil.create((EClass) scoreBoardClass);
         game.setScoreboard(sb);

         final GridNode[][] nodes = new GridNode[WIDTH][WIDTH];
         for(int j = 0; j < WIDTH; j++) {
            for(int k = 0; k < WIDTH; k++) {
               final GridNode node = (GridNode) EcoreUtil.create((EClass) gridnodeClass);
               node.setId(String.format("%s_%s", j, k));
               game.getGridnodes().add(node);
               nodes[j][k] = node;
            }
         }

         for(int j = 0; j < WIDTH; j++) {
            for(int k = 0; k < WIDTH; k++) {
               final GridNode n = nodes[j][k];
               if(j > 0) {
                  n.setTop(nodes[j - 1][k]);
               }
               if(j < WIDTH - 1) {
                  n.setBottom(nodes[j + 1][k]);
               }
               if(k > 0) {
                  n.setLeft(nodes[j][k - 1]);
               }
               if(k < WIDTH - 1) {
                  n.setRight(nodes[j][k + 1]);
               }
            }
         }

         final int[] unique = RNG.ints(0, WIDTH * WIDTH).distinct().limit(NO_GHOSTS + NO_FOODS + 1).toArray();

         int ci = 0;

         final Pacman p = (Pacman) EcoreUtil.create((EClass) pacmanClass);

         p.setId("p1");
         p.setOn(game.getGridnodes().get(unique[ci++]));
         game.getEntites().add(p);

         for(int j = 0; j < NO_GHOSTS; j++) {
            final Ghost g = (Ghost) EcoreUtil.create((EClass) ghostClass);
            g.setId(String.valueOf(j + 1));
            g.setOn(game.getGridnodes().get(unique[ci++]));
            game.getEntites().add(g);
         }

         for(int j = 0; j < NO_FOODS; j++) {
            final Food f = (Food) EcoreUtil.create((EClass) foodClass);
            f.setId(String.valueOf(j + 1));
            f.setOn(game.getGridnodes().get(unique[ci++]));
            game.getEntites().add(f);
         }

         if(VERBOSE) {
            System.out.println("\n" + i);
            final Map<String, String> nodeStates = new HashMap<>();
            for(final GridNode g : game.getGridnodes()) {
               nodeStates.put(g.getId(), "-");

            }

            for(final PositionableEntity entity : game.getEntites()) {
               if(entity instanceof PacmanImpl) {
                  final String pacId = ((PacmanImpl) entity).getOn().getId();
                  nodeStates.put(pacId, "P");

               } else if(entity instanceof FoodImpl) {
                  final String foodId = ((FoodImpl) entity).getOn().getId();

                  nodeStates.put(foodId, "F");

               } else if(entity instanceof GhostImpl) {
                  final String ghostId = ((GhostImpl) entity).getOn().getId();

                  nodeStates.put(ghostId, "G");

               }
            }

            final List<String> sortedNodeIds = new ArrayList<>(nodeStates.keySet());

            final List<Entry<String, String>> sortedList = nodeStates.entrySet().stream()
                  .sorted(Map.Entry.comparingByKey()).collect(Collectors.toList());

            int cnt = 1;

            for(final Entry<String, String> e : sortedList) {
               System.out.print(e.getValue());
               if(cnt % Math.sqrt(game.getGridnodes().size()) == 0) {
                  System.out.println("");
               }
               cnt++;
            }
         }

         final Resource oR = rSet.createResource(URI.createFileURI(
               "rand_gen_models/" + WIDTH + "x" + WIDTH + "_f" + NO_FOODS + "_g" + NO_GHOSTS + "_" + i + ".xmi"));
         oR.getContents().add(game);
         try {
            oR.save(null);
         } catch(final IOException e) {
            e.printStackTrace();
         }
      }

   }
}
