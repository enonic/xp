package com.enonic.xp.core.impl.media;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.net.MediaType;

import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.media.MediaTypeProvider;

@Component(immediate = true, configurationPid = "com.enonic.xp.media")
public final class MediaTypesLoader
    implements MediaTypeProvider
{
    private final static Logger LOG = LoggerFactory.getLogger( MediaTypesLoader.class );

    private final Map<String, MediaType> types;

    public MediaTypesLoader()
    {
        this.types = Maps.newHashMap();
    }

    @Activate
    public void activate( final Map<String, String> config )
    {
        this.types.clear();
        addAll( ConfigBuilder.create().
            addAll( config ).
            build().
            subConfig( "ext." ).
            asMap() );
    }

    private void addAll( final Map<String, String> config )
    {
        config.forEach( this::add );
    }

    private void add( final String ext, final String type )
    {
        try
        {
            this.types.put( ext, MediaType.parse( type ) );
        }
        catch ( final Exception e )
        {
            LOG.error( "Failed to add mapping [" + ext + " -> " + type + "]: " + e.getMessage() );
        }
    }

    @Override
    public MediaType fromExt( final String ext )
    {
        return this.types.get( ext );
    }

    @Override
    public Map<String, MediaType> asMap()
    {
        return this.types;
    }
}
