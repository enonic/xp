package com.enonic.wem.api.resource;

import java.io.File;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Map;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import com.enonic.wem.api.module.ModuleKey;

final class MockUrlStreamHandlerFactory
    implements URLStreamHandlerFactory, ResourceUrlRegistry
{
    private final Map<ModuleKey, URL> urlMapping;

    public MockUrlStreamHandlerFactory()
    {
        this.urlMapping = Maps.newHashMap();
    }

    @Override
    public URL getUrl( final ResourceKey resourceKey )
    {
        final URL baseUrl = this.urlMapping.get( resourceKey.getModule() );
        if ( baseUrl == null )
        {
            return null;
        }

        try
        {
            return new URL( combineUrl( baseUrl.toString(), resourceKey.getPath() ) );
        }
        catch ( final Exception e )
        {
            throw Throwables.propagate( e );
        }
    }

    private String combineUrl( final String baseUrl, final String path )
    {
        if ( baseUrl.endsWith( "/" ) )
        {
            return baseUrl + path.substring( 1 );
        }
        else
        {
            return baseUrl + path;
        }
    }

    @Override
    public void register( final ModuleKey moduleKey, final URL baseUrl )
    {
        this.urlMapping.put( moduleKey, baseUrl );
    }

    @Override
    public void register( final ModuleKey moduleKey, final File baseDir )
    {
        try
        {
            register( moduleKey, baseDir.toURI().toURL() );
        }
        catch ( final Exception e )
        {
            throw Throwables.propagate( e );
        }
    }

    @Override
    public URLStreamHandler createURLStreamHandler( final String protocol )
    {
        if ( "module".equals( protocol ) )
        {
            return new MockUrlStreamHandler( this );
        }

        return null;
    }
}
