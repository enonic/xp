package com.enonic.xp.content;

import com.enonic.xp.branch.Branch;

import static java.util.Objects.requireNonNull;


public final class GetPublishStatusesParams
{
    private final ContentIds contentIds;

    private final Branch target;

    private GetPublishStatusesParams( final Builder builder )
    {
        this.contentIds = builder.contentIds;
        this.target = null;
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public Branch getTarget()
    {
        return target;
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

        public GetPublishStatusesParams build()
        {
            requireNonNull( this.contentIds, "contentIds is required" );
            return new GetPublishStatusesParams( this );
        }
    }
}
