package com.enonic.wem.core.content.config.field.type;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;

public class RadioButtonsConfigJsonGenerator
    extends FieldTypeConfigJsonGenerator
{
    public static final RadioButtonsConfigJsonGenerator DEFAULT = new RadioButtonsConfigJsonGenerator();

    public void generate( FieldTypeConfig config, JsonGenerator g )
        throws IOException
    {
        RadioButtonsConfig radioButtonsConfig = (RadioButtonsConfig) config;
        g.writeStartObject();
        g.writeArrayFieldStart( "options" );
        for ( RadioButtonsConfig.Option option : radioButtonsConfig.getOptions() )
        {
            g.writeStartObject();
            g.writeStringField( "label", option.getLabel() );
            g.writeStringField( "value", option.getValue() );
            g.writeEndObject();
        }
        g.writeEndArray();
        g.writeEndObject();
    }
}
