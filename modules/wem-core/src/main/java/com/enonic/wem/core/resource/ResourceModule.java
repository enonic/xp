package com.enonic.wem.core.resource;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.api.resource.ResourceService;

public final class ResourceModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( ResourceService.class ).to( ResourceServiceImpl.class ).in( Singleton.class );
    }
}
