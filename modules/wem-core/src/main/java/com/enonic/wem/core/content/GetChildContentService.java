package com.enonic.wem.core.content;

import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.GetNodesByParentParams;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.schema.content.ContentTypeService;


final class GetChildContentService
    extends AbstractContentCommand
{
    private final ContentPath contentPath;

    private boolean populateChildIds = false;

    GetChildContentService( final ContentPath contentPath,
                            final NodeService nodeService,
                            final ContentTypeService contentTypeService,
                            final BlobService blobService )
    {
        this.contentPath = contentPath;
        super.nodeService = nodeService;
        super.contentTypeService = contentTypeService;
        super.blobService = blobService;
    }

    GetChildContentService populateChildIds( boolean value )
    {
        this.populateChildIds = value;
        return this;
    }

    Contents execute()
    {
        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( this.contentPath );

        final Nodes nodes = nodeService.getByParent( new GetNodesByParentParams( nodePath ) );
        final Contents contents = getTranslator().fromNodes( removeNonContentNodes( nodes ) );

        if ( populateChildIds )
        {
            return new ChildContentIdsResolver( this.nodeService, this.contentTypeService, this.blobService ).resolve( contents );
        }
        else
        {
            return contents;
        }
    }

}
