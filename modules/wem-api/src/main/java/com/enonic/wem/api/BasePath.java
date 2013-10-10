package com.enonic.wem.api;


import java.util.Iterator;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public abstract class BasePath<T extends BasePath>
    implements Iterable<String>
{
    private final char separator;

    protected final ImmutableList<String> elements;

    private final boolean isAbsolute;

    protected final String refString;

    protected BasePath( final char separator, final Iterable<String> elements, final boolean isAbsolute )
    {
        this.separator = separator;
        this.elements = ImmutableList.copyOf( elements );
        this.isAbsolute = isAbsolute;
        final String joined = Joiner.on( separator ).join( this.elements );
        this.refString = isAbsolute ? separator + joined : joined;
    }

    protected abstract T newPath( final Iterable<String> elements, final boolean isAbsolute );

    public boolean isAbsolute()
    {
        return isAbsolute;
    }

    public boolean isRelative()
    {
        return !isAbsolute;
    }

    public T toAbsolutePath()
    {
        return isAbsolute ? (T) this : newPath( this.elements, true );
    }

    public T toRelativePath()
    {
        return !isAbsolute ? (T) this : newPath( this.elements, false );
    }

    public boolean isRoot()
    {
        return isAbsolute && elements.isEmpty();
    }

    public String getName()
    {
        return elements.isEmpty() ? "" : elements.get( elements.size() - 1 );
    }

    public int getElementCount()
    {
        return elements.size();
    }

    public String getElement( int index )
    {
        return elements.get( index );
    }

    public Iterator<String> iterator()
    {
        return elements.iterator();
    }

    public boolean startsWith( T path )
    {
        final ImmutableList<String> subList = this.elements.subList( 0, path.getElementCount() );
        return subList.equals( path.elements );
    }

    public boolean startsWith( String path )
    {
        return startsWith( parsePath( path ) );
    }

    public boolean endsWith( T path )
    {
        final int thisSize = this.elements.size();
        final ImmutableList<String> subList = this.elements.subList( thisSize - path.elements.size(), thisSize );
        return subList.equals( path.elements );
    }

    public boolean endsWith( String path )
    {
        return endsWith( parsePath( path ) );
    }

    public T resolve( T path )
    {
        final ImmutableList<String> elementList = ImmutableList.<String>builder().addAll( this.elements ).addAll( path.elements ).build();
        return newPath( elementList, isAbsolute );
    }

    public T resolve( String path )
    {
        return resolve( parsePath( path ) );
    }

    public T parent()
    {
        if ( this.elements.isEmpty() )
        {
            return null;
        }
        return newPath( Iterables.limit( this.elements, this.elements.size() - 1 ), isAbsolute );
    }

    @Override
    public String toString()
    {
        return refString;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || ( getClass() != o.getClass() ) )
        {
            return false;
        }
        final T that = (T) o;
        return refString.equals( that.refString );
    }

    @Override
    public int hashCode()
    {
        return refString.hashCode();
    }

    private T parsePath( final String path )
    {
        final Iterable<String> pathElements = Splitter.on( separator ).omitEmptyStrings().split( path );
        final boolean isAbsolute = path.startsWith( String.valueOf( separator ) );
        return newPath( pathElements, isAbsolute );
    }
}
