package com.enonic.wem.core.content.config.field.type;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;

public class DropdownConfigJsonGenerator
    extends FieldTypeConfigJsonGenerator
{
    public static final DropdownConfigJsonGenerator DEFAULT = new DropdownConfigJsonGenerator();

    public void generate( FieldTypeConfig config, JsonGenerator g )
        throws IOException
    {
        DropdownConfig dropdownConfig = (DropdownConfig) config;
        g.writeStartObject();
        g.writeArrayFieldStart( "options" );
        for ( DropdownConfig.Option option : dropdownConfig.getOptions() )
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
