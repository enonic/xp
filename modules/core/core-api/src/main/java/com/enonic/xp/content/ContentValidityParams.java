package com.enonic.xp.content;

import java.util.Objects;

public final class ContentValidityParams
{
    private final ContentIds contentIds;

    private ContentValidityParams( final ContentIds contentIds )
    {
        this.contentIds = contentIds;
    }

    public ContentIds getContentIds()
    {
        return contentIds;
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

        public ContentValidityParams build()
        {
            Objects.requireNonNull( this.contentIds, "contentIds is required" );
            return new ContentValidityParams( this.contentIds );
        }
    }
}
