package com.enonic.wem.core.content;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;

public class FieldEntriesJsonGenerator
{
    public static void generate( FieldEntries fieldEntries, JsonGenerator g )
        throws IOException
    {
        g.writeArrayFieldStart( "fieldEntries" );
        for ( FieldEntry fieldEntry : fieldEntries )
        {
            fieldEntry.getJsonGenerator().generate( fieldEntry, g );
        }
        g.writeEndArray();
    }
}
