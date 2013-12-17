package com.enonic.wem.core.content;

import com.enonic.wem.api.content.ContentName;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.core.entity.dao.NodeJcrDao;

public class EmbeddedNodePathFactory
{
    public static NodePath create( final ContentPath parentPath, final ContentName name )
    {
        return NodePath.
            newNodePath( NodePath.
                newNodePath( new NodePath( NodeJcrDao.CONTENT_ROOT_NODE_NAME ), parentPath.getRelativePath().toString() ).build() ).
            addElement( NodeJcrDao.EMBEDDED_NODE_ROOT_PATH ).
            addElement( name.toString() ).build().asAbsolute();
    }

}
