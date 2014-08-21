package com.enonic.wem.portal.internal.restlet;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.restlet.resource.Finder;
import org.restlet.resource.ServerResource;

import com.google.inject.Injector;
import com.google.inject.Key;

@Singleton
final class FinderFactoryImpl
    implements FinderFactory
{
    private final Injector injector;

    @Inject
    public FinderFactoryImpl( final Injector injector )
    {
        this.injector = injector;
    }

    @Override
    public Finder finder( final Class<? extends ServerResource> type )
    {
        return new ResourceKeyFinder<>( this.injector, Key.get( type ) );
    }
}
