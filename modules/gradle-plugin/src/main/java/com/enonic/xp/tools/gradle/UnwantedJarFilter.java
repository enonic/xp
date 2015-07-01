package com.enonic.xp.tools.gradle;

import java.util.HashMap;
import java.util.Map;

import org.gradle.api.artifacts.Configuration;

public final class UnwantedJarFilter
{
    private final Configuration config;

    public UnwantedJarFilter( final Configuration config )
    {
        this.config = config;
    }

    public Configuration filter()
    {
        final Configuration result = this.config.copy();

        addExclude( result, "org.slf4j", null );
        addExclude( result, "com.enonic.xp", "core-api" );
        addExclude( result, "com.enonic.xp", "web-api" );
        addExclude( result, "com.enonic.xp", "portal-api" );
        addExclude( result, "com.google.guava", "guava" );

        return result;
    }

    private void addExclude( final Configuration result, final String group, final String module )
    {
        final Map<String, String> map = new HashMap<>();
        if ( group != null )
        {
            map.put( "group", group );
        }

        if ( module != null )
        {
            map.put( "module", module );
        }

        result.exclude( map );
    }
}
