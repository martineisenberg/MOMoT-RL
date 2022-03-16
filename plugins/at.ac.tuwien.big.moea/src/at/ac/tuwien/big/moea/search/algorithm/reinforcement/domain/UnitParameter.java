package at.ac.tuwien.big.moea.search.algorithm.reinforcement.domain;

import java.util.Map;

public class UnitParameter {
   private String unitName;
   private Map<String, Object> parameterValues;
   boolean applyRepeatedlyIfPossible;

   public UnitParameter(final String unitName, final Map<String, Object> parameterValues) {
      this.unitName = unitName;
      this.parameterValues = parameterValues;
      this.applyRepeatedlyIfPossible = false;
   }

   public UnitParameter(final String unitName, final Map<String, Object> parameterValues,
         final boolean applyRepeatedlyIfPossible) {
      this(unitName, parameterValues);
      this.applyRepeatedlyIfPossible = applyRepeatedlyIfPossible;
   }

   public Map<String, Object> getParameterValues() {
      return parameterValues;
   }

   public String getUnitName() {
      return unitName;
   }

   public boolean isApplyRepeatedlyIfPossible() {
      return applyRepeatedlyIfPossible;
   }

   public void setApplyRepeatedlyIfPossible(final boolean applyRepeatedlyIfPossible) {
      this.applyRepeatedlyIfPossible = applyRepeatedlyIfPossible;
   }

   public void setParameterValues(final Map<String, Object> parameterValues) {
      this.parameterValues = parameterValues;
   }

   public void setUnit(final String unitName) {
      this.unitName = unitName;
   }

   public void setUnitName(final String unitName) {
      this.unitName = unitName;
   }

}
