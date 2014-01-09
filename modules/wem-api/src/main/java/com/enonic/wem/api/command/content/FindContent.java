package com.enonic.wem.api.command.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.query.ContentQuery;
import com.enonic.wem.api.content.query.ContentQueryResult;

public class FindContent
    extends Command<ContentQueryResult>
{
    private ContentQuery contentQuery;

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( contentQuery );
    }

    public FindContent query( final ContentQuery ContentQuery )
    {
        this.contentQuery = ContentQuery;
        return this;
    }

    public ContentQuery getContentQuery()
    {
        return contentQuery;
    }
}
