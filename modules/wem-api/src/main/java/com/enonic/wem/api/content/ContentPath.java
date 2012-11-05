package com.enonic.wem.api.content;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public final class ContentPath
{
    private static final String ELEMENT_SEPARATOR = "/";

    private final LinkedList<String> elements = new LinkedList<String>();

    private final String refString;

    public ContentPath( final String... elements )
    {
        Collections.addAll( this.elements, elements );
        this.refString = Joiner.on( ELEMENT_SEPARATOR ).join( elements );
    }

    private ContentPath( final List<String> elements )
    {
        this.elements.addAll( elements );
        this.refString = Joiner.on( ELEMENT_SEPARATOR ).join( elements );
    }

    private ContentPath( final List<String> parentElements, final String lastElement )
    {
        this.elements.addAll( parentElements );
        this.elements.add( lastElement );
        this.refString = Joiner.on( ELEMENT_SEPARATOR ).join( elements );
    }

    public String getElement( final int index )
    {
        return this.elements.get( index );
    }

    public int length()
    {
        return this.elements.size();
    }

    public ContentPath getParentPath()
    {
        if ( this.elements.size() < 2 )
        {
            return null;
        }

        final LinkedList<String> parentElements = newListOfParentElements();
        return new ContentPath( parentElements );
    }

    public ContentPath withName( final String name )
    {
        Preconditions.checkNotNull( name, "name not given" );
        final LinkedList<String> newElements = newListOfParentElements();
        newElements.add( name );
        return new ContentPath( newElements );
    }

    public boolean hasName()
    {
        return !elements.isEmpty();
    }

    public String getName()
    {
        return elements.getLast();
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

        final ContentPath that = (ContentPath) o;

        return refString.equals( that.refString );
    }

    @Override
    public int hashCode()
    {
        return refString.hashCode();
    }

    @Override
    public String toString()
    {
        return refString;
    }

    private LinkedList<String> newListOfParentElements()
    {
        final LinkedList<String> newElements = Lists.newLinkedList( this.elements );
        if ( !newElements.isEmpty() )
        {
            newElements.removeLast();
        }
        return newElements;
    }

    public static ContentPath from( final String path )
    {
        final LinkedList<String> elements = new LinkedList<String>();
        for ( String pathElement : Splitter.on( ELEMENT_SEPARATOR ).omitEmptyStrings().split( path ) )
        {
            elements.add( pathElement );
        }
        return new ContentPath( elements );
    }

    public static ContentPath from( final ContentPath parent, final String name )
    {
        return new ContentPath( parent.elements, name );
    }
}
