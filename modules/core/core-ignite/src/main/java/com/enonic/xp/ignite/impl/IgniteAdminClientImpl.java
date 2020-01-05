package com.enonic.xp.ignite.impl;

import org.apache.ignite.Ignite;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true)
public class IgniteAdminClientImpl
    implements IgniteAdminClient
{
    private final Ignite ignite;

    @Activate
    public IgniteAdminClientImpl( @Reference final Ignite ignite )
    {
        this.ignite = ignite;
    }

    @Override
    public Ignite getIgnite()
    {
        return this.ignite;
    }
}
