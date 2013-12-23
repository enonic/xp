package com.enonic.wem.core.content;


import javax.jcr.Session;

import com.enonic.wem.api.command.content.DeleteContent;
import com.enonic.wem.api.command.entity.DeleteNodeByPath;
import com.enonic.wem.api.command.entity.DeleteNodeResult;
import com.enonic.wem.api.content.DeleteContentResult;
import com.enonic.wem.core.entity.DeleteNodeByPathService;
import com.enonic.wem.core.index.IndexService;


class DeleteContentService
extends ContentService
{
    private final DeleteContent command;

    private final IndexService indexService;

    DeleteContentService( final Session session, final DeleteContent command, final IndexService indexService )
    {
        super( session );
        this.command = command;
        this.indexService = indexService;
    }

    public DeleteContentResult execute()
        throws Exception
    {
        final DeleteNodeByPath deleteNodeByPathCommand =
            new DeleteNodeByPath( ContentNodeHelper.translateContentPathToNodePath( command.getContentPath() ) );
        final DeleteNodeResult deleteNodeResult =
            new DeleteNodeByPathService( this.session, this.indexService, deleteNodeByPathCommand ).execute();

        switch ( deleteNodeResult )
        {
            case NOT_FOUND:
                return DeleteContentResult.NOT_FOUND;
            case UNABLE_TO_DELETE:
                return DeleteContentResult.UNABLE_TO_DELETE;
            default:
                return DeleteContentResult.SUCCESS;
        }
    }
}
