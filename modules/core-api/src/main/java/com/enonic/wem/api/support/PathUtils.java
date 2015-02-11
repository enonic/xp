package com.enonic.wem.api.support;

import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class PathUtils
{
    public static String removeLeadingWindowsSlash( final String value )
    {
        if ( !System.getProperty( "os.name" ).contains( "indow" ) )
        {
            return value;
        }

        return value.replaceFirst( "^/+([A-Z]:)", "$1" );
    }


    private static String[] doGetPathElements( final String path, final String separator )
    {
        return path.split( Pattern.quote( separator + "" ) );
    }

    public static String getJoinedPaths( final String separator, final String newSeparator, final String... paths )
    {
        final boolean isAbsolute = paths[0].startsWith( separator );

        final List<String> elements = doGetJoinedPathElements( separator, paths );

        return isAbsolute ? newSeparator + Joiner.on( newSeparator ).join( elements ) : Joiner.on( newSeparator ).join( elements );
    }

    private static List<String> doGetJoinedPathElements( final String separator, final String[] paths )
    {
        final List<String> elements = Lists.newLinkedList();

        for ( final String path : paths )
        {
            final String[] pathElements = doGetPathElements( path, separator );

            for ( final String pathElement : pathElements )
            {
                if ( !Strings.isNullOrEmpty( pathElement ) )
                {
                    elements.add( pathElement );
                }
            }
        }

        return elements;
    }

}
