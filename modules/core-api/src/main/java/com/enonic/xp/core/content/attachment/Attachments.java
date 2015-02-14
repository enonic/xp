package com.enonic.xp.core.content.attachment;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.xp.core.support.AbstractImmutableEntityList;

public final class Attachments
    extends AbstractImmutableEntityList<Attachment>
{
    private final ImmutableMap<String, Attachment> attachmentByName;

    private Attachments( final ImmutableList<Attachment> list )
    {
        super( list );

        this.attachmentByName = Maps.uniqueIndex( list, new ToNameFunction() );
    }

    public Attachment byName( final String name )
    {
        return attachmentByName.get( name );
    }

    public Attachment byLabel( final String name )
    {
        for ( final Attachment attachment : this )
        {
            if ( name.equals( attachment.getLabel() ) )
            {
                return attachment;
            }
        }
        return null;
    }

    public boolean hasByName( final String name )
    {
        return attachmentByName.containsKey( name );
    }

    public boolean hasByLabel( final String label )
    {
        return byLabel( label ) != null;
    }

    public Attachments add( final Attachment... attachments )
    {
        final ImmutableList.Builder<Attachment> listBuilder = ImmutableList.builder();
        listBuilder.addAll( this.list );
        listBuilder.addAll( Arrays.asList( attachments ) );
        return new Attachments( listBuilder.build() );
    }

    private Attachments add( final Iterable<Attachment> attachments )
    {
        final ImmutableList.Builder<Attachment> listBuilder = ImmutableList.builder();
        listBuilder.addAll( this.list );
        listBuilder.addAll( attachments );
        return new Attachments( listBuilder.build() );
    }

    public static Attachments empty()
    {
        final ImmutableList<Attachment> list = ImmutableList.of();
        return new Attachments( list );
    }

    public static Attachments from( final Attachment... contents )
    {
        return new Attachments( ImmutableList.copyOf( contents ) );
    }

    public static Attachments from( final Iterable<? extends Attachment> contents )
    {
        return new Attachments( ImmutableList.copyOf( contents ) );
    }

    public static Attachments from( final Collection<? extends Attachment> contents )
    {
        return new Attachments( ImmutableList.copyOf( contents ) );
    }

    private final static class ToNameFunction
        implements Function<Attachment, String>
    {
        @Override
        public String apply( final Attachment value )
        {
            return value.getName();
        }
    }

    private final static class ToLabelFunction
        implements Function<Attachment, String>
    {
        @Override
        public String apply( final Attachment value )
        {
            return value.getLabel();
        }
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableList.Builder<Attachment> builder = ImmutableList.builder();

        public Builder add( Attachment attachment )
        {
            builder.add( attachment );
            return this;
        }

        public Builder addAll( Attachments attachments )
        {
            builder.addAll( attachments );
            return this;
        }

        public Attachments build()
        {
            return new Attachments( builder.build() );
        }
    }
}
