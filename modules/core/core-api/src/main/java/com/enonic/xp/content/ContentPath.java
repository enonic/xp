package com.enonic.xp.content;


import java.util.LinkedList;
import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@Beta
public final class ContentPath
{
    public static final ContentPath ROOT = create().absolute( true ).build();

    private static final String ELEMENT_DIVIDER = "/";

    private final boolean absolute;

    private final ImmutableList<String> elements;

    private final String refString;

    private ContentPath( final Builder builder )
    {
        Preconditions.checkNotNull( builder.elements );
        this.absolute = builder.absolute;

        if ( builder.elements.isEmpty() )
        {
            refString = this.absolute ? ELEMENT_DIVIDER : "";
            this.elements = ImmutableList.of();
        }
        else
        {
            final ImmutableList.Builder<String> elementsBuilder = ImmutableList.builder();
            elementsBuilder.addAll( builder.elements );
            this.elements = elementsBuilder.build();
            this.refString = ( this.absolute ? ELEMENT_DIVIDER : "" ) + Joiner.on( ELEMENT_DIVIDER ).join( elements );
        }

    }

    public String getElement( final int index )
    {
        return this.elements.get( index );
    }

    public boolean isRoot()
    {
        return this.elements.isEmpty();
    }

    public int elementCount()
    {
        return this.elements.size();
    }

    public ContentPath getParentPath()
    {
        return getAncestorPath(1);
    }

    public ContentPath getAncestorPath( final Integer deep )
    {
        if ( this.elements.size() < 1 )
        {
            return null;
        }

        final LinkedList<String> parentElements = newListOfParentElements( deep );

        return parentElements != null ? create().absolute( absolute ).elements( parentElements ).build() : null;
    }

    public boolean isAbsolute()
    {
        return absolute;
    }

    public boolean isRelative()
    {
        return !absolute;
    }

    public ContentPath asRelative()
    {
        if ( isRelative() )
        {
            return this;
        }

        return new ContentPath.Builder( this ).absolute( false ).build();
    }

    public ContentPath asAbsolute()
    {
        if ( isAbsolute() )
        {
            return this;
        }

        return new ContentPath.Builder( this ).absolute( true ).build();
    }

    public boolean hasName()
    {
        return !elements.isEmpty();
    }

    public final String getName()
    {
        return elements.size() == 0 ? null : elements.get( elements.size() - 1 );
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

    public int compareTo( ContentPath contentPath )
    {
        return refString.compareTo( contentPath.refString );
    }

    @Override
    public String toString()
    {
        return refString;
    }

    private LinkedList<String> newListOfParentElements( final Integer deep )
    {
        final LinkedList<String> newElements = Lists.newLinkedList( this.elements );
        for ( int count = 0; count < deep; count++ )
        {
            if ( !newElements.isEmpty() )
            {
                newElements.removeLast();
            } else {
                return null;
            }
        }
        return newElements;
    }

    public static ContentPath from( final String path )
    {
        final Iterable<String> pathElements = Splitter.on( ELEMENT_DIVIDER ).omitEmptyStrings().split( path );
        boolean absolute = path.startsWith( ELEMENT_DIVIDER );
        return create().elements( pathElements ).absolute( absolute ).build();
    }

    public static ContentPath from( final ContentPath parent, final String name )
    {
        return create().elements( parent.elements ).absolute( parent.isAbsolute() ).addElement( name ).build();
    }

    public static ContentPath from( final ContentPath parent, final ContentPath relative )
    {
        final Builder builder = create().elements( parent.elements );
        builder.addElements( relative.elements );
        builder.absolute( parent.isAbsolute() );
        return builder.build();
    }


    public static Builder create()
    {
        return new Builder();
    }

    public final static class Builder
    {
        private LinkedList<String> elements;

        private boolean absolute = true;

        private Builder()
        {
            this.elements = Lists.newLinkedList();
        }

        private Builder( ContentPath source )
        {
            this.elements = Lists.newLinkedList( source.elements );
            this.absolute = source.absolute;
        }

        public Builder absolute( final boolean value )
        {
            this.absolute = value;
            return this;
        }

        public Builder elements( final String... pathElements )
        {
            this.elements.clear();
            for ( String pathElement : pathElements )
            {
                validatePathElement( pathElement );
                this.elements.add( pathElement );
            }
            return this;
        }

        public Builder elements( final Iterable<String> pathElements )
        {
            this.elements.clear();
            for ( String pathElement : pathElements )
            {
                validatePathElement( pathElement );
                this.elements.add( pathElement );
            }
            return this;
        }

        public Builder addElement( final String pathElement )
        {
            validatePathElement( pathElement );
            this.elements.add( pathElement );
            return this;
        }

        public void addElements( final List<String> elements )
        {
            elements.forEach( this::addElement );
        }

        private void validatePathElement( final String pathElement )
        {
            Preconditions.checkNotNull( pathElement, "A path element cannot be null" );
            Preconditions.checkArgument( !pathElement.isEmpty(), "A path element cannot be empty" );
            Preconditions.checkArgument( !pathElement.contains( ELEMENT_DIVIDER ),
                                         "A path element cannot contain an element divider '%s': [%s]", ELEMENT_DIVIDER, pathElement );
        }

        public ContentPath build()
        {
            return new ContentPath( this );
        }
    }
}
