package com.enonic.wem.admin.rpc.util;

import java.util.Locale;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;

final class LocaleJsonResult
    extends JsonResult
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
