package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class FindContentByQueryParams
{
    private final ContentQuery contentQuery;

    private final boolean populateChildren;

    private FindContentByQueryParams( final Builder builder )
    {
        contentQuery = builder.contentQuery;
        populateChildren = builder.populateChildren;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentQuery getContentQuery()
    {
        return contentQuery;
    }

    public boolean isPopulateChildren()
    {
        return populateChildren;
    }

    public static final class Builder
    {
        private ContentQuery contentQuery;

        private boolean populateChildren = false;

        private Builder()
        {
        }

        public Builder contentQuery( final ContentQuery contentQuery )
        {
            this.contentQuery = contentQuery;
            return this;
        }

        public Builder populateChildren( final boolean populateChildren )
        {
            this.populateChildren = populateChildren;
            return this;
        }

        public FindContentByQueryParams build()
        {
            return new FindContentByQueryParams( this );
        }
    }
}
