package com.enonic.wem.api.content;

import com.enonic.wem.api.context.Context;

public class GetContentVersionsParams
{
    private final ContentId contentId;

    private final int from;

    private final int size;

    private GetContentVersionsParams( Builder builder )
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

        private int from;

        private int size;

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

        public GetContentVersionsParams build()
        {
            return new GetContentVersionsParams( this );
        }
    }
}
