package com.enonic.wem.core.content.type.formitem.fieldtype;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

public abstract class AbstractComponentTypeConfigSerializerJson
{
    public void generate( ComponentTypeConfig config, JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeFieldName( config.getClass().getName() );
        generateConfig( config, g );
        g.writeEndObject();
    }

    public abstract void generateConfig( ComponentTypeConfig config, JsonGenerator g )
        throws IOException;


    public abstract ComponentTypeConfig parseConfig( final JsonNode componentTypeConfigNode );
}
