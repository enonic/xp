package com.enonic.xp.core.impl.export;

import java.nio.file.Path;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;

@Component(service = ExportConfigurationDynamic.class, configurationPid = "com.enonic.xp.export")
public class ExportConfigurationDynamic
{
    private volatile Configuration config;

    public Path getExportsDir()
    {
        return Path.of( this.config.get( "exports.dir" ) );
    }

    @Activate
    @Modified
    public void activate( final Map<String, String> map )
    {
        this.config =
            new ConfigInterpolator().interpolate( ConfigBuilder.create().load( getClass(), "default.properties" ).addAll( map ).build() );
    }
}
