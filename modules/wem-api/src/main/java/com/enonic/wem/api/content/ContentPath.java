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
    public static final ContentPath ROOT = new ContentPath( "/" );

    private static final String ELEMENT_DIVIDER = "/";

    private final LinkedList<String> elements = new LinkedList<String>();

    private final String refString;

    public ContentPath( final String... elements )
    {
        Preconditions.checkNotNull( elements );
        if ( elements.length == 0 )
        {
            refString = "";
        }
        else if ( elements.length == 1 && elements[0].equals( ELEMENT_DIVIDER ) )
        {
            refString = "/";
        }
        else
        {
            for ( String element : elements )
            {
                Preconditions.checkArgument( !element.contains( ELEMENT_DIVIDER ),
                                             "A path element cannot contain an element divider: " + element );
            }

            Collections.addAll( this.elements, elements );
            this.refString = Joiner.on( ELEMENT_DIVIDER ).join( elements );
        }
    }

    private ContentPath( final List<String> elements )
    {
        this.elements.addAll( elements );
        this.refString = Joiner.on( ELEMENT_DIVIDER ).join( elements );
    }

    private ContentPath( final List<String> parentElements, final String lastElement )
    {
        this.elements.addAll( parentElements );
        this.elements.add( lastElement );
        this.refString = Joiner.on( ELEMENT_DIVIDER ).join( elements );
    }

    public final String getElement( final int index )
    {
        return this.elements.get( index );
    }

    public final boolean isRoot()
    {
        return ROOT.equals( this );
    }

    public final int elementCount()
    {
        return this.elements.size();
    }

    public final ContentPath getParentPath()
    {
        if ( this.elements.size() < 2 )
        {
            return null;
        }

        final LinkedList<String> parentElements = newListOfParentElements();
        return new ContentPath( parentElements );
    }

    public final ContentPath withName( final String name )
    {
        Preconditions.checkNotNull( name, "name not given" );
        final LinkedList<String> newElements = newListOfParentElements();
        newElements.add( name );
        return new ContentPath( newElements );
    }

    public final boolean hasName()
    {
        return !elements.isEmpty();
    }

    public final String getName()
    {
        return elements.getLast();
    }

    public boolean isChildOf( final ContentPath possibleParentPath )
    {
        if ( elementCount() <= possibleParentPath.elementCount() )
        {
            return false;
        }

        for ( int i = 0; i < possibleParentPath.elementCount(); i++ )
        {
            if ( !elements.get( i ).equalsIgnoreCase( possibleParentPath.getElement( i ) ) )
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public final boolean equals( final Object o )
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
    public final int hashCode()
    {
        return refString.hashCode();
    }

    @Override
    public final String toString()
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
        for ( String pathElement : Splitter.on( ELEMENT_DIVIDER ).omitEmptyStrings().split( path ) )
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
