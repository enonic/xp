package com.enonic.wem.export.util;

import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.List;

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

    public static List<String> getPathElements( final String path )
    {
        final List<String> elements = Lists.newLinkedList();

        final String separator = FileSystems.getDefault().getSeparator();

        elements.addAll( Arrays.asList( path.split( separator ) ) );
        return elements;
    }

    public static List<String> joinPaths( final String... paths )
    {
        final List<String> joinedPath = Lists.newLinkedList();

        for ( final String path : paths )
        {
            joinedPath.addAll( PathUtils.getPathElements( path ) );
        }

        return joinedPath;
    }


}
