package com.enonic.wem.core.content;

import javax.inject.Inject;

import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.command.content.GetContentByPath;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.core.command.CommandHandler;


public class GetContentByPathHandler
    extends CommandHandler<GetContentByPath>
{
    @Inject
    private NodeService nodeService;

    @Inject
    private ContentTypeService contentTypeService;

    @Inject
    private BlobService blobService;

    @Override
    public void handle()
        throws Exception
    {
        command.setResult( new GetContentByPathService(
            this.context, this.command, this.nodeService, this.contentTypeService, this.blobService ).execute() );
    }
}
