package com.enonic.xp.xml.parser;

import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.xml.DomElement;

public final class ApiMountDescriptorParser
{
    private static final String API_DESCRIPTOR_TAG_NAME = "api";

    private static final int APPLICATION_KEY_INDEX = 0;

    private static final int API_KEY_INDEX = 1;

    private final ApplicationKey currentApplication;

    private final DomElement apisElement;

    public ApiMountDescriptorParser( final ApplicationKey currentApplication, final DomElement apisElement )
    {
        this.currentApplication = currentApplication;
        this.apisElement = apisElement;
    }

    public DescriptorKeys parse()
    {
        if ( apisElement != null )
        {
            return DescriptorKeys.from(
                apisElement.getChildren( API_DESCRIPTOR_TAG_NAME ).stream().map( this::toDescriptorKey ).collect( Collectors.toList() ) );
        }
        return DescriptorKeys.empty();
    }

    private DescriptorKey toDescriptorKey( final DomElement apiElement )
    {
        final String apiMount = apiElement.getValue().trim();

        ApplicationKey applicationKey = currentApplication;
        String apiKey;

        if ( !apiMount.contains( ":" ) )
        {
            apiKey = apiMount;
        }
        else
        {
            final String[] parts = apiMount.split( ":", 2 );

            applicationKey = resolveApplicationKey( parts[APPLICATION_KEY_INDEX].trim() );
            apiKey = parts[API_KEY_INDEX].trim();
        }

        Preconditions.checkArgument( applicationKey != null, "applicationKey must be set." );
        Preconditions.checkArgument( !apiKey.isBlank(), "apiKey must be set." );

        return DescriptorKey.from( applicationKey, apiKey );
    }

    private ApplicationKey resolveApplicationKey( final String applicationKey )
    {
        try
        {
            return ApplicationKey.from( applicationKey );
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException( String.format( "Invalid applicationKey '%s'", applicationKey ), e );
        }
    }
}
