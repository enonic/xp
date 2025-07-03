package com.enonic.xp.attachment;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class CreateAttachments
    extends AbstractImmutableEntitySet<CreateAttachment>
{
    private static final CreateAttachments EMPTY = new CreateAttachments( ImmutableSet.of() );

    private CreateAttachments( final Set<CreateAttachment> set )
    {
        super( ImmutableSet.copyOf( set ) );
    }

    public static CreateAttachments empty()
    {
        return EMPTY;
    }

    public static CreateAttachments from( final CreateAttachment... contents )
    {
        return fromInternal( ImmutableSet.copyOf( contents ) );
    }

    public static CreateAttachments from( final Iterable<? extends CreateAttachment> contents )
    {
        return fromInternal( ImmutableSet.copyOf( contents ) );
    }

    public static CreateAttachments from( final Collection<? extends CreateAttachment> contents )
    {
        return fromInternal( ImmutableSet.copyOf( contents ) );
    }

    public static Collector<CreateAttachment, ?, CreateAttachments> collecting()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), CreateAttachments::fromInternal );
    }

    private static CreateAttachments fromInternal( final ImmutableSet<CreateAttachment> contents )
    {
        return contents.isEmpty() ? EMPTY : new CreateAttachments( contents );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final Set<CreateAttachment> contents = new LinkedHashSet<>();

        public Builder add( CreateAttachment value )
        {
            contents.add( value );
            return this;
        }

        public Builder add( CreateAttachments value )
        {
            contents.addAll( value.set );
            return this;
        }

        public CreateAttachments build()
        {
            return new CreateAttachments( contents );
        }
    }
}
