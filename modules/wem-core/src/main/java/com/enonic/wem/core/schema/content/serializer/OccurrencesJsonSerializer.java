package com.enonic.wem.core.schema.content.serializer;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.api.schema.content.form.Occurrences;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;

import static com.enonic.wem.api.schema.content.form.Occurrences.newOccurrences;

class OccurrencesJsonSerializer
    extends AbstractJsonSerializer<Occurrences>
{

    OccurrencesJsonSerializer( final ObjectMapper objectMapper )
    {
        super( objectMapper );
    }

    public JsonNode serialize( final Occurrences occurrences )
    {
        if ( occurrences != null )
        {
            final ObjectNode jsonObject = objectMapper().createObjectNode();
            jsonObject.put( "minimum", occurrences.getMinimum() );
            jsonObject.put( "maximum", occurrences.getMaximum() );
            return jsonObject;
        }
        else
        {
            return NullNode.getInstance();
        }
    }

    public Occurrences parse( final JsonNode node )
    {
        int minEntries = 0;
        int maxEntries = 0;

        JsonNode minEntriesNode = node.get( "minimum" );
        if ( minEntriesNode != null )
        {
            minEntries = minEntriesNode.intValue();
        }
        JsonNode maxEntriesNode = node.get( "maximum" );
        if ( maxEntriesNode != null )
        {
            maxEntries = maxEntriesNode.intValue();
        }

        return newOccurrences().minimum( minEntries ).maximum( maxEntries ).build();
    }
}
