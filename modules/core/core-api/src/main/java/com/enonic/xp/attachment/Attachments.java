package com.enonic.xp.attachment;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class Attachments
    extends AbstractImmutableEntityList<Attachment>
{
    private static final Attachments EMPTY = new Attachments( ImmutableList.of() );

    private Attachments( final ImmutableList<Attachment> list )
    {
        super( list );
    }

    public Attachment byName( final String name )
    {
        return stream().filter( a -> a.getName().equals( name ) ).findFirst().orElse( null );
    }

    public Attachment byLabel( final String label )
    {
        return stream().filter( a -> label.equals( a.getLabel() ) ).findFirst().orElse( null );
    }

    public boolean hasByName( final String name )
    {
        return byName( name ) != null;
    }

    public boolean hasByLabel( final String label )
    {
        return byLabel( label ) != null;
    }

    public static Attachments empty()
    {
        return EMPTY;
    }

    public static Attachments from( final Attachment... attachments )
    {
        return fromInternal( ImmutableList.copyOf( attachments ) );
    }

    public static Attachments from( final Iterable<Attachment> attachments )
    {
        return attachments instanceof Attachments a ? a : fromInternal( ImmutableList.copyOf( attachments ) );
    }

    public static Collector<Attachment, ?, Attachments> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), Attachments::fromInternal );
    }

    private static Attachments fromInternal( final ImmutableList<Attachment> list )
    {
        return list.isEmpty() ? EMPTY : new Attachments( list );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<Attachment> builder = ImmutableList.builder();

        private Builder()
        {
        }

        public Builder add( Attachment attachment )
        {
            builder.add( attachment );
            return this;
        }

        public Attachments build()
        {
            return fromInternal( builder.build() );
        }
    }
}
