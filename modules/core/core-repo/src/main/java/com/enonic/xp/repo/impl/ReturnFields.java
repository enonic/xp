package com.enonic.xp.repo.impl;

import java.util.Arrays;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.index.IndexPath;

public class ReturnFields
{
    private static final ReturnFields EMPTY = new ReturnFields( ImmutableSet.of() );

    private final ImmutableSet<String> indexPaths;

    private ReturnFields( final ImmutableSet<String> set )
    {
        indexPaths = set;
    }

    public static ReturnFields empty()
    {
        return EMPTY;
    }

    public static ReturnFields from( final IndexPath... indexPath )
    {
        return new ReturnFields( Arrays.stream( indexPath ).map( IndexPath::getPath ).collect( ImmutableSet.toImmutableSet() ) );
    }

    public ReturnFields add( final IndexPath indexPath )
    {
        return new ReturnFields( ImmutableSet.<String>builder().addAll( this.indexPaths ).add( indexPath.getPath() ).build() );
    }

    public String[] getReturnFieldNames()
    {
        return this.indexPaths.toArray( String[]::new );
    }

    public boolean isNotEmpty()
    {
        return !indexPaths.isEmpty();
    }
}
