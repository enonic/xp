package com.enonic.xp.core.impl.app;

import java.net.URL;
import java.net.URLClassLoader;
import java.time.Instant;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.ClassLoaderApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.PrefixApplicationUrlResolver;

public class MockApplication
    implements Application
{
    private ApplicationKey key;

    private ApplicationUrlResolver urlResolver;

    private boolean started;

    @Override
    public ApplicationKey getKey()
    {
        return this.key;
    }

    @Override
    public Version getVersion()
    {
        return Version.parseVersion( "1.0.0" );
    }

    @Override
    public String getDisplayName()
    {
        return "defaultDisplayName";
    }

    @Override
    public String getSystemVersion()
    {
        return "1.0.1";
    }

    @Override
    public String getMaxSystemVersion()
    {
        return "1.0.2";
    }

    @Override
    public String getMinSystemVersion()
    {
        return "1.0.0";
    }

    @Override
    public String getUrl()
    {
        return null;
    }

    @Override
    public String getVendorName()
    {
        return null;
    }

    @Override
    public String getVendorUrl()
    {
        return null;
    }

    @Override
    public Bundle getBundle()
    {
        return null;
    }

    @Override
    public ClassLoader getClassLoader()
    {
        return null;
    }

    @Override
    public Instant getModifiedTime()
    {
        return null;
    }

    @Override
    public boolean isStarted()
    {
        return this.started;
    }

    @Override
    public Set<String> getFiles()
    {
        return this.urlResolver.findFiles();
    }

    @Override
    public URL resolveFile( final String path )
    {
        return this.urlResolver.findUrl( path );
    }

    public void setKey( final ApplicationKey key )
    {
        this.key = key;
    }

    public void setUrlResolver( final ApplicationUrlResolver urlResolver )
    {
        this.urlResolver = urlResolver;
    }

    public void setClassLoaderUrlResolver( final ClassLoader loader, final String prefix )
    {
        final ApplicationUrlResolver resolver = new ClassLoaderApplicationUrlResolver( loader );
        final ApplicationUrlResolver prefixedReslver = new PrefixApplicationUrlResolver( resolver, prefix );
        setUrlResolver( prefixedReslver );
    }

    public void setUrlResolver( final URL root, final String prefix )
    {
        setClassLoaderUrlResolver( new URLClassLoader( new URL[]{root}, null ), prefix );
    }

    public void setStarted( final boolean started )
    {
        this.started = started;
    }

    @Override
    public boolean hasSiteDescriptor()
    {
        return true;
    }
}
