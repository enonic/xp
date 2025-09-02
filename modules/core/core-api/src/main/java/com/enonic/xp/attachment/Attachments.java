package com.enonic.xp.attachment;

import java.util.Arrays;
import java.util.Collection;
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
        return list.stream().filter( a -> a.getName().equals( name ) ).findAny().orElse( null );
    }

    public Attachment byLabel( final String label )
    {
        return list.stream().filter( a -> label.equals( a.getLabel() ) ).findAny().orElse( null );
    }

    public boolean hasByName( final String name )
    {
        return byName( name ) != null;
    }

    public boolean hasByLabel( final String label )
    {
        return byLabel( label ) != null;
    }

    public Attachments add( final Attachment... attachments )
    {
        return add( Arrays.asList( attachments ) );
    }

    public Attachments add( final Iterable<Attachment> attachments )
    {
        final ImmutableList.Builder<Attachment> listBuilder = ImmutableList.builder();
        listBuilder.addAll( this.list );
        listBuilder.addAll( attachments );
        return fromInternal( listBuilder.build() );
    }

    public static Attachments empty()
    {
        return EMPTY;
    }

    public static Attachments from( final Attachment... contents )
    {
        return fromInternal( ImmutableList.copyOf( contents ) );
    }

    public static Attachments from( final Iterable<? extends Attachment> contents )
    {
        return fromInternal( ImmutableList.copyOf( contents ) );
    }

    public static Attachments from( final Collection<? extends Attachment> contents )
    {
        return fromInternal( ImmutableList.copyOf( contents ) );
    }

    public static Collector<Attachment, ?, Attachments> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), Attachments::fromInternal );
    }

    private static Attachments fromInternal( final ImmutableList<Attachment> list )
    {
        if ( list.isEmpty() )
        {
            return EMPTY;
        }
        else
        {
            return new Attachments( list );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final ImmutableList.Builder<Attachment> builder = ImmutableList.builder();

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
