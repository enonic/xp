package com.enonic.wem.core.module;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.url.AbstractURLStreamHandlerService;

import com.google.common.base.Strings;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;

@Singleton
public final class ModuleURLStreamHandler
    extends AbstractURLStreamHandlerService
{
    @Inject
    protected BundleContext bundleContext;

    @Override
    public URLConnection openConnection( final URL url )
        throws IOException
    {
        final String path = url.getPath();
        if ( Strings.isNullOrEmpty( path ) )
        {
            throw new MalformedURLException( "Path can not be null or empty." );
        }

        final ModuleResourceKey key = ModuleResourceKey.from( path );
        final Bundle bundle = findBundle( key.getModule() );

        return bundle.getResource( key.getPath() ).openConnection();
    }

    // TODO: Need to cache this in some way. Use a modulekeyresolver?
    private Bundle findBundle( final ModuleKey key )
        throws IOException
    {
        for ( final Bundle bundle : this.bundleContext.getBundles() )
        {
            final String str = bundle.getSymbolicName() + "-" + bundle.getVersion().toString();
            if ( key.toString().equals( str ) )
            {
                return bundle;
            }
        }

        throw new IOException( "Module [" + key.toString() + "] does not exist" );
    }
}
