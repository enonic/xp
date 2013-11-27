package com.enonic.wem.api.content;


import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.wem.api.space.SpaceName;

import static org.apache.commons.lang.StringUtils.substringAfter;
import static org.apache.commons.lang.StringUtils.substringBefore;

public final class ContentPath
    implements ContentSelector
{
    public static final ContentPath ROOT = newPath().build();

    private static final String ELEMENT_DIVIDER = "/";

    private static final String SPACE_PREFIX_DIVIDER = ":";

    private final ImmutableList<String> elements;

    private final String refString;

    private final SpaceName spaceName;

    private static final String EMBEDDED = "__embedded";

    private final boolean pathToEmbeddedContent;

    private ContentPath( final Builder builder )
    {
        Preconditions.checkNotNull( builder.elements );
        this.spaceName = builder.spaceName;

        final String spacePrefix = spaceName == null ? "" : spaceName.name() + SPACE_PREFIX_DIVIDER;
        if ( builder.elements.isEmpty() )
        {
            refString = spacePrefix + ELEMENT_DIVIDER;
            this.elements = ImmutableList.of();
        }
        else
        {
            final ImmutableList.Builder<String> elementsBuilder = ImmutableList.builder();
            elementsBuilder.addAll( builder.elements );
            this.elements = elementsBuilder.build();
            this.refString = spacePrefix + ELEMENT_DIVIDER + Joiner.on( ELEMENT_DIVIDER ).join( elements );
        }

        pathToEmbeddedContent = resolveIsPathToEmbeddedContent();
    }

    private boolean resolveIsPathToEmbeddedContent()
    {
        for ( int i = 0; i < elements.size(); i++ )
        {
            final String pathElement = elements.get( i );
            if ( EMBEDDED.equals( pathElement ) )
            {
                final boolean lastElement = i == elements.size() - 1;
                if ( lastElement )
                {
                    throw new IllegalArgumentException( "Missing name of embedded Content: " + refString );
                }
                final boolean firstElement = i == 0;
                if ( firstElement )
                {
                    throw new IllegalArgumentException( "Expected a path to a Content before the embedded marker: " + refString );
                }
                final boolean notSecondToLastElement = i != elements.size() - 2;
                if ( notSecondToLastElement )
                {
                    throw new IllegalArgumentException( "Expected only one element after the embedded marker: " + refString );
                }
                return true;
            }
        }
        return false;
    }

    public String getElement( final int index )
    {
        return this.elements.get( index );
    }

    public boolean isRoot()
    {
        return this.elements.isEmpty();
    }

    public boolean isAbsolute()
    {
        return this.spaceName != null;
    }

    public boolean isRelative()
    {
        return this.spaceName == null;
    }

    public boolean isPathToEmbeddedContent()
    {
        return pathToEmbeddedContent;
    }

    public SpaceName getSpace()
    {
        return spaceName;
    }

    public int elementCount()
    {
        return this.elements.size();
    }

    public ContentPath getParentPath()
    {
        if ( this.elements.size() < 1 )
        {
            return null;
        }

        final LinkedList<String> parentElements = newListOfParentElements();
        return newPath().spaceName( this.spaceName ).elements( parentElements ).build();
    }

    public ContentPath withName( final String name )
    {
        Preconditions.checkNotNull( name, "name not given" );
        final LinkedList<String> newElements = newListOfParentElements();
        return newPath().spaceName( this.spaceName ).elements( newElements ).addElement( name ).build();
    }

    public String getRelativePath()
    {
        return Joiner.on( ELEMENT_DIVIDER ).join( elements );
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
        if ( !Objects.equal( this.spaceName, possibleParentPath.spaceName ) )
        {
            return false;
        }
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

    @Override
    public String toString()
    {
        return refString;
    }

    private LinkedList<String> newListOfParentElements()
    {
        if ( isPathToEmbeddedContent() )
        {
            final LinkedList<String> newElements = Lists.newLinkedList( this.elements );
            newElements.removeLast();
            newElements.removeLast();
            return newElements;
        }
        else
        {
            final LinkedList<String> newElements = Lists.newLinkedList( this.elements );
            if ( !newElements.isEmpty() )
            {
                newElements.removeLast();
            }
            return newElements;
        }
    }

    public static ContentPath from( final String path )
    {
        final boolean isAbsolute = path.contains( SPACE_PREFIX_DIVIDER );
        final String relativePath;
        final String spaceName;
        if ( isAbsolute )
        {
            relativePath = substringAfter( path, SPACE_PREFIX_DIVIDER );
            spaceName = substringBefore( path, SPACE_PREFIX_DIVIDER );
        }
        else
        {
            relativePath = path;
            spaceName = null;
        }
        final Iterable<String> pathElements = Splitter.on( ELEMENT_DIVIDER ).omitEmptyStrings().split( relativePath );
        return newPath().elements( pathElements ).spaceName( spaceName ).build();
    }

    public static ContentPath from( final ContentPath parent, final String name )
    {
        return newPath().spaceName( parent.spaceName ).elements( parent.elements ).addElement( name ).build();
    }

    public static ContentPath from( final ContentPath parent, final ContentPath relative )
    {
        final Builder builder = newPath().spaceName( parent.spaceName ).elements( parent.elements );
        builder.addElements( relative.elements );
        return builder.build();
    }

    public static ContentPath createPathToEmbeddedContent( final ContentPath parent, final String name )
    {
        return newPath().spaceName( parent.spaceName ).elements( parent.elements ).addElement( EMBEDDED ).addElement( name ).build();
    }

    public static ContentPath rootOf( final SpaceName spaceName )
    {
        return newPath().spaceName( spaceName ).build();
    }

    public static Builder newPath()
    {
        return new Builder();
    }

    public final static class Builder
    {
        private LinkedList<String> elements;

        private SpaceName spaceName;

        private Builder()
        {
            this.elements = Lists.newLinkedList();
            this.spaceName = null;
        }

        public Builder spaceName( final SpaceName spaceName )
        {
            this.spaceName = spaceName;
            return this;
        }

        public Builder spaceName( final String spaceName )
        {
            return spaceName( spaceName == null ? null : SpaceName.from( spaceName ) );
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
            for( String element : elements )
            {
                addElement( element );
            }
        }

        private void validatePathElement( final String pathElement )
        {
            Preconditions.checkNotNull( pathElement, "A path element cannot be null" );
            Preconditions.checkArgument( !pathElement.isEmpty(), "A path element cannot be empty" );
            Preconditions.checkArgument( !pathElement.contains( ELEMENT_DIVIDER ),
                                         "A path element cannot contain an element divider '%s': [%s]", ELEMENT_DIVIDER, pathElement );
            Preconditions.checkArgument( !pathElement.contains( SPACE_PREFIX_DIVIDER ),
                                         "A path element cannot contain an element divider '%s': [%s]", SPACE_PREFIX_DIVIDER, pathElement );
        }

        public ContentPath build()
        {
            return new ContentPath( this );
        }
    }
}
