package com.enonic.xp.archive;

import java.util.List;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ContentId;

@PublicApi
public final class ResolveArchivedParams
{
    private final List<ContentId> contents;

    public ResolveArchivedParams( Builder builder )
    {
        this.contents = builder.contents;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public List<ContentId> getContents()
    {
        return contents;
    }

    public static final class Builder
    {
        private List<ContentId> contents;

        private Builder()
        {
        }

        public Builder contents( final List<ContentId> contents )
        {
            this.contents = contents;
            return this;
        }

        private void validate()
        {
            Preconditions.checkArgument( this.contents != null && !this.contents.isEmpty(), "contents must be set" );
        }

        public ResolveArchivedParams build()
        {
            validate();
            return new ResolveArchivedParams( this );
        }
    }
}
