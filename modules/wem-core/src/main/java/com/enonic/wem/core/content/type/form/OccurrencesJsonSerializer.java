package com.enonic.wem.core.content.type.form;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.NullNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.type.form.Occurrences;
import com.enonic.wem.core.content.AbstractJsonSerializer;

class OccurrencesJsonSerializer
    extends AbstractJsonSerializer<Occurrences>
{
    public JsonNode serialize( final Occurrences occurrences, final ObjectMapper objectMapper )
    {
        if ( occurrences != null )
        {
            final ObjectNode jsonObject = objectMapper.createObjectNode();
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
