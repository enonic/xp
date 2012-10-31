package com.enonic.wem.api.content.type.formitem.inputtype;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

public abstract class AbstractInputTypeConfigSerializerJson
{
    public void generate( InputTypeConfig config, JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeFieldName( config.getClass().getName() );
        generateConfig( config, g );
        g.writeEndObject();
    }

    public abstract void generateConfig( InputTypeConfig config, JsonGenerator g )
        throws IOException;


    public abstract InputTypeConfig parseConfig( final JsonNode inputTypeConfigNode );
}
