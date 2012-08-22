package com.enonic.wem.core.content.type.configitem.fieldtype;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

public abstract class AbstractFieldTypeConfigSerializerJson
{
    public void generate( FieldTypeConfig config, JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeFieldName( config.getClass().getName() );
        generateConfig( config, g );
        g.writeEndObject();
    }

    public abstract void generateConfig( FieldTypeConfig config, JsonGenerator g )
        throws IOException;


    public abstract FieldTypeConfig parseConfig( final JsonNode fieldTypeConfigNode );
}
