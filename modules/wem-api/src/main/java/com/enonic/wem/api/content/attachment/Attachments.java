package com.enonic.wem.api.content.attachment;

import java.util.HashSet;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public final class Attachments
    extends AbstractImmutableEntitySet<Attachment>
    implements Iterable<Attachment>
{
    private Attachments( final ImmutableSet<Attachment> set )
    {
        super( set );
    }

    public Attachments add( final Attachment... attachments )
    {
        return add( ImmutableSet.copyOf( attachments ) );
    }

    public Attachments add( final Iterable<Attachment> iterable )
    {
        return add( ImmutableSet.copyOf( iterable ) );
    }

    private Attachments add( final ImmutableSet<Attachment> attachments )
    {
        final HashSet<Attachment> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.addAll( attachments );
        return new Attachments( ImmutableSet.copyOf( tmp ) );
    }

    public Attachments remove( final Attachment... attachments )
    {
        return remove( ImmutableSet.copyOf( attachments ) );
    }

    public Attachments remove( final Iterable<Attachment> iterable )
    {
        return remove( ImmutableSet.copyOf( iterable ) );
    }

    private Attachments remove( final ImmutableSet<Attachment> attachments )
    {
        final HashSet<Attachment> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.removeAll( attachments );
        return new Attachments( ImmutableSet.copyOf( tmp ) );
    }

    @Override
    public int hashCode()
    {
        return this.set.hashCode();
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof Attachments ) && this.set.equals( ( (Attachments) o ).set );
    }

    @Override
    public String toString()
    {
        return this.set.toString();
    }

    public static Attachments empty()
    {
        final ImmutableSet<Attachment> list = ImmutableSet.of();
        return new Attachments( list );
    }

    public static Attachments from( final Attachment... paths )
    {
        return new Attachments( ImmutableSet.copyOf( paths ) );
    }

    public static Attachments from( final Iterable<Attachment> paths )
    {
        return new Attachments( ImmutableSet.copyOf( paths ) );
    }

    public static Builder newAttachments()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final ImmutableSet.Builder<Attachment> setBuilder = new ImmutableSet.Builder<>();

        public Builder add( final Attachment value )
        {
            setBuilder.add( value );
            return this;
        }

        public Attachments build()
        {
            return new Attachments( setBuilder.build() );
        }
    }
}
