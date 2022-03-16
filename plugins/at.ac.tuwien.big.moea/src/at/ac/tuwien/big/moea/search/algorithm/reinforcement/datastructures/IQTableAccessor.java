package at.ac.tuwien.big.moea.search.algorithm.reinforcement.datastructures;

public interface IQTableAccessor<S, A> {
   public boolean addStateIfNotExists(final S s);

}
