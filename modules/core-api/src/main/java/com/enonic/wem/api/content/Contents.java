package com.enonic.wem.api.content;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public final class Contents
    extends AbstractImmutableEntitySet<Content>
{
    private final ImmutableMap<ContentId, Content> map;

    private Contents( final Set<Content> set )
    {
        super( ImmutableSet.copyOf( set ) );
        this.map = Maps.uniqueIndex( set, new ToIdFunction() );
    }

    public ContentPaths getPaths()
    {
        final Collection<ContentPath> paths = Collections2.transform( this.set, new ToPathFunction() );
        return ContentPaths.from( paths );
    }

    public ContentIds getIds()
    {
        final Collection<ContentId> ids = Collections2.transform( this.set, new ToIdFunction() );
        return ContentIds.from( ids );
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

    private final static class ToPathFunction
        implements Function<Content, ContentPath>
    {
        @Override
        public ContentPath apply( final Content value )
        {
            return value.getPath();
        }
    }

    private final static class ToIdFunction
        implements Function<Content, ContentId>
    {
        @Override
        public ContentId apply( final Content value )
        {
            return value.getId();
        }
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Set<Content> contents = Sets.newLinkedHashSet();

        public Builder add( Content content )
        {
            contents.add( content );
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
