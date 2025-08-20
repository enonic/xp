package com.enonic.xp.content;

import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ContentPath
{
    public static final ContentPath ROOT = create().absolute( true ).build();

    private static final String ELEMENT_DIVIDER = "/";

    private final boolean absolute;

    private final ImmutableList<ContentName> elements;

    private ContentPath( final boolean absolute, final ImmutableList<ContentName> elements )
    {
        this.absolute = absolute;
        this.elements = elements;
    }

    public ContentName getElement( final int index )
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
        return getAncestorPath( 1 );
    }

    public ContentPath getAncestorPath( final int deep )
    {
        final int size = this.elements.size();

        if ( size < deep )
        {
            return null;
        }

        return new ContentPath( this.absolute, this.elements.subList( 0, size - deep ) );
    }

    public boolean isAbsolute()
    {
        return absolute;
    }

    public ContentPath asAbsolute()
    {
        if ( absolute )
        {
            return this;
        }

        return new ContentPath.Builder( this ).absolute( true ).build();
    }

    public ContentName getName()
    {
        return elements.isEmpty() ? null : elements.get( elements.size() - 1 );
    }

    public boolean isChildOf( final ContentPath possibleParentPath )
    {
        if ( elementCount() <= possibleParentPath.elementCount() )
        {
            return false;
        }

        for ( int i = 0; i < possibleParentPath.elementCount(); i++ )
        {
            if ( !elements.get( i ).toString().equalsIgnoreCase( possibleParentPath.getElement( i ).toString() ) )
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
        if ( !( o instanceof ContentPath ) )
        {
            return false;
        }
        final ContentPath that = (ContentPath) o;
        return absolute == that.absolute && elements.equals( that.elements );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( absolute, elements );
    }

    @Override
    public String toString()
    {
        return ( this.absolute ? ELEMENT_DIVIDER : "" ) +
            elements.stream().map( ContentName::toString ).collect( Collectors.joining( ELEMENT_DIVIDER ) );
    }

    public static ContentPath from( final String path )
    {
        final Builder builder = create().absolute( path.startsWith( ELEMENT_DIVIDER ) );
        Splitter.on( ELEMENT_DIVIDER ).omitEmptyStrings().splitToStream( path ).map( ContentName::from ).forEach( builder::addElement );
        return builder.build();
    }

    public static ContentPath from( final ContentPath parent, final ContentName name )
    {
        return new Builder( parent ).addElement( name ).build();
    }

    public static ContentPath from( final ContentPath parent, final String name )
    {
        return from( parent, ContentName.from( name ) );
    }

    public static ContentPath from( final ContentPath parent, final ContentPath relative )
    {
        final Builder builder = new Builder( parent );
        for ( ContentName element : relative.elements )
        {
            builder.addElement( element );
        }
        return builder.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ImmutableList.Builder<ContentName> elements;

        private boolean absolute = true;

        private Builder()
        {
            this.elements = ImmutableList.builder();
        }

        private Builder( ContentPath source )
        {
            this.elements = ImmutableList.<ContentName>builder().addAll( source.elements );
            this.absolute = source.absolute;
        }

        public Builder absolute( final boolean value )
        {
            this.absolute = value;
            return this;
        }

        public Builder elements( final String... pathElements )
        {
            this.elements = ImmutableList.builder();
            for ( String pathElement : pathElements )
            {
                this.elements.add( ContentName.from( pathElement ) );
            }
            return this;
        }

        public Builder elements( final Iterable<ContentName> pathElements )
        {
            this.elements = ImmutableList.builder();
            for ( ContentName pathElement : pathElements )
            {
                this.elements.add( pathElement );
            }
            return this;
        }

        public Builder addElement( final ContentName pathElement )
        {
            this.elements.add( pathElement );
            return this;
        }

        public ContentPath build()
        {
            return new ContentPath( absolute, elements.build() );
        }
    }
}
