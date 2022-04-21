package com.enonic.xp.shared;

public interface SharedMapService
{
    /**
     * @param name name of a shared map
     * @param <K>  the type of keys maintained by this map. Can only be standard Java classes.
     * @param <V>  the type of values maintained by this map. Can only be standard Java classes.
     * @return map instance that is shared across all XP nodes
     */
    <K, V> SharedMap<K, V> getSharedMap( String name );
}
