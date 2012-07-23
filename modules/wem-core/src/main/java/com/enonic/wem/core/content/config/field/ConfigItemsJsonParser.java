package com.enonic.wem.core.content.config.field;


import java.io.IOException;
import java.util.Iterator;

import org.codehaus.jackson.JsonNode;

public class ConfigItemsJsonParser
{
    public static ConfigItems parse( final JsonNode configItemsNode )
        throws IOException
    {
        final ConfigItems configItems = new ConfigItems();
        final Iterator<JsonNode> configItemIt = configItemsNode.getElements();
        while ( configItemIt.hasNext() )
        {
            configItems.addField( ConfigItemJsonParser.parse( configItemIt.next() ) );
        }

        return configItems;
    }
}
