package com.enonic.wem.api.content;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public final class Contents
    extends AbstractImmutableEntityList<Content>
{
    private Contents( final ImmutableList<Content> list )
    {
        super( list );
    }

    public ContentPaths getPaths()
    {
        final Collection<ContentPath> paths = Collections2.transform( this.list, new ToPathFunction() );
        return ContentPaths.from( paths );
    }

    public ContentIds getIds()
    {
        final Collection<ContentId> ids = Collections2.transform( this.list, new ToIdFunction() );
        return ContentIds.from( ids );
    }

    public static Contents empty()
    {
        final ImmutableList<Content> list = ImmutableList.of();
        return new Contents( list );
    }

    public static Contents from( final Content... contents )
    {
        return new Contents( ImmutableList.copyOf( contents ) );
    }

    public static Contents from( final Iterable<? extends Content> contents )
    {
        return new Contents( ImmutableList.copyOf( contents ) );
    }

    public static Contents from( final Collection<? extends Content> contents )
    {
        return new Contents( ImmutableList.copyOf( contents ) );
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
        private ImmutableList.Builder<Content> builder = ImmutableList.builder();

        public Builder add( Content content )
        {
            builder.add( content );
            return this;
        }

        public Contents build()
        {
            return new Contents( builder.build() );
        }
    }
}
