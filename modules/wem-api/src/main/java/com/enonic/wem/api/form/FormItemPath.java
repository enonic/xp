package com.enonic.wem.api.form;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@Immutable
public class FormItemPath
    implements Iterable<String>
{
    public static final FormItemPath ROOT = new FormItemPath();

    private final static String ELEMENT_DIVIDER = ".";

    private final FormItemPath parentPath;

    private final ImmutableList<String> elements;

    private final String refString;

    public static FormItemPath from( final FormItemPath parentPath, final String name )
    {
        Preconditions.checkNotNull( parentPath, "parentPath cannot be null" );
        Preconditions.checkNotNull( parentPath, "name cannot be null" );

        return new FormItemPath( parentPath, name );
    }

    public static FormItemPath from( final Iterable<String> pathElements )
    {
        Preconditions.checkNotNull( pathElements, "pathElements cannot be null" );

        return new FormItemPath( ImmutableList.copyOf( pathElements ) );
    }

    public static FormItemPath from( final String path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );

        final List<String> pathElements = splitPathIntoElements( path );
        return new FormItemPath( ImmutableList.copyOf( pathElements ) );
    }

    public FormItemPath( final FormItemPath parentPath, final String name )
    {
        Preconditions.checkNotNull( parentPath, "parentPath cannot be null" );
        Preconditions.checkNotNull( parentPath, "name cannot be null" );

        this.parentPath = parentPath;
        final ImmutableList.Builder<String> elementBuilder = ImmutableList.builder();
        elementBuilder.addAll( parentPath.elements ).add( name );
        this.elements = elementBuilder.build();
        this.refString = toString( this.elements );
    }

    public FormItemPath( final ImmutableList<String> elementNames )
    {
        Preconditions.checkNotNull( elementNames, "elementNames cannot be null" );

        this.elements = elementNames;

        final List<String> parentPathElements = Lists.newArrayList();
        for ( int i = 0; i < this.elements.size(); i++ )
        {
            if ( i < this.elements.size() - 1 )
            {
                parentPathElements.add( this.elements.get( i ) );
            }
        }
        this.parentPath = parentPathElements.size() > 0 ? FormItemPath.from( parentPathElements ) : null;
        this.refString = toString( this.elements );
    }

    private FormItemPath()
    {
        elements = ImmutableList.of();
        parentPath = null;
        refString = "";
    }

    public FormItemPath getParent()
    {
        return parentPath;
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

    public FormItemPath asNewWithoutFirstPathElement()
    {
        final List<String> pathElements = Lists.newArrayList();
        for ( int i = 0; i < elements.size(); i++ )
        {
            if ( i > 0 )
            {
                pathElements.add( elements.get( i ) );
            }
        }
        return FormItemPath.from( pathElements );
    }

    @Override
    public Iterator<String> iterator()
    {
        return elements.iterator();
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

        final FormItemPath formItemPath = (FormItemPath) o;

        return elements.equals( formItemPath.elements );
    }

    @Override
    public int hashCode()
    {
        return elements.hashCode();
    }

    @Override
    public String toString()
    {
        return refString;
    }

    private String toString( final ImmutableList<String> elements )
    {
        final StringBuilder s = new StringBuilder();
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

    private static ImmutableList<String> splitPathIntoElements( final String path )
    {
        final ImmutableList.Builder<String> elements = ImmutableList.builder();
        final StringTokenizer st = new StringTokenizer( path, ELEMENT_DIVIDER );
        while ( st.hasMoreTokens() )
        {
            elements.add( st.nextToken() );
        }
        return elements.build();
    }

    public static boolean hasNotPathElementDivider( final String s )
    {
        return !s.contains( ELEMENT_DIVIDER );
    }
}
