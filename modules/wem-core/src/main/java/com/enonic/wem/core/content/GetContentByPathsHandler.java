package com.enonic.wem.core.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.GetContentByPaths;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.NoNodeAtPathFoundException;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.core.command.CommandHandler;


public class GetContentByPathsHandler
    extends CommandHandler<GetContentByPaths>
{
    @Inject
    private NodeService nodeService;

    @Inject
    private ContentTypeService contentTypeService;

    @Override
    public void handle()
        throws Exception
    {
        final Contents contents;

        try
        {
            contents = new GetContentByPathsService( this.context, this.command, this.nodeService, this.contentTypeService ).execute();
        }
        catch ( NoNodeAtPathFoundException ex )
        {
            throw new ContentNotFoundException( ContentPath.from( ex.getPath().toString() ) );
        }

        command.setResult( contents );
    }
}
