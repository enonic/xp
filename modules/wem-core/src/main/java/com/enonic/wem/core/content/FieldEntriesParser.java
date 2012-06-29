package com.enonic.wem.core.content;


import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import com.google.common.base.Preconditions;

import com.enonic.wem.core.content.config.field.ConfigItems;

public class FieldEntriesParser
{
    public static FieldEntries parse( JsonParser jp, ConfigItems configItems )
        throws IOException
    {
        Preconditions.checkArgument( "fieldEntries".equals( jp.getCurrentName() ) );
        Preconditions.checkArgument( jp.getCurrentToken() == JsonToken.START_ARRAY );

        FieldEntries fieldEntries = new FieldEntries( configItems );
        JsonToken nextToken = jp.nextToken();
        if ( nextToken == JsonToken.START_OBJECT )
        {
            fieldEntries.add( FieldEntryParser.parse( jp, configItems ) );
        }

        return fieldEntries;
    }
}
