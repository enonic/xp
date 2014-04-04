package com.enonic.wem.core.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.GetRootContent;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.core.command.CommandHandler;

public class GetRootContentHandler
    extends CommandHandler<GetRootContent>
{
    @Inject
    private NodeService nodeService;

    @Inject
    private ContentTypeService contentTypeService;

    @Override
    public void handle()
        throws Exception
    {
        final Contents contents = new GetRootContentService( this.context, this.command, this.nodeService, this.contentTypeService ).execute();

        command.setResult( contents );
    }
}
