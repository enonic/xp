package com.enonic.xp.repo.impl.config;

import java.nio.file.Path;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;

@Component(service = RepoConfigurationDynamic.class, configurationPid = "com.enonic.xp.repo")
public final class RepoConfigurationDynamic
{
    private volatile Configuration config;

    @Activate
    @Modified
    public void activate( final Map<String, String> map )
    {
        this.config =
            new ConfigInterpolator().interpolate( ConfigBuilder.create().load( getClass(), "default.properties" ).addAll( map ).build() );
    }

    public Path getDumpsDir()
    {
        return Path.of( this.config.get( "dumps.dir" ) );
    }
}
