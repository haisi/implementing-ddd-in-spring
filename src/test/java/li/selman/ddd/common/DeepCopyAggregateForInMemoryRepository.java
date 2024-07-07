package li.selman.ddd.common;

import org.jmolecules.ddd.types.AggregateRoot;
import org.jmolecules.ddd.types.Identifier;

public interface DeepCopyAggregateForInMemoryRepository {

  /**
   * To ensure that saved instance can not be updated after save through the reference.
   *
   * <p><code>
   * <T> deepClone(T object) {
   *     var serialized = serialize(object)
   *     T copy = deserialize(serialized)
   *     return copy;
   * }
   *
   * void save(myAggregate) {
   *     map.put(myAggregate.id, deepClone(myAggregate))
   * }
   * </code>
   *
   * @param object which is added to the repository before a deep copy
   * @return deep copy of object
   * @param <T> AggregateRoot
   * @param <ID> Identifier
   */
  <T extends AggregateRoot<T, ID>, ID extends Identifier> T deepCopy(T object);
}
