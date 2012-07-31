package com.enonic.wem.core.content.type.configitem;


import java.io.IOException;
import java.util.Iterator;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

public class ConfigItemsSerializerJson
{
    public static void generate( ConfigItems configItems, JsonGenerator g )
        throws IOException
    {
        g.writeArrayFieldStart( "items" );
        for ( ConfigItem configItem : configItems )
        {
            ConfigItemSerializerJson.generate( configItem, g );
        }
        g.writeEndArray();
    }

    public static ConfigItems parse( final JsonNode configItemsNode )
        throws IOException
    {
        final ConfigItems configItems = new ConfigItems();
        final Iterator<JsonNode> configItemIt = configItemsNode.getElements();
        while ( configItemIt.hasNext() )
        {
            configItems.addConfigItem( ConfigItemSerializerJson.parse( configItemIt.next() ) );
        }

        return configItems;
    }
}
