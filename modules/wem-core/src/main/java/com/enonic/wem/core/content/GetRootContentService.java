package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetRootContent;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.GetNodesByParentParams;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.entity.dao.NodeJcrDao;

public class GetRootContentService
    extends ContentService
{
    public GetRootContentService( final CommandContext context,
                                  @SuppressWarnings("UnusedParameters") final GetRootContent command,
                                  final NodeService nodeService,
                                  final ContentTypeService contentTypeService )
    {
        super( context, nodeService,contentTypeService );
    }

    public Contents execute()
        throws Exception
    {
        final NodePath nodePath = NodeJcrDao.CONTENT_ROOT_NODE.asAbsolute();
        final Nodes rootNodes = nodeService.getByParent( new GetNodesByParentParams( nodePath ) );
        final Contents contents = translator.fromNodes( removeNonContentNodes( rootNodes ) );

        return new ChildContentIdsResolver( this.context, this.nodeService, this.contentTypeService ).resolve( contents );
    }
}
