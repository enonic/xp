package com.enonic.wem.core.content.config.field;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;

public class MultipleJsonGenerator
{
    public static void generate( final Multiple multiple, final JsonGenerator g )
        throws IOException
    {
        g.writeFieldName( "multiple" );
        if ( multiple == null )
        {
            g.writeNull();
        }
        else
        {
            g.writeStartObject();
            g.writeNumberField( "minEntries", multiple.getMinimumEntries() );
            g.writeNumberField( "maxEntries", multiple.getMaximumEntries() );
            g.writeEndObject();
        }
    }
}
