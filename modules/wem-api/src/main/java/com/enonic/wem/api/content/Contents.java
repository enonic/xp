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
        final Collection<ContentPath> paths = Collections2.transform( this.list, new ToKeyFunction() );
        return ContentPaths.from( paths );
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

    private final static class ToKeyFunction
        implements Function<Content, ContentPath>
    {
        @Override
        public ContentPath apply( final Content value )
        {
            return value.getPath();
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
