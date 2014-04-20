package com.enonic.wem.core.entity;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.api.entity.NodeService;

public final class EntityModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( NodeService.class ).to( NodeServiceImpl.class ).in( Singleton.class );
    }
}
