package com.enonic.xp.core.impl.content;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public final class ContentEventSyncCommandParams
{
    private final List<ContentToSync> contents;

    private final Map<String, Object> eventMetadata;

    private ContentEventSyncCommandParams( final Builder builder )
    {
        this.contents = builder.contents.build();
        this.eventMetadata = builder.eventMetadata.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public List<ContentToSync> getContents()
    {
        return contents;
    }

    public Map<String, Object> getEventMetadata()
    {
        return eventMetadata;
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<ContentToSync> contents = ImmutableList.builder();

        private final ImmutableMap.Builder<String, Object> eventMetadata = ImmutableMap.builder();

        public Builder addContents( Collection<ContentToSync> contents )
        {
            this.contents.addAll( contents );
            return this;
        }

        public Builder setEventMetadata( final Map<String, Object> eventMetadata )
        {
            this.eventMetadata.putAll( eventMetadata );
            return this;
        }

        private void validate()
        {
            Preconditions.checkArgument( !contents.build().isEmpty(), "At least one content must be set" );
        }

        public ContentEventSyncCommandParams build()
        {
            validate();
            return new ContentEventSyncCommandParams( this );
        }

    }
}
