package com.enonic.wem.core.content.config.field.type;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;

public abstract class FieldTypeConfigJsonGenerator
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
}
