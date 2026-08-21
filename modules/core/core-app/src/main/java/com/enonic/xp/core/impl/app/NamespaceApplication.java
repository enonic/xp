package com.enonic.xp.core.impl.app;

import java.time.Instant;
import java.util.Set;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.Namespace;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.server.VersionInfo;
import com.enonic.xp.util.Version;

final class NamespaceApplication
    implements Application
{
    private final Namespace namespace;

    NamespaceApplication( final Namespace namespace )
    {
        this.namespace = namespace;
    }

    @Override
    public ApplicationKey getKey()
    {
        return namespace.getKey();
    }

    @Override
    public Version getVersion()
    {
        return Version.emptyVersion;
    }

    @Override
    public String getSystemVersion()
    {
        return VersionInfo.get().getVersion();
    }

    @Override
    public String getMaxSystemVersion()
    {
        return VersionInfo.get().getVersion();
    }

    @Override
    public String getMinSystemVersion()
    {
        return VersionInfo.get().getVersion();
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
    public Set<String> getCapabilities()
    {
        return Set.of();
    }

    @Override
    public boolean isStarted()
    {
        return true;
    }

    @Override
    public Configuration getConfig()
    {
        return ConfigBuilder.create().build();
    }

    @Override
    public boolean isSystem()
    {
        return false;
    }
}
