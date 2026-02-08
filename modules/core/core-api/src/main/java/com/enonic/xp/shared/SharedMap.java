package com.enonic.xp.shared;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Shared Map is similar to other Map, but its instances are shared across all applications and even cluster nodes.
 * Due to distributed nature of Shared Map it is recommended to use only Standard Serializable Java classes as keys and values. Other types may not work as expected.
 * <p>
 * WARNING: SharedMap has no guarantees for value mutability or immutability. Make sure you don't modify the values in-place.
 *
 * @param <K> the type of keys maintained by this map.
 * @param <V> the type of values maintained by this map.
 */
public interface SharedMap<K, V>
{
    /**
     * Returns the value to which the specified key is mapped, or null if this map contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
     */
    V get( K key );

    /**
     * Removes the mapping for the key from this map if it is present.
     *
     * @param key the key whose associated value is to be removed
     */
    void delete( K key );

    /**
     * Equivalent of calling {@link #set(Object, Object, int)}  set(k, v, -1)}
     *
     * @param key   key of the entry
     * @param value value of the entry
     */
    void set( K key, V value );

    /**
     * Puts an entry into this map with a given time to live (TTL).
     * If value is null, the existing entry will be removed
     *
     * @param key        key of the entry
     * @param value      value of the entry
     * @param ttlSeconds maximum time to live in seconds for this entry to stay in the map. (0 means infinite, negative means map provider (for instance Hazelcast) default or infinite if map provider does not have own TTL setting)
     */
    void set( K key, V value, int ttlSeconds );

    /**
     * Equivalent of calling {@link #modify(Object, Function, int)}  modify(k, f, -1)}
     *
     * @param key      key of the entry
     * @param modifier mapping function that accepts the existing mapped value (or null, if there is no associated mapping).
     *                 The returned value replaces the existing mapped value for the specified key.
     *                 If returned value is null then the value is removed from the map
     * @return the new value to which the specified key is mapped, or null if this map no longer contains mapping for the key
     */
    V modify( K key, Function<V, V> modifier );

    /**
     * Attempts to compute a mapping for the specified key and its current mapped value.
     * The mapping is done atomically with other {@code modify} calls.
     *
     * @param key        key of the entry
     * @param modifier   mapping function that accepts the existing mapped value (or null, if there is no associated mapping).
     *                   The returned value replaces the existing mapped value for the specified key.
     *                   If returned value is null then the value is removed from the map
     * @param ttlSeconds maximum time to live in seconds for this entry to stay in the map. (0 means infinite, negative means map provider (for instance Hazelcast) default or infinite if map provider does not have own TTL setting)
     * @return the new value to which the specified key is mapped, or null if this map no longer contains mapping for the key
     */
    V modify( K key, Function<V, V> modifier, int ttlSeconds );

    /**
     * Removes all entries from this map that match the given predicate.
     * The predicate is evaluated for each entry in the map, and entries for which
     * the predicate returns true are removed.
     *
     * @param predicate the predicate to apply to each entry. The predicate receives
     *                  the key and value as parameters and should return true for entries to be removed
     */
    void removeAll( Predicate<Entry<K, V>> predicate );

    /**
     * Represents a key-value pair in the map.
     *
     * @param <K> the type of keys
     * @param <V> the type of values
     */
    interface Entry<K, V>
    {
        K getKey();

        V getValue();
    }
}
