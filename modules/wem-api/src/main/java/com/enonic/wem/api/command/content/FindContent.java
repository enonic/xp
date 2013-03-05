package com.enonic.wem.api.command.content;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.query.ContentIndexQuery;
import com.enonic.wem.api.content.query.ContentQueryHits;

public class FindContent
    extends Command<ContentQueryHits>
{
    private ContentIndexQuery contentIndexQuery;


    @Override
    public void validate()
    {

    }

    public FindContent query( final ContentIndexQuery contentIndexQuery )
    {
        this.contentIndexQuery = contentIndexQuery;
        return this;
    }

    public ContentIndexQuery getContentIndexQuery()
    {
        return contentIndexQuery;
    }
}
