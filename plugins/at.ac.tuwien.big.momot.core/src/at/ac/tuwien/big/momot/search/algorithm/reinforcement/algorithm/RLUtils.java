package at.ac.tuwien.big.momot.search.algorithm.reinforcement.algorithm;

import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.ApplicationState;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.IMOQTableAccessor;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.IParetoQTableAccessor;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.IQTableAccessor;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.ISOQTableAccessor;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures.ParetoQState;
import at.ac.tuwien.big.moea.search.algorithm.reinforcement.utils.IRLUtils;
import at.ac.tuwien.big.momot.problem.solution.TransformationSolution;
import at.ac.tuwien.big.momot.problem.solution.variable.RuleApplicationVariable;
import at.ac.tuwien.big.momot.problem.solution.variable.UnitApplicationVariable;
import at.ac.tuwien.big.momot.search.algorithm.reinforcement.datastructures.MOQTable;
import at.ac.tuwien.big.momot.search.algorithm.reinforcement.datastructures.ParetoQTable;
import at.ac.tuwien.big.momot.search.algorithm.reinforcement.datastructures.SOQTable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.henshin.interpreter.Assignment;
import org.eclipse.emf.henshin.interpreter.RuleApplication;
import org.eclipse.emf.henshin.interpreter.UnitApplication;
import org.eclipse.emf.henshin.model.Parameter;
import org.eclipse.emf.henshin.model.Unit;
import org.moeaframework.core.Solution;

public class RLUtils<S extends Solution> implements IRLUtils<S> {

   private Map<String, Object> extractParameters(final Assignment assignment) {
      final Map<String, Object> paramValues = new HashMap<>();

      for(final Parameter p : assignment.getUnit().getParameters()) {
         if(assignment.getParameterValue(p) != null) {
            paramValues.put(p.getName(), assignment.getParameterValue(p));
         }
      }

      return paramValues;
   }

   private Map<String, Object> extractParameters(final RuleApplication... raps) {
      final Map<String, Object> paramValues = new HashMap<>();

      for(final RuleApplication ra : raps) {
         for(final Parameter p : ra.getUnit().getParameters()) {
            if(ra.getPartialMatch().getParameterValue(p) != null) {
               paramValues.put(p.getName(), ra.getPartialMatch().getParameterValue(p));
            }
         }

      }
      return paramValues;
   }

   @Override
   public List<ApplicationState> getApplicationStates(final S solution) {

      final List<ApplicationState> stateList = new ArrayList<>();

      for(int i = 0; i < solution.getNumberOfVariables(); i++) {
         final UnitApplication ua = (UnitApplication) solution.getVariable(i);
         stateList.add(unitApplicationToApplicationState(ua));
      }

      return stateList;
   }

   @Override
   public List<ApplicationState> getApplicationStatesDiff(final S cur, final S next) {
      final List<UnitApplication> trafoVars1 = new ArrayList<>();
      final List<UnitApplication> trafoVars2 = new ArrayList<>();
      for(int i = 0; i < cur.getNumberOfVariables(); i++) {
         trafoVars1.add((UnitApplication) cur.getVariable(i));

      }

      for(int i = 0; i < next.getNumberOfVariables(); i++) {
         trafoVars2.add((UnitApplication) next.getVariable(i));

      }

      final List<ApplicationState> stateList = new ArrayList<>();

      for(final UnitApplication ua : trafoVars2.subList(trafoVars1.size(), trafoVars2.size())) {
         stateList.add(unitApplicationToApplicationState(ua));
      }
      return stateList;
   }

   @Override
   public IMOQTableAccessor<List<ApplicationState>, List<ApplicationState>> initMOQTable(
         final Map<String, Unit> unitMapping) {
      return new MOQTable<>();
   }

   @Override
   public IParetoQTableAccessor<List<ApplicationState>, List<ApplicationState>> initParetoQTable(
         final Map<String, Unit> unitMapping) {
      return new ParetoQTable<>();
   }

   @Override
   public ISOQTableAccessor<List<ApplicationState>, List<ApplicationState>> initSOQTable(
         final Map<String, Unit> unitMapping) {
      return new SOQTable<>();
   }

   @Override
   public IMOQTableAccessor<List<ApplicationState>, List<ApplicationState>> loadMOQTable(final String inputSrc,
         final Map<String, Unit> unitMapping) {
      MOQTable<List<ApplicationState>, List<ApplicationState>> o = null;
      FileInputStream fis = null;
      ObjectInputStream in = null;
      try {
         fis = new FileInputStream(inputSrc);
         in = new ObjectInputStream(fis);
         o = (MOQTable<List<ApplicationState>, List<ApplicationState>>) in.readObject();
         in.close();
      } catch(final IOException ex) {
         ex.printStackTrace();
      } catch(final ClassNotFoundException ex) {
         ex.printStackTrace();
      }

      final Map<String, Unit> unitPlainNameMapping = new HashMap<>();
      for(final Entry<String, Unit> e : unitMapping.entrySet()) {
         final String[] strings = e.getKey().split("::");
         unitPlainNameMapping.put(strings[strings.length - 1], e.getValue());
      }

      for(final Entry<List<ApplicationState>, Map<List<ApplicationState>, double[]>> e : o.getTable().entrySet()) {
         final List<ApplicationState> aList = e.getKey();
         for(final ApplicationState as : aList) {
            as.setUnit(unitPlainNameMapping.get(as.getUnitName()));
         }
         for(final Entry<List<ApplicationState>, double[]> e2 : e.getValue().entrySet()) {
            for(final ApplicationState as2 : e2.getKey()) {
               as2.setUnit(unitPlainNameMapping.get(as2.getUnitName()));
            }
         }

      }

      return o;
   }

