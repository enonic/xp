package com.enonic.xp.impl.common;

import com.enonic.xp.app.ApplicationKey;

public final class ApiMountResolver
{
    private static final int APPLICATION_KEY_INDEX = 0;

    private static final int API_KEY_INDEX = 1;

    private final String[] apiMountParts;

    public ApiMountResolver( final String apiMount )
    {
        this.apiMountParts = apiMount.split( ":", 2 );
    }

    public ApplicationKey resolveApplicationKey()
    {
        return apiMountParts.length != 1 ? resolveApplicationKey( apiMountParts[APPLICATION_KEY_INDEX].trim() ) : null;
    }

    public String resolveApiKey()
    {
        final String trimmedApiKey = apiMountParts.length == 1 ? apiMountParts[0].trim() : apiMountParts[API_KEY_INDEX].trim();
        return trimmedApiKey.isBlank() ? null : trimmedApiKey;
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
