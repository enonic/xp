package com.enonic.wem.core.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.GetContentByIds;
import com.enonic.wem.api.command.schema.content.ContentTypeService;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.NoEntityWithIdFoundException;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.core.command.CommandHandler;


public class GetContentByIdsHandler
    extends CommandHandler<GetContentByIds>
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
            contents = new GetContentByIdsService( this.context, this.command, this.nodeService, this.contentTypeService ).execute();
        }
        catch ( NoEntityWithIdFoundException ex )
        {
            final ContentId contentId = ContentId.from( ex.getId().toString() );
            throw new ContentNotFoundException( contentId );

        }

        command.setResult( this.command.doGetCHildrenIds()
                               ? new ChildContentIdsResolver( this.context, this.nodeService, this.contentTypeService ).resolve( contents )
                               : contents );
    }

}
