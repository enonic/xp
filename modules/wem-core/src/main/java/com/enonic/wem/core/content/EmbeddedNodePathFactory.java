package com.enonic.wem.core.content;

import com.enonic.wem.api.content.ContentName;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.core.entity.dao.NodeElasticsearchDao;

public class EmbeddedNodePathFactory
{
    public static NodePath create( final ContentPath parentPath, final ContentName name )
    {
        return NodePath.
            newNodePath( NodePath.
                newNodePath( new NodePath( NodeElasticsearchDao.CONTENT_ROOT_NODE_NAME ), parentPath.getRelativePath() ).build() ).
            addElement( NodeElasticsearchDao.EMBEDDED_NODE_ROOT_PATH ).
            addElement( name.toString() ).build().asAbsolute();
    }
}
