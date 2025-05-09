package com.enonic.xp.core.impl.app;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.ClassLoaderApplicationUrlResolver;
import com.enonic.xp.resource.Resource;

public class MockApplication
    implements ApplicationAdaptor
{
    private ApplicationKey key;

    private ApplicationUrlResolver urlResolver;

    private boolean started;

    private Configuration config;

    private Bundle bundle;

    @Override
    public ApplicationKey getKey()
    {
        return this.key;
    }

    public void setKey( final ApplicationKey key )
    {
        this.key = key;
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
    public boolean includesSystemVersion( final Version version )
    {
        return true;
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
        return this.bundle;
    }

    public void setBundle( final Bundle bundle )
    {
        this.bundle = bundle;
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

    public void setStarted( final boolean started )
    {
        this.started = started;
    }

    public void setResourcePath( final Path resourcePath )
    {
        final URL url;
        try
        {
            url = resourcePath.toUri().toURL();
        }
        catch ( MalformedURLException e )
        {
            throw new UncheckedIOException( e );
        }
        final URLClassLoader loader = new URLClassLoader( new URL[]{url}, null );
        this.urlResolver =
            new ClassLoaderApplicationUrlResolver( loader, bundle != null ? ApplicationKey.from( bundle ) : ApplicationKey.SYSTEM );
    }

    public ApplicationUrlResolver getUrlResolver()
    {
        return this.urlResolver;
    }

    @Override
    public Configuration getConfig()
    {
        return this.config != null ? this.config : ConfigBuilder.create().build();
    }

    public void setConfig( final Configuration config )
    {
        this.config = config;
    }

    @Override
    public boolean isSystem()
    {
        return false;
    }

    @Override
    public Set<String> getCapabilities()
    {
        return Set.of();
    }
}
