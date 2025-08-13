package com.enonic.xp.core.impl.content;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public final class ContentEventSyncCommandParams
{
    private final List<ContentToSync> contents;

    private ContentEventSyncCommandParams( final Builder builder )
    {
        this.contents = builder.contents.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public List<ContentToSync> getContents()
    {
        return contents;
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<ContentToSync> contents = ImmutableList.builder();

        public Builder addContents( Collection<ContentToSync> contents )
        {
            this.contents.addAll( contents );
            return this;
        }

        private void validate()
        {
            Preconditions.checkArgument( !contents.build().isEmpty(), "at least one content must be set." );
        }

        public ContentEventSyncCommandParams build()
        {
            validate();
            return new ContentEventSyncCommandParams( this );
        }

    }
}
