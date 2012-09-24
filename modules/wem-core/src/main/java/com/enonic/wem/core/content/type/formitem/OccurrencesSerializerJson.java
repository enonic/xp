package com.enonic.wem.core.content.type.formitem;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.api.content.type.formitem.Occurrences;

public class OccurrencesSerializerJson
{
    public static void generate( final Occurrences occurrences, final JsonGenerator g )
        throws IOException
    {
        g.writeFieldName( "occurrences" );
        if ( occurrences == null )
        {
            g.writeNull();
        }
        else
        {
            g.writeStartObject();
            g.writeNumberField( "minimum", occurrences.getMinimum() );
            g.writeNumberField( "maximum", occurrences.getMaximum() );
            g.writeEndObject();
        }
    }

    public static Occurrences parse( final JsonNode node )
    {
        int minEntries = 0;
        int maxEntries = 0;

        JsonNode minEntriesNode = node.get( "minimum" );
        if ( minEntriesNode != null )
        {
            minEntries = minEntriesNode.getIntValue();
        }
        JsonNode maxEntriesNode = node.get( "maximum" );
        if ( maxEntriesNode != null )
        {
            maxEntries = maxEntriesNode.getIntValue();
        }

        return new Occurrences( minEntries, maxEntries );
    }
}
