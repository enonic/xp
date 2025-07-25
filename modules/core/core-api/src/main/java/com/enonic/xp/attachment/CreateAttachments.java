package com.enonic.xp.attachment;

import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class CreateAttachments
    extends AbstractImmutableEntityList<CreateAttachment>
{
    private static final CreateAttachments EMPTY = new CreateAttachments( ImmutableList.of() );

    private CreateAttachments( final ImmutableList<CreateAttachment> list )
    {
        super( list );
    }

    public static CreateAttachments empty()
    {
        return EMPTY;
    }

    public static CreateAttachments from( final CreateAttachment... contents )
    {
        return fromInternal( ImmutableList.copyOf( contents ) );
    }

    public static CreateAttachments from( final Iterable<? extends CreateAttachment> contents )
    {
        return fromInternal( ImmutableList.copyOf( contents ) );
    }

    public static Collector<CreateAttachment, ?, CreateAttachments> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), CreateAttachments::fromInternal );
    }

    private static CreateAttachments fromInternal( final ImmutableList<CreateAttachment> contents )
    {
        return contents.isEmpty() ? EMPTY : new CreateAttachments( contents );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<CreateAttachment> contents = ImmutableList.builder();

        public Builder add( CreateAttachment value )
        {
            contents.add( value );
            return this;
        }

        public Builder addAll( final Iterable<? extends CreateAttachment> values )
        {
            contents.addAll( values );
            return this;
        }

        public CreateAttachments build()
        {
            return new CreateAttachments( contents.build() );
        }
    }
}
