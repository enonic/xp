package com.enonic.wem.core.content;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;

public class FieldValueJsonGenerator
    extends FieldEntryJsonGenerator
{
    public final static FieldValueJsonGenerator DEFAULT = new FieldValueJsonGenerator();

    @Override
    public void generate( final FieldEntry fieldEntry, final JsonGenerator g )
        throws IOException
    {
        final FieldValue fieldValue = (FieldValue) fieldEntry;

        g.writeStartObject();
        g.writeStringField( "path", fieldValue.getPath().toString() );
        if ( fieldValue.getValue() != null )
        {
            g.writeStringField( "value", String.valueOf( fieldValue.getValue() ) );
        }
        else
        {
            g.writeNullField( "value " );
        }

        g.writeEndObject();
    }
}
