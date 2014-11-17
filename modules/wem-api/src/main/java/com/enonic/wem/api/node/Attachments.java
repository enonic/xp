package com.enonic.wem.api.node;


import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public class Attachments
    extends AbstractImmutableEntityList<Attachment>
{
    private final ImmutableMap<String, Attachment> attachmentByName;

    private Attachments( final ImmutableList<Attachment> list )
    {
        super( list );

        this.attachmentByName = Maps.uniqueIndex( list, new ToNameFunction() );
    }

    public Attachment getAttachment( final String name )
    {
        return attachmentByName.get( name );
    }

    public boolean hasAttachment( final String name )
    {
        return attachmentByName.containsKey( name );
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
            return value.name();
        }
    }

    public static Builder builder()
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
