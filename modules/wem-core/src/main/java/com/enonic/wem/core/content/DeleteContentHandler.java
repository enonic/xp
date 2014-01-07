package com.enonic.wem.core.content;


import javax.inject.Inject;

import com.enonic.wem.api.command.content.DeleteContent;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.index.IndexService;


public class DeleteContentHandler
    extends CommandHandler<DeleteContent>
{
    private IndexService indexService;

    @Override
    public void handle()
        throws Exception
    {
        command.setResult( new DeleteContentService( this.context, this.command, this.indexService ).execute() );
    }

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}
