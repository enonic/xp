package com.enonic.xp.core.impl.export;

import java.nio.file.Path;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;

@Component(service = {ExportConfiguration.class}, configurationPid = "com.enonic.xp.export")
public class ExportConfiguration
{
    private Configuration config;

    public Path getExportsDir()
    {
        return Path.of( this.config.get( "exports.dir" ) );
    }

    @Activate
    public void activate( final Map<String, String> map )
    {
        this.config = ConfigBuilder.create().load( getClass(), "default.properties" ).addAll( map ).build();

        this.config = new ConfigInterpolator().interpolate( this.config );
    }
}
