package com.enonic.wem.api;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

public abstract class BasePath2<PATH extends BasePath2, ELEMENT extends BasePath2.Element, BUILDER extends BasePath2.Builder>
{
    private static final char DEFAULT_ELEMENT_DIVIDER = '/';

    private final char elementDivider;

    private final boolean absolute;

    private final boolean trailingDivider;

    private final ImmutableList<ELEMENT> elements;

    private final String refString;

    public BasePath2( final Builder builder )
    {
        this.elementDivider = builder.elementDivider != null ? builder.elementDivider : DEFAULT_ELEMENT_DIVIDER;
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

    public boolean isEmpty()
    {
        return this.elements.size() == 0;
    }

    public boolean isRoot()
    {
        return isAbsolute() && isEmpty();
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

    public BasePath2<PATH,ELEMENT,BUILDER> trimTrailingDivider()
    {
        return newBuilder( (PATH)this ).trailingDivider( false ).build();
    }

    public int elementCount()
    {
        return this.elements.size();
    }

    public Iterator<ELEMENT> iterator()
    {
        return elements.iterator();
    }

    protected ELEMENT getFirstElement()
    {
        return elements.get( 0 );
    }

    protected ELEMENT getElement( final int index )
    {
        return elements.get( index );
    }

    public String getElementAsString( final int index )
    {
        return elements.get( index ).toString();
    }

    protected ELEMENT getLastElement()
    {
        return elements.get( elements.size() - 1 );
    }

    public PATH getParentPath()
    {
        final Builder builder = newBuilder( (PATH) this );
        builder.removeLastElement();
        return (PATH) builder.build();
    }

    public Iterable<String> resolvePathElementNames()
    {
        final List<String> pathElements = new ArrayList<>();
        for ( Element element : elements )
        {
            pathElements.add( element.toString() );
        }
        return pathElements;
    }

    public <T extends BasePath2> T removeFromBeginning( final T path )
    {
        if ( path.elementCount() == 0 )
        {
            //noinspection unchecked
            return (T) this;
        }

        for ( int i = 0; i < path.elementCount(); i++ )
        {
            if ( !path.getElement( i ).equals( this.getElement( i ) ) )
            {
                //noinspection unchecked
                return (T) this;
            }
        }

        final Builder builder = this.newBuilder().absolute( this.isAbsolute() ).trailingDivider( this.hasTrailingDivider() );
        for ( int i = path.elementCount(); i < this.elementCount(); i++ )
        {
            builder.addElement( this.getElement( i ) );
        }

        //noinspection unchecked
        return (T) builder.build();
    }

    private String doToString()
    {
        final StringBuilder s = new StringBuilder( 25 * elements.size() );
        if ( absolute )
        {
            s.append( elementDivider );
        }

        for ( int i = 0; i < this.elements.size(); i++ )
        {
            final Element element = this.elements.get( i );
            s.append( element.toString() );
            if ( i < this.elements.size() - 1 )
            {
                s.append( this.elementDivider );
            }
        }
        if ( trailingDivider )
        {
            s.append( elementDivider );
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
        if ( !( o instanceof BasePath2 ) )
        {
            return false;
        }

        final BasePath2 path = (BasePath2) o;
        return Objects.equals( absolute, path.absolute ) &&
            Objects.equals( elementDivider, path.elementDivider ) &&
            Objects.equals( trailingDivider, path.trailingDivider ) &&
            Objects.equals( elements, path.elements );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.elementDivider, this.absolute, this.trailingDivider, this.elements );
    }

    public String toString()
    {
        return refString;
    }

    public static class Element
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

            return Objects.equals( this.name, element.name );
        }

        @Override
        public int hashCode()
        {
            return Objects.hash( this.name );
        }

        public String toString()
        {
            return name;
        }
    }

    protected abstract Builder<BUILDER,PATH> newBuilder();

