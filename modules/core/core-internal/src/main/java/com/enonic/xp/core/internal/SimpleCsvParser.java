package com.enonic.xp.core.internal;

import java.util.ArrayList;
import java.util.List;

public final class SimpleCsvParser
{
    private SimpleCsvParser()
    {
    }

    public static List<String> parseLine( final String line )
    {
        final List<String> fields = new ArrayList<>();
        final StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        boolean fieldStarted = false;
        int trailingSpaces = 0;

        for ( int i = 0; i < line.length(); i++ )
        {
            char c = line.charAt( i );

            if ( inQuotes )
            {
                if ( c == '\"' )
                {
                    if ( i + 1 < line.length() && line.charAt( i + 1 ) == '\"' )
                    {
                        field.append( c );
                        i++;
                    }
                    else
                    {
                        inQuotes = false;
                    }
                }
                else
                {
                    field.append( c );
                }
            }
            else
            {
                if ( c == '\"' )
                {
                    inQuotes = true;
                    fieldStarted = true;
                    trailingSpaces = 0;
                }
                else if ( c == ',' )
                {
                    field.setLength( field.length() - trailingSpaces );
                    fields.add( field.toString() );
                    field.setLength( 0 );
                    fieldStarted = false;
                    trailingSpaces = 0;
                }
                else if ( c == ' ' )
                {
                    if ( fieldStarted )
                    {
                        field.append( c );
                        trailingSpaces++;
                    }
                }
                else
                {
                    field.append( c );
                    fieldStarted = true;
                    trailingSpaces = 0;
                }
            }
        }

        field.setLength( field.length() - trailingSpaces );
        fields.add( field.toString() );

        return fields;
    }
}
