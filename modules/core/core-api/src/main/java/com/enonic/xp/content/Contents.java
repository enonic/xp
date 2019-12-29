package com.enonic.xp.content;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class Contents
    extends AbstractImmutableEntitySet<Content>
{
    private final ImmutableMap<ContentId, Content> map;

    private Contents( final Set<Content> set )
    {
        super( ImmutableSet.copyOf( set ) );
        this.map = set.stream().collect( ImmutableMap.toImmutableMap( Content::getId, Function.identity() ) );
    }

    public ContentPaths getPaths()
    {
        return ContentPaths.from( set.stream().map( Content::getPath ).collect( ImmutableSet.toImmutableSet() ) );
    }

    public ContentIds getIds()
    {
        return ContentIds.from( map.keySet() );
    }

    public Content getContentById( final ContentId contentId )
    {
        return this.map.get( contentId );
    }

    public static Contents empty()
    {
        final ImmutableSet<Content> set = ImmutableSet.of();
        return new Contents( set );
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

    public static class Builder
    {
        private Set<Content> contents = new LinkedHashSet<>();

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
            return new Contents( contents );
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
