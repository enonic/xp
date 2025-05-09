package com.enonic.xp.content;

import java.util.Collection;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class Contents
    extends AbstractImmutableEntitySet<Content>
{
    private Contents( final ImmutableSet<Content> set )
    {
        super( set );
    }

    public ContentPaths getPaths()
    {
        return ContentPaths.from( set.stream().map( Content::getPath ).collect( ImmutableSet.toImmutableSet() ) );
    }

    public ContentIds getIds()
    {
        return ContentIds.from( set.stream().map( Content::getId ).collect( ImmutableSet.toImmutableSet() ) );
    }

    public static Contents empty()
    {
        return new Contents( ImmutableSet.of() );
    }

    public static Contents from( final Content... contents )
    {
        return new Contents( ImmutableSet.copyOf( contents ) );
    }

    public static Contents from( final Iterable<? extends Content> contents )
    {
        return new Contents( ImmutableSet.copyOf( contents ) );
    }

    public static Contents from( final Collection<? extends Content> contents )
    {
        return new Contents( ImmutableSet.copyOf( contents ) );
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

        public Builder addAll( Contents contents )
        {
            this.contents.addAll( contents.getSet() );
            return this;
        }


        public Contents build()
        {
            return new Contents( contents.build() );
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
