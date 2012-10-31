package com.enonic.wem.api.content.type.component;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Immutable.
 */
public class ComponentPath
{
    private final static String ELEMENT_DIVIDER = ".";

    private List<String> elements;

    public ComponentPath()
    {
        elements = new ArrayList<String>();
    }

    public ComponentPath( List<String> pathElements )
    {
        Preconditions.checkNotNull( pathElements );
        elements = pathElements;
    }

    public ComponentPath( ComponentPath parentPath, String name )
    {
        elements = new ArrayList<String>();
        elements.addAll( parentPath.elements );
        elements.add( name );
    }

    public ComponentPath( final ComponentPath parentPath, final ComponentPath childPath )
    {
        elements = new ArrayList<String>();
        elements.addAll( parentPath.elements );
        elements.addAll( childPath.elements );
    }

    public ComponentPath( String path )
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

    public int elementCount()
    {
        return elements.size();
    }

    public ComponentPath asNewWithoutFirstPathElement()
    {
        List<String> pathElements = Lists.newArrayList();
        for ( int i = 0; i < elements.size(); i++ )
        {
            if ( i > 0 )
            {
                pathElements.add( elements.get( i ) );
            }
        }
        return new ComponentPath( pathElements );
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

        final ComponentPath componentPath = (ComponentPath) o;

        return elements.equals( componentPath.elements );
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
