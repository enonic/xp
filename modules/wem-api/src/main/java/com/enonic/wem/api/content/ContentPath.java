package com.enonic.wem.api.content;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class ContentPath
{
    private static final String ELEMENT_SEPARATOR = "/";

    private LinkedList<String> elements = new LinkedList<String>();

    private String refString;

    public ContentPath( String... elements )
    {
        Collections.addAll( this.elements, elements );
        this.refString = Joiner.on( ELEMENT_SEPARATOR ).join( elements );
    }

    private ContentPath( List<String> elements )
    {
        this.elements.addAll( elements );
        this.refString = Joiner.on( ELEMENT_SEPARATOR ).join( elements );
    }

    private ContentPath( List<String> parentElements, String lastElement )
    {
        this.elements.addAll( parentElements );
        this.elements.add( lastElement );
        this.refString = Joiner.on( ELEMENT_SEPARATOR ).join( elements );
    }

    public String getElement( int index )
    {
        return this.elements.get( index );
    }

    public int numberOfElements()
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
        return elements.size() > 0;
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
        if ( newElements.size() > 0 )
        {
            newElements.removeLast();
        }
        return newElements;
    }

    public static ContentPath from( final String path )
    {
        LinkedList<String> elements = new LinkedList<String>();

        Scanner scanner = new Scanner( path );
        scanner.useDelimiter( "\\" + ELEMENT_SEPARATOR );
        while ( scanner.hasNext() )
        {
            elements.add( scanner.next() );
        }

        return new ContentPath( elements );
    }

    public static ContentPath from( final ContentPath parent, final String name )
    {
        return new ContentPath( parent.elements, name );
    }
}
