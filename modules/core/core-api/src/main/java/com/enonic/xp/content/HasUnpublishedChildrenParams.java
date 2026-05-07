package com.enonic.xp.content;

import static java.util.Objects.requireNonNull;


public final class HasUnpublishedChildrenParams
{
    private final ContentId contentId;

    private HasUnpublishedChildrenParams( final Builder builder )
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

        private Builder()
        {
        }

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public HasUnpublishedChildrenParams build()
        {
            requireNonNull( this.contentId, "contentId is required" );
            return new HasUnpublishedChildrenParams( this );
        }
    }
}
