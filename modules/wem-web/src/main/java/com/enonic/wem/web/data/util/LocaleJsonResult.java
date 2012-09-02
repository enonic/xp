package com.enonic.wem.web.data.util;

import java.util.Locale;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.json.result.JsonSuccessResult;

final class LocaleJsonResult
    extends JsonSuccessResult
{
    private final Locale[] list;

    public LocaleJsonResult( final Locale... list )
    {
        this.list = list;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "total", this.list.length );

        final ArrayNode array = json.putArray( "locales" );
        for ( final Locale model : this.list )
        {
            serialize( array.addObject(), model );
        }
    }

    private void serialize( final ObjectNode json, final Locale model )
    {
        json.put( "id", model.toString() );
        json.put( "displayName", model.getDisplayName() );
    }
}
