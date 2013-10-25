package com.enonic.wem.core.hazelcast.store;

import java.util.Set;

public abstract class JdbcMapStore<K, V>
    extends StringBasedMapStore<K, V>
{
    private final String segment;

    public JdbcMapStore( final String segment )
    {
        this.segment = segment;
    }

    @Override
    protected void doStore( final String key, final String value )
    {
        // INSERT FROM store WHERE segment = ? AND key = ?
    }

    @Override
    protected void doDelete( final String key )
    {
        // DELETE FROM store WHERE segment = ? AND key = ?
    }

    @Override
    protected String doLoad( final String key )
    {
        return null;  // SELECT value FROM store WHERE segment = ? AND key = ?
    }

    @Override
    protected Set<String> doLoadAllKeys()
    {
        return null; // SELECT key FROM store WHERE segment = ?
    }
}
