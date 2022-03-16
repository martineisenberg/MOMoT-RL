package at.ac.tuwien.big.momot.search.algorithm.reinforcement.datastructures;

public interface IModifier<T> {
   T modify(T obj);

   T reverseModify(T obj);
}
