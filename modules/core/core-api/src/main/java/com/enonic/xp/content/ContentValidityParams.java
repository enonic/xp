package com.enonic.xp.content;

public class ContentValidityParams
{
    private final ContentIds contentIds;

    private ContentValidityParams( final Builder builder )
    {
        this.contentIds = builder.contentIds;
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
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
            return new ContentValidityParams( this );
        }
    }
}
