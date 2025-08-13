package com.enonic.xp.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class GetContentByIdsParams
{
    private final ContentIds ids;

    @Deprecated
    public GetContentByIdsParams( final ContentIds ids ) {
        Preconditions.checkNotNull( ids, "ids must be specified" );
        this.ids = ids;
    }

    public GetContentByIdsParams( final Builder builder )
    {
        this.ids = builder.contentIds;
    }

    public ContentIds getIds()
    {
        return this.ids;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ContentIds contentIds;

        private Builder()
        {
        }

        public Builder contentIds( final ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        public GetContentByIdsParams build()
        {
            Preconditions.checkNotNull( this.contentIds, "ids must be specified" );
            return new GetContentByIdsParams( this );
        }
    }
}