    protected abstract Builder<BUILDER,PATH> newBuilder( PATH source );

    /*protected static Builder newPath( final String path, final char elementDivider )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        if ( path.length() == 0 )
        {
            return newBuilder().absolute( false ).trailingDivider( false );
        }

        final boolean absolute = path.charAt( 0 ) == elementDivider;
        final boolean hasTrailingDivider = !( path.length() == 1 && absolute ) && path.charAt( path.length() - 1 ) == elementDivider;

        final Builder builder = new Builder();
        builder.absolute( absolute );
        builder.trailingDivider( hasTrailingDivider );
        builder.elementDivider( elementDivider );
        for ( final String pathElement : Splitter.on( elementDivider ).omitEmptyStrings().trimResults().split( path ) )
        {
            builder.addElement( pathElement );
        }
        return builder;
    }*/

    public static abstract class Builder<B extends Builder, P extends BasePath2>
    {
        private Character elementDivider;

        private boolean absolute = true;

        private boolean trailingDivider = false;

        private ImmutableList<Element> elements = null;

        private ImmutableList.Builder<Element> elementListBuilder = new ImmutableList.Builder<>();

        public Builder( final BasePath2 source )
        {
            Preconditions.checkNotNull( source, "source to build copy from not given" );
            this.elementDivider = source.elementDivider;
            this.absolute = source.absolute;
            this.trailingDivider = source.trailingDivider;
            this.elements = source.elements;
        }

        public Builder( final String elements, final char elementDivider )
        {
            this.elementDivider = elementDivider;
            this.elements( elements );
        }

        public Builder()
        {

        }

        @SuppressWarnings("unchecked")
        private B getThis()
        {
            return (B) this;
        }

        public B elementDivider( final char elementDivider )
        {
            this.elementDivider = elementDivider;
            return getThis();
        }

        public B trailingDivider( final boolean value )
        {
            this.trailingDivider = value;
            return getThis();
        }

        public B absolute( final boolean value )
        {
            this.absolute = value;
            return getThis();
        }

        public B elements( final String elements )
        {
            if ( elements.length() == 0 )
            {
                absolute( false );
                trailingDivider( false );
            }
            else
            {
                final boolean absolute = elements.charAt( 0 ) == elementDivider;
                final boolean hasTrailingDivider =
                    !( elements.length() == 1 && absolute ) && elements.charAt( elements.length() - 1 ) == elementDivider;

                absolute( absolute );
                trailingDivider( hasTrailingDivider );
                for ( final String pathElement : Splitter.on( elementDivider ).omitEmptyStrings().trimResults().split( elements ) )
                {
                    addElement( pathElement );
                }
            }
            return getThis();
        }

        public B addElement( final String value )
        {
            if ( this.elements != null )
            {
                final ImmutableList.Builder<Element> newList = new ImmutableList.Builder<>();
                for ( final Element element : this.elements )
                {
                    newList.add( element );
                }
                newList.add( newElement( value ) );
                this.elementListBuilder = newList;
                this.elements = null;
            }
            else
            {
                this.elementListBuilder.add( newElement( value ) );
            }
            return getThis();
        }

        protected Element newElement( final String value )
        {
            return new Element( value );
        }

        public B addElement( final Element value )
        {
            if ( this.elements != null )
            {
                final ImmutableList.Builder<Element> newList = new ImmutableList.Builder<>();
                for ( final Element element : this.elements )
                {
                    newList.add( element );
                }
                newList.add( value );
                this.elementListBuilder = newList;
                this.elements = null;
            }
            else
            {
                this.elementListBuilder.add( value );
            }

            return getThis();
        }

        public B addElements( final Iterator<Element> elements )
        {
            this.elementListBuilder.addAll( elements );
            return getThis();
        }

        public B removeLastElement()
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
            return getThis();
        }

        public B removeFirstElement()
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
            return getThis();
        }

        public abstract P build();
    }
}
