package com.enonic.wem.web.rest2.locale;

import java.util.Locale;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.rest2.common.JsonResult;

public final class LocaleResult
    extends JsonResult
{
    private final Locale[] list;

    public LocaleResult( final Locale... list )
    {
        this.list = list;
    }

    @Override
    public JsonNode toJson()
    {
        final ObjectNode json = objectNode();
        json.put( "total", this.list.length );

        final ArrayNode array = json.putArray( "locales" );
        for ( final Locale model : this.list )
        {
            array.add( toJson( model ) );
        }

        return json;
    }

    private ObjectNode toJson( final Locale model )
    {
        final ObjectNode json = objectNode();
        json.put( "id", model.toString() );
        json.put( "displayName", model.getDisplayName() );
        return json;
    }
}