   @Override
   public IParetoQTableAccessor<List<ApplicationState>, List<ApplicationState>> loadParetoQTable(final String inputSrc,
         final Map<String, Unit> unitMapping) {

      ParetoQTable<List<ApplicationState>, List<ApplicationState>> o = null;
      FileInputStream fis = null;
      ObjectInputStream in = null;
      try {
         fis = new FileInputStream(inputSrc);
         in = new ObjectInputStream(fis);
         o = (ParetoQTable<List<ApplicationState>, List<ApplicationState>>) in.readObject();
         in.close();
      } catch(final IOException ex) {
         ex.printStackTrace();
      } catch(final ClassNotFoundException ex) {
         ex.printStackTrace();
      }

      final Map<String, Unit> unitPlainNameMapping = new HashMap<>();
      for(final Entry<String, Unit> e : unitMapping.entrySet()) {
         final String[] strings = e.getKey().split("::");
         unitPlainNameMapping.put(strings[strings.length - 1], e.getValue());
      }

      for(final Entry<List<ApplicationState>, Map<List<ApplicationState>, ParetoQState>> e : o.getTable().entrySet()) {
         final List<ApplicationState> aList = e.getKey();
         for(final ApplicationState as : aList) {
            as.setUnit(unitPlainNameMapping.get(as.getUnitName()));
         }
         for(final Entry<List<ApplicationState>, ParetoQState> e2 : e.getValue().entrySet()) {
            for(final ApplicationState as2 : e2.getKey()) {
               as2.setUnit(unitPlainNameMapping.get(as2.getUnitName()));
            }
         }

      }

      return o;
   }

   @Override
   public ISOQTableAccessor<List<ApplicationState>, List<ApplicationState>> loadSOQTable(final String inputSrc,
         final Map<String, Unit> unitMapping) {
      SOQTable<List<ApplicationState>, List<ApplicationState>> o = null;
      FileInputStream fis = null;
      ObjectInputStream in = null;
      try {
         fis = new FileInputStream(inputSrc);
         in = new ObjectInputStream(fis);
         o = (SOQTable<List<ApplicationState>, List<ApplicationState>>) in.readObject();
         in.close();
      } catch(final IOException ex) {
         ex.printStackTrace();
      } catch(final ClassNotFoundException ex) {
         ex.printStackTrace();
      }

      final Map<String, Unit> unitPlainNameMapping = new HashMap<>();
      for(final Entry<String, Unit> e : unitMapping.entrySet()) {
         final String[] strings = e.getKey().split("::");
         unitPlainNameMapping.put(strings[strings.length - 1], e.getValue());
      }

      for(final Entry<List<ApplicationState>, Map<List<ApplicationState>, Double>> e : o.getTable().entrySet()) {
         final List<ApplicationState> aList = e.getKey();
         for(final ApplicationState as : aList) {
            as.setUnit(unitPlainNameMapping.get(as.getUnitName()));
         }
         for(final Entry<List<ApplicationState>, Double> e2 : e.getValue().entrySet()) {
            for(final ApplicationState as2 : e2.getKey()) {
               as2.setUnit(unitPlainNameMapping.get(as2.getUnitName()));
            }
         }

      }

      return o;
   }

   @Override
   public S newTransformationSolution(final S s) {
      final TransformationSolution ts = (TransformationSolution) s;
      final TransformationSolution newTs = new TransformationSolution(ts.getSourceGraph(), ts.getNumberOfVariables(),
            ts.getNumberOfObjectives(), ts.getNumberOfConstraints());
      newTs.execute();
      return (S) newTs;
   }

   private ApplicationState unitApplicationToApplicationState(final UnitApplication ua) {
      Map<String, Object> params = new HashMap<>();
      final List<RuleApplication> appliedRules = new ArrayList<>();
      if(ua instanceof UnitApplicationVariable) {
         params = this.extractParameters(((UnitApplicationVariable) ua).getAssignment());
         appliedRules.addAll(((UnitApplicationVariable) ua).getAppliedRules());
      } else if(ua instanceof RuleApplicationVariable) {
         final RuleApplicationVariable[] rarr = { (RuleApplicationVariable) ua };
         params = this.extractParameters(rarr);
      }

      final ApplicationState a = new ApplicationState(ua.getUnit(), params, appliedRules);
      return a;
   }

   @Override
   public void writeQTableToDisk(final IQTableAccessor<List<ApplicationState>, List<ApplicationState>> qTable,
         final String outputSrc) {

      FileOutputStream fos = null;
      ObjectOutputStream out = null;
      try {
         fos = new FileOutputStream(outputSrc);
         out = new ObjectOutputStream(fos);
         out.writeObject(qTable);
         out.close();
      } catch(final IOException ex) {
         ex.printStackTrace();
      }

   }

}
