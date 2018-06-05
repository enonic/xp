package com.enonic.xp.ignite.impl;

import org.apache.ignite.Ignite;

public class IgniteAdminClientImpl
    implements IgniteAdminClient
{
    private final Ignite ignite;

    public IgniteAdminClientImpl( final Ignite ignite )
    {
        this.ignite = ignite;
    }

    @Override
    public Ignite getIgnite()
    {
        return this.ignite;
    }
}
