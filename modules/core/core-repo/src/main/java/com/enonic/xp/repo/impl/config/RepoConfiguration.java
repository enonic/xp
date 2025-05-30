package com.enonic.xp.repo.impl.config;

import java.nio.file.Path;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.core.internal.MemoryLimitParser;

@Component(service = RepoConfiguration.class, configurationPid = "com.enonic.xp.repo")
public final class RepoConfiguration
{
    private final Configuration config;

    @Activate
    public RepoConfiguration( final Map<String, String> map )
    {
        this.config =
            new ConfigInterpolator().interpolate( ConfigBuilder.create().load( getClass(), "default.properties" ).addAll( map ).build() );
    }

    public Path getSnapshotsDir()
    {
        return Path.of( this.config.get( "snapshots.dir" ) );
    }

    public long cacheCapacity()
    {
        return MemoryLimitParser.maxHeap().parse( this.config.get( "cache.capacity" ) );
    }
}
