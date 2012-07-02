package com.enonic.wem.core.content;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;

public class SubTypeEntriesJsonGenerator
    extends FieldEntryJsonGenerator
{
    public final static SubTypeEntriesJsonGenerator DEFAULT = new SubTypeEntriesJsonGenerator();

    @Override
    public void generate( final FieldEntry fieldEntry, final JsonGenerator g )
        throws IOException
    {
        final SubTypeEntries subTypeEntries = (SubTypeEntries) fieldEntry;

        g.writeStartObject();
        g.writeStringField( "path", subTypeEntries.getPath().toString() );
        FieldEntriesJsonGenerator.generate( subTypeEntries.getFieldEntries(), g );
        g.writeEndObject();
    }
}
