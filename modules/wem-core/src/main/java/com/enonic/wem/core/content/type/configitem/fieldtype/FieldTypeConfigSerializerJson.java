package com.enonic.wem.core.content.type.configitem.fieldtype;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

public abstract class FieldTypeConfigSerializerJson
{
    public void generateBase( FieldTypeConfig config, JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeFieldName( config.getClass().getName() );
        generate( config, g );
        g.writeEndObject();
    }

    public abstract void generate( FieldTypeConfig config, JsonGenerator g )
        throws IOException;


    public abstract FieldTypeConfig parse( final JsonNode jp );
}
