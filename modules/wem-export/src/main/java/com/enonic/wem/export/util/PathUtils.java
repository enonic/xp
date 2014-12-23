package com.enonic.wem.export.util;

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

}
