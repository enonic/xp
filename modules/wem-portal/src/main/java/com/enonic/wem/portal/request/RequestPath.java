package com.enonic.wem.portal.request;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

public final class RequestPath
{
    private final String path;

    private final ImmutableList<String> elements;

    private RequestPath( final ImmutableList<String> elements )
    {
        this.elements = elements;
        this.path = joinPath( this.elements );
    }

    public boolean isRoot()
    {
        return this.elements.isEmpty();
    }

    public RequestPath getParent()
    {
        if ( isRoot() )
        {
            return null;
        }
        else
        {
            return new RequestPath( this.elements.subList( 0, this.elements.size() - 1 ) );
        }
    }

    public RequestPath append( final String path )
    {
        return append( from( path ) );
    }

    public RequestPath append( final RequestPath path )
    {
        if ( path.elements.isEmpty() )
        {
            return this;
        }

        final ImmutableList.Builder<String> builder = ImmutableList.builder();
        builder.addAll( this.elements );
        builder.addAll( path.elements );

        return new RequestPath( builder.build() );
    }

    @Override
    public String toString()
    {
        return this.path;
    }

    private static ImmutableList<String> splitPath( final String path )
    {
        return ImmutableList.copyOf( Splitter.on( '/' ).omitEmptyStrings().trimResults().split( path ) );
    }

    private static String joinPath( final Iterable<String> elements )
    {
        return "/" + Joiner.on( '/' ).join( elements );
    }

    public static RequestPath from( final String path )
    {
        return new RequestPath( splitPath( path ) );
    }
}
