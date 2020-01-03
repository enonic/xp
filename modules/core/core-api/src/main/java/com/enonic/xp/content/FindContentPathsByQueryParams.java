package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class FindContentPathsByQueryParams
{
    private final ContentQuery contentQuery;

    public FindContentPathsByQueryParams( final ContentQuery contentQuery )
    {
        this.contentQuery = contentQuery;
    }

    public ContentQuery getContentQuery()
    {
        return contentQuery;
    }
}
