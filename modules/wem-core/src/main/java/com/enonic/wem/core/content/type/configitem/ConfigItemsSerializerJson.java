package com.enonic.wem.core.content.type.configitem;


import java.io.IOException;
import java.util.Iterator;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.core.content.JsonParsingException;

public class ConfigItemsSerializerJson
{
    private ConfigItemSerializerJson configItemSerializer = new ConfigItemSerializerJson();

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

    public ConfigItems parse( final JsonNode configItemsNode )
        throws IOException
    {
        final ConfigItems configItems = new ConfigItems();
        final Iterator<JsonNode> configItemIt = configItemsNode.getElements();
        while ( configItemIt.hasNext() )
        {
            try
            {
                final JsonNode configItemNode = configItemIt.next();
                configItems.addConfigItem( configItemSerializer.parse( configItemNode ) );
            }
            catch ( Exception e )
            {
                throw new JsonParsingException( "Failed to parse ConfigItem: " + configItemsNode.toString(), e );
            }
        }

        return configItems;
    }
}
