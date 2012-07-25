package com.enonic.wem.core.content.type.item;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

public class MultipleSerializerJson
{
    public static void generate( final Multiple multiple, final JsonGenerator g )
        throws IOException
    {
        g.writeFieldName( "multiple" );
        if ( multiple == null )
        {
            g.writeNull();
        }
        else
        {
            g.writeStartObject();
            g.writeNumberField( "minEntries", multiple.getMinimumEntries() );
            g.writeNumberField( "maxEntries", multiple.getMaximumEntries() );
            g.writeEndObject();
        }
    }

    public static Multiple parse( final JsonNode node )
        throws IOException
    {
        int minEntries = 0;
        int maxEntries = 0;

        JsonNode minEntriesNode = node.get( "minEntries" );
        if ( minEntriesNode != null )
        {
            minEntries = minEntriesNode.getIntValue();
        }
        JsonNode maxEntriesNode = node.get( "maxEntries" );
        if ( maxEntriesNode != null )
        {
            maxEntries = maxEntriesNode.getIntValue();
        }

        return new Multiple( minEntries, maxEntries );
    }
}
