package com.enonic.wem.core.content.config.field;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;

public class ConfigItemsJsonGenerator
{
    public static void generate( ConfigItems configItems, JsonGenerator g )
        throws IOException
    {
        g.writeArrayFieldStart( "items" );
        for ( ConfigItem configItem : configItems )
        {
            configItem.getJsonGenerator().generate( configItem, g );
        }
        g.writeEndArray();
    }
}
