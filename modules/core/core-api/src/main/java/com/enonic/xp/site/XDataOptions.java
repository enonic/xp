package com.enonic.xp.site;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class XDataOptions
    extends AbstractImmutableEntityList<XDataOption>
{
    private static final XDataOptions EMPTY = new XDataOptions( ImmutableList.of() );

    private XDataOptions( final ImmutableList<XDataOption> list )
    {
        super( list );
    }

    public static XDataOptions empty()
    {
        return EMPTY;
    }

    public static Collector<XDataOption, ?, XDataOptions> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), XDataOptions::fromInternal );
    }

    private static XDataOptions fromInternal( final ImmutableList<XDataOption> list )
    {
        return list.isEmpty() ? EMPTY : new XDataOptions( list );
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private final ImmutableList.Builder<XDataOption> builder = ImmutableList.builder();

        private Builder()
        {
        }

        public Builder add( XDataOption xDataOption )
        {
            builder.add( xDataOption );
            return this;
        }

        public Builder addAll( Iterable<XDataOption> xDataOptions )
        {
            builder.addAll( xDataOptions );
            return this;
        }

        public XDataOptions build()
        {
            return fromInternal( builder.build() );
        }
    }

}
