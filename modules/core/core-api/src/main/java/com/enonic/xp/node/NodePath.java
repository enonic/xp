package com.enonic.xp.node;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;

import static com.google.common.base.Strings.isNullOrEmpty;

@PublicApi
public final class NodePath
    implements Comparable<NodePath>
{
    public final static NodePath ROOT = new NodePath( "/" );

    private static final char ELEMENT_DIVIDER = '/';

    private final boolean absolute;

    private final boolean trailingDivider;

    private final ImmutableList<Element> elements;

    private final String refString;

    public NodePath( final String path )
    {
        this( new Builder( path ) );
    }

    public NodePath( final NodePath parent, final NodeName element )
    {
        this( new Builder( parent ).addElement( element.toString() ) );
    }

    private NodePath( final Builder builder )
    {
        this.absolute = builder.absolute;
        this.trailingDivider = builder.trailingDivider;
        if ( builder.elements != null )
        {
            this.elements = builder.elements;
        }
        else
        {
            this.elements = builder.elementListBuilder.build();
        }

        this.refString = doToString();
    }

    public boolean isRoot()
    {
        return this.equals( NodePath.ROOT );
    }

    public NodePath asRelative()
    {
        if ( isRelative() )
        {
            return this;
        }
        return create( this ).absolute( false ).build();
    }

    public NodePath asAbsolute()
    {
        if ( isAbsolute() )
        {
            return this;
        }
        final Builder builder = create( this );
        return builder.absolute( true ).build();
    }

    public NodePath getParentPath()
    {
        return new NodePath( new Builder( this ).removeLastElement() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final NodePath parent, final String path )
    {
        return new Builder( parent ).
            elements( path ).
            absolute( true );
    }

    public List<NodePath> getParentPaths()
    {
        List<NodePath> parentPaths = new ArrayList<>();

        final Builder builder = new Builder( this );
        for ( int i = 0; i < elementCount(); i++ )
        {
            builder.removeLastElement();
            parentPaths.add( builder.build() );
        }

        return parentPaths;
    }

    public boolean isEmpty()
    {
        return this.elements.size() == 0;
    }

    public boolean isAbsolute()
    {
        return this.absolute;
    }

    public boolean isRelative()
    {
        return !this.absolute;
    }

    public boolean hasTrailingDivider()
    {
        return this.trailingDivider;
    }

    public NodePath trimTrailingDivider()
    {
        return new Builder( this ).trailingDivider( false ).build();
    }

    public int elementCount()
    {
        return this.elements.size();
    }

    public Iterator<Element> iterator()
    {
        return elements.iterator();
    }

    protected Element getFirstElement()
    {
        return elements.get( 0 );
    }

    protected Element getElement( final int index )
    {
        return elements.get( index );
    }

    public String getElementAsString( final int index )
    {
        return elements.get( index ).toString();
    }

    public Element getLastElement()
    {
        return elements.get( elements.size() - 1 );
    }

    public final String getName()
    {
        return isEmpty() ? null : getLastElement().toString();
    }

    public Iterable<String> resolvePathElementNames()
    {
        return elements.stream().map( Element::toString ).collect( Collectors.toList() );
    }

    public NodePath removeFromBeginning( final NodePath path )
    {
        Preconditions.checkState( this.elementCount() >= path.elementCount(),
                                  "No point in trying to remove [" + path.toString() + "] from [" + this.toString() + "]" );

        if ( path.elementCount() == 0 )
        {
            return this;
        }

        for ( int i = 0; i < path.elementCount(); i++ )
        {
            if ( !path.getElement( i ).equals( this.getElement( i ) ) )
            {
                return this;
            }
        }

        final Builder builder = new Builder().absolute( this.isAbsolute() ).trailingDivider( this.hasTrailingDivider() );
        for ( int i = path.elementCount(); i < this.elementCount(); i++ )
        {
            builder.addElement( this.getElement( i ) );
        }

        return builder.build();
    }

    private String doToString()
    {
        final StringBuilder s = new StringBuilder( 25 * elements.size() );
        if ( absolute )
        {
            s.append( ELEMENT_DIVIDER );
        }

        for ( int i = 0; i < this.elements.size(); i++ )
        {
            final Element element = this.elements.get( i );
            s.append( element.toString() );
            if ( i < this.elements.size() - 1 )
            {
                s.append( ELEMENT_DIVIDER );
            }
        }
        if ( trailingDivider )
        {
            s.append( ELEMENT_DIVIDER );
        }

        return s.toString();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null )
        {
            return false;
        }

        if ( !( o instanceof NodePath ) )
        {
            return false;
        }

        final NodePath path = (NodePath) o;
        return Objects.equals( absolute, path.absolute ) &&
            Objects.equals( trailingDivider, path.trailingDivider ) &&
            Objects.equals( elements, path.elements );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.absolute, this.trailingDivider, this.elements );
    }

    @Override
    public String toString()
    {
        return refString;
    }

    public static Builder create( final NodePath source )
    {
        Preconditions.checkNotNull( source, "source to build copy from not given" );
        return new Builder( source );
    }

    public static Builder create( final String path )
    {
        final Builder builder = new Builder();
        builder.elements( path );
        return builder;
    }

    public final static class Element
    {
        private String name;

        public Element( final String name )
        {
            this.name = name;
        }

        @Override
        public boolean equals( final Object o )
        {
            if ( this == o )
            {
                return true;
            }
            if ( !( o instanceof Element ) )
            {
                return false;
            }

            final Element element = (Element) o;

            return Objects.equals( this.name.toLowerCase(), element.name.toLowerCase() );
        }

        @Override
        public int hashCode()
        {
            return Objects.hash( this.name.toLowerCase() );
        }

        @Override
        public String toString()
        {
            return name;
        }
    }

    @Override
    public int compareTo( final NodePath o )
    {
        if ( o.equals( this ) )
        {
            return 0;
        }

        return this.refString.compareTo( o.refString );
    }

    public final static class Builder
    {
        private boolean absolute = true;

        private boolean trailingDivider = false;

        private ImmutableList<Element> elements = null;

        private ImmutableList.Builder<Element> elementListBuilder = new ImmutableList.Builder<>();

        public Builder()
        {
        }

        public Builder( final NodePath source )
        {
            Preconditions.checkNotNull( source, "source to build copy from not given" );
            this.absolute = source.absolute;
            this.trailingDivider = source.trailingDivider;
            this.elements = source.elements;
        }

        public Builder( final String path )
        {
            this.elements( path );
        }

        private Element newElement( String value )
        {
            return new Element( value );
        }

        public Builder trailingDivider( final boolean value )
        {
            this.trailingDivider = value;
            return this;
        }

        public Builder absolute( final boolean value )
        {
            this.absolute = value;
            return this;
        }

        public Builder elements( final String elements )
        {
            if ( elements.length() == 0 )
            {
                absolute( false );
                trailingDivider( false );
            }
            else
            {
                final boolean absolute = elements.charAt( 0 ) == ELEMENT_DIVIDER;
                final boolean hasTrailingDivider =
                    !( elements.length() == 1 && absolute ) && elements.charAt( elements.length() - 1 ) == ELEMENT_DIVIDER;

                absolute( absolute );
                trailingDivider( hasTrailingDivider );
                for ( final String pathElement : Splitter.on( ELEMENT_DIVIDER ).omitEmptyStrings().trimResults().split( elements ) )
                {
                    addElement( pathElement );
                }
            }
            return this;
        }

        public Builder addElement( final String value )
        {
            if ( isNullOrEmpty( value ) )
            {
                return this;
            }

            if ( this.elements != null )
            {
                final ImmutableList.Builder<Element> newList = new ImmutableList.Builder<>();
                newList.addAll( this.elements );
                newList.add( newElement( value ) );
                this.elementListBuilder = newList;
                this.elements = null;
            }
            else
            {
                this.elementListBuilder.add( newElement( value ) );
            }
            return this;
        }

        public Builder removeLastElement()
        {
            if ( this.elements != null )
            {
                final ImmutableList.Builder<Element> newList = new ImmutableList.Builder<>();
                for ( int i = 0; i < this.elements.size() - 1; i++ )
                {
                    newList.add( this.elements.get( i ) );
                }
                this.elementListBuilder = newList;
                this.elements = null;
            }
            else if ( this.elementListBuilder != null )
            {
                final ImmutableList<Element> list = this.elementListBuilder.build();
                final ImmutableList.Builder<Element> newList = new ImmutableList.Builder<>();
                for ( int i = 0; i < list.size() - 1; i++ )
                {
                    newList.add( list.get( i ) );
                }
                this.elementListBuilder = newList;
                this.elements = null;
            }
            return this;
        }

        public Builder removeFirstElement()
        {
            if ( this.elements != null )
            {
                final ImmutableList.Builder<Element> newList = new ImmutableList.Builder<>();
                for ( int i = 1; i < this.elements.size(); i++ )
                {
                    newList.add( this.elements.get( i ) );
                }
                this.elementListBuilder = newList;
                this.elements = null;
            }
            else if ( this.elementListBuilder != null )
            {
                final ImmutableList<Element> list = this.elementListBuilder.build();
                final ImmutableList.Builder<Element> newList = new ImmutableList.Builder<>();
                for ( int i = 1; i < list.size(); i++ )
                {
                    newList.add( list.get( i ) );
                }
                this.elementListBuilder = newList;
                this.elements = null;
            }
            return this;
        }

        public Builder addElement( final Element value )
        {
            if ( this.elements != null )
            {
                final ImmutableList.Builder<Element> newList = new ImmutableList.Builder<>();
                this.elements.forEach( newList::add );
                newList.add( value );
                this.elementListBuilder = newList;
                this.elements = null;
            }
            else
            {
                this.elementListBuilder.add( value );
            }

            return this;
        }

        public NodePath build()
        {
            return new NodePath( this );
        }
    }
}
