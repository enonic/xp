package com.enonic.wem.core.content;

import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.entity.dao.NodeJcrDao;

final class GetRootContentCommand
    extends AbstractContentCommand<GetRootContentCommand>
{
    Contents execute()
    {
        final NodePath nodePath = NodeJcrDao.CONTENT_ROOT_NODE.asAbsolute();
        final Nodes rootNodes = nodeService.getByParent( nodePath );
        final Contents contents = getTranslator().fromNodes( removeNonContentNodes( rootNodes ) );

        return new ChildContentIdsResolver( this.nodeService, this.contentTypeService, this.blobService ).resolve( contents );
    }
}
