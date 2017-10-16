package com.enonic.xp.content;

import com.google.common.annotations.Beta;

@Beta
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
