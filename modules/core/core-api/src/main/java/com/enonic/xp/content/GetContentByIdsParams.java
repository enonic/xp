package com.enonic.xp.content;

import static java.util.Objects.requireNonNull;


public final class GetContentByIdsParams
{
    private final ContentIds ids;

    @Deprecated
    public GetContentByIdsParams( final ContentIds ids )
    {
        this.ids = requireNonNull( ids, "ids must be specified" );
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
            requireNonNull( this.contentIds, "contentIds is required" );
            return new GetContentByIdsParams( this );
        }
    }
}
