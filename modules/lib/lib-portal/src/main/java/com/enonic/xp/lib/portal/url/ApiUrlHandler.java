package com.enonic.xp.lib.portal.url;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.ApiUrlParams;
import com.enonic.xp.script.ScriptValue;

public final class ApiUrlHandler
    extends AbstractUrlHandler
{
    private static final Set<String> VALID_URL_PROPERTY_KEYS = Set.of( "application", "api", "type", "params" );

    private String path;

    private List<String> pathSegments;

    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final ApiUrlParams params =
            new ApiUrlParams().portalRequest( this.request ).setAsMap( map ).path( this.path ).pathSegments( this.pathSegments );
        return this.urlService.apiUrl( params );
    }

    public void setPath( final Object value )
    {
        if ( value instanceof ScriptValue && ( (ScriptValue) value ).isArray() )
        {
            this.pathSegments = ( (ScriptValue) value ).getArray( String.class );
        }
        else if ( value instanceof String )
        {
            this.path = (String) value;
        }
        else
        {
            throw new IllegalArgumentException( "Invalid path value" );
        }
    }

    @Override
    protected boolean isValidParam( final String param )
    {
        return VALID_URL_PROPERTY_KEYS.contains( param );
    }
}
