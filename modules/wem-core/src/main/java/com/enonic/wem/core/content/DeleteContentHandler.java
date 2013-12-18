package com.enonic.wem.core.content;


import javax.inject.Inject;

import com.enonic.wem.api.command.content.DeleteContent;
import com.enonic.wem.api.content.DeleteContentResult;
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
        final DeleteContentResult deleteContentResult =
            new DeleteContentService( this.context.getJcrSession(), this.command, this.indexService ).execute();

        command.setResult( deleteContentResult );
    }

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}
