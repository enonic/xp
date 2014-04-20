package com.enonic.wem.core.mustache;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

public final class MustacheModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( MustacheService.class ).to( MustacheServiceImpl.class ).in( Singleton.class );
    }
}
