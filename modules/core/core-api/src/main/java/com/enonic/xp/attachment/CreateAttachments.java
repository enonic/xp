package com.enonic.xp.attachment;

import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class CreateAttachments
    extends AbstractImmutableEntityList<CreateAttachment>
{
    private static final CreateAttachments EMPTY = new CreateAttachments( ImmutableMap.of() );

    private CreateAttachments( final ImmutableMap<String, CreateAttachment> map )
    {
        super( map.values().asList() );
    }

    public static CreateAttachments empty()
    {
        return EMPTY;
    }

    public static CreateAttachments from( final CreateAttachment... attachments )
    {
        return checkDistinct( ImmutableList.copyOf( attachments ) );
    }

    public static CreateAttachments from( final Iterable<CreateAttachment> attachments )
    {
        return attachments instanceof CreateAttachments a ? a : checkDistinct( ImmutableList.copyOf( attachments ) );
    }

    public static Collector<CreateAttachment, ?, CreateAttachments> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), CreateAttachments::checkDistinct );
    }

    private static CreateAttachments fromInternal( final ImmutableMap<String, CreateAttachment> map )
    {
        return map.isEmpty() ? EMPTY : new CreateAttachments( map );
    }

    private static CreateAttachments checkDistinct( final ImmutableList<CreateAttachment> list )
    {
        return fromInternal( list.stream().collect( ImmutableMap.toImmutableMap( CreateAttachment::getName, Function.identity() ) ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableMap.Builder<String, CreateAttachment> map = ImmutableMap.builder();

        private Builder()
        {
        }

        public Builder add( CreateAttachment value )
        {
            map.put(  value.getName(), value );
            return this;
        }

        public Builder addAll( final Iterable<CreateAttachment> values )
        {
            for ( CreateAttachment value : values )
            {
                map.put( value.getName(), value );
            }
            return this;
        }

        public CreateAttachments build()
        {
            return fromInternal( map.buildOrThrow() );
        }

        public CreateAttachments buildKeepingLast()
        {
            return fromInternal( map.buildKeepingLast() );
        }
    }
}
