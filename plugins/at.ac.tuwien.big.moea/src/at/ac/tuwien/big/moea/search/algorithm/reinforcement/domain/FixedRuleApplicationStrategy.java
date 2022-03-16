package at.ac.tuwien.big.moea.search.algorithm.reinforcement.domain;

import java.util.ArrayList;
import java.util.List;

public class FixedRuleApplicationStrategy {
   private UnitParameter distributionSampleRule;
   private List<UnitParameter> optionalSubsequentRules;
   private List<UnitParameter> optionalPrecedentRules;

   public FixedRuleApplicationStrategy() {
      this.optionalSubsequentRules = new ArrayList<>();
   }

   public UnitParameter getDistributionSampleRule() {
      return distributionSampleRule;
   }

   public List<UnitParameter> getOptionalPrecedentRules() {
      return optionalPrecedentRules;
   }

   public List<UnitParameter> getOptionalSubsequentRules() {
      return optionalSubsequentRules;
   }

   public void setDistributionSampleRule(final UnitParameter distributionSampleRule) {
      this.distributionSampleRule = distributionSampleRule;
   }

   public void setOptionalPrecedentRules(final List<UnitParameter> optionalPrecedentRules) {
      this.optionalPrecedentRules = optionalPrecedentRules;
   }

   public void setOptionalSubsequentRules(final List<UnitParameter> optionalSubsequentRules) {
      this.optionalSubsequentRules = optionalSubsequentRules;
   }

}
