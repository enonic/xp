package com.enonic.xp.content;

import com.google.common.base.Preconditions;

public final class GetActiveContentVersionParams
{
    private final ContentId contentId;

    private GetActiveContentVersionParams( final Builder builder )
    {
        this.contentId = builder.contentId;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private ContentId contentId;

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public GetActiveContentVersionParams build()
        {
            Preconditions.checkNotNull( this.contentId, "Content id cannot be null" );
            return new GetActiveContentVersionParams( this );
        }
    }
}
