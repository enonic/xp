package com.enonic.wem.core.content.config;


import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonGenerator;

import com.enonic.wem.core.content.JsonFactoryHolder;
import com.enonic.wem.core.content.config.field.ConfigItemsJsonGenerator;

public class ContentTypeJsonGenerator
{
    public static String toJson( ContentType contentType )
    {
        try
        {
            StringWriter sw = new StringWriter();
            JsonGenerator g = JsonFactoryHolder.DEFAULT_FACTORY.createJsonGenerator( sw );
            g.useDefaultPrettyPrinter();
            g.writeStartObject();
            ConfigItemsJsonGenerator.generate( contentType.getConfigItems(), g );
            g.writeEndObject();
            g.close();
            sw.close();
            return sw.toString();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to generate json", e );
        }
    }
}
