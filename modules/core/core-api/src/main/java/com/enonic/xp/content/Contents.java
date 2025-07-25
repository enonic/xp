package com.enonic.xp.content;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class Contents
    extends AbstractImmutableEntitySet<Content>
{
    private static final Contents EMPTY = new Contents( ImmutableSet.of() );

    private Contents( final ImmutableSet<Content> set )
    {
        super( set );
    }

    public ContentPaths getPaths()
    {
        return set.stream().map( Content::getPath ).collect( ContentPaths.collector() );
    }

    public ContentIds getIds()
    {
        return set.stream().map( Content::getId ).collect( ContentIds.collector() );
    }

    public static Contents empty()
    {
        return EMPTY;
    }

    public static Contents from( final Content... contents )
    {
        return fromInternal( ImmutableSet.copyOf( contents ) );
    }

    public static Contents from( final Iterable<? extends Content> contents )
    {
        return fromInternal( ImmutableSet.copyOf( contents ) );
    }

    public static Collector<Content, ?, Contents> collector()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), Contents::fromInternal );
    }

    private static Contents fromInternal( final ImmutableSet<Content> set )
    {
        return set.isEmpty() ? EMPTY : new Contents( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableSet.Builder<Content> contents = new ImmutableSet.Builder<>();

        public Builder add( Content content )
        {
            this.contents.add( content );
            return this;
        }

        public Builder addAll( Iterable<? extends Content> contents )
        {
            this.contents.addAll( contents );
            return this;
        }

        public Contents build()
        {
            return fromInternal( contents.build() );
        }
    }

    @Override
    public String toString()
    {
        final MoreObjects.ToStringHelper s = MoreObjects.toStringHelper( this );
        for ( final Content content : this )
        {
            s.add( "content", content.toString() );
        }

        return s.toString();
    }
}
