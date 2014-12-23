package com.enonic.wem.export.util;

public class PathUtils
{
    private static final boolean IS_WINDOWS = System.getProperty( "os.name" ).contains( "indow" );

    public static String removeLeadingWindowsSlash( final String value )
    {
        if ( !IS_WINDOWS )
        {
            return value;
        }

        return value.replaceFirst( "^/+([A-Z]:)", "$1" );
    }

}
