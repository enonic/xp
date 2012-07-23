package com.enonic.wem.core.content.config.field;


import java.io.IOException;

import org.codehaus.jackson.JsonNode;

public class MultipleJsonParser
{
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
