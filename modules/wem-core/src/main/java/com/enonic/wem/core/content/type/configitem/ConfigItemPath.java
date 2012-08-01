package com.enonic.wem.core.content.type.configitem;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.google.common.base.Preconditions;

public class ConfigItemPath
{
    private final static String ELEMENT_DIVIDER = ".";

    private List<String> elements;

    public ConfigItemPath()
    {
        elements = new ArrayList<String>();
    }

    public ConfigItemPath( List<String> pathElements )
    {
        Preconditions.checkNotNull( pathElements );
        elements = pathElements;
    }

    public ConfigItemPath( ConfigItemPath configItemPath, String name )
    {
        elements = new ArrayList<String>();
        elements.addAll( configItemPath.elements );
        elements.add( name );
    }

    public ConfigItemPath( String path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );

        elements = splitPathIntoElements( path );
    }

    public String getFirstElement()
    {
        return elements.get( 0 );
    }

    public String getLastElement()
    {
        return elements.get( elements.size() - 1 );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final ConfigItemPath configItemPath = (ConfigItemPath) o;

        return elements.equals( configItemPath.elements );
    }

    @Override
    public int hashCode()
    {
        return elements.hashCode();
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        for ( int i = 0, size = elements.size(); i < size; i++ )
        {
            s.append( elements.get( i ) );

            if ( i < elements.size() - 1 )
            {
                s.append( ELEMENT_DIVIDER );
            }
        }
        return s.toString();
    }

    private static List<String> splitPathIntoElements( String path )
    {
        List<String> elements = new ArrayList<String>();

        StringTokenizer st = new StringTokenizer( path, ELEMENT_DIVIDER );
        while ( st.hasMoreTokens() )
        {
            elements.add( st.nextToken() );
        }
        return elements;
    }
}
