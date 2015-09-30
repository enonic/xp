package com.enonic.xp.content;

import com.google.common.annotations.Beta;

@Beta
public class FindContentVersionsParams
{
    private final static int DEFAULT_SIZE = 10;

    private final ContentId contentId;

    private final int from;

    private final int size;

    private FindContentVersionsParams( Builder builder )
    {
        contentId = builder.contentId;
        from = builder.from;
        size = builder.size;
    }


    public ContentId getContentId()
    {
        return contentId;
    }

    public int getFrom()
    {
        return from;
    }

    public int getSize()
    {
        return size;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private ContentId contentId;

        private int from = 0;

        private int size = DEFAULT_SIZE;

        private Builder()
        {
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder from( int from )
        {
            this.from = from;
            return this;
        }

        public Builder size( int size )
        {
            this.size = size;
            return this;
        }

        public FindContentVersionsParams build()
        {
            return new FindContentVersionsParams( this );
        }
    }
}
