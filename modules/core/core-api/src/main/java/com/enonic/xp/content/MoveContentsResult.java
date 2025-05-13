package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class MoveContentsResult
{
    private final ContentIds movedContents;

    private final String contentName;

    private MoveContentsResult( Builder builder )
    {
        this.movedContents = builder.movedContents.build();
        this.contentName = builder.contentName;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getMovedContents()
    {
        return movedContents;
    }

    public String getContentName()
    {
        return contentName;
    }

    public static final class Builder
    {
        private final ContentIds.Builder movedContents = ContentIds.create();

        private String contentName;

        private Builder()
        {
        }

        public Builder addMoved( final ContentId contentId )
        {
            this.movedContents.add( contentId );
            return this;
        }

        public Builder addMoved( final ContentIds contentIds )
        {
            this.movedContents.addAll( contentIds );
            return this;
        }

        public Builder setContentName( final String contentName )
        {
            this.contentName = contentName;
            return this;
        }

        public MoveContentsResult build()
        {
            return new MoveContentsResult( this );
        }
    }
}
