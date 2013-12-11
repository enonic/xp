package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.elasticsearch.common.Strings;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.core.entity.dao.NodeJcrDao;
import com.enonic.wem.core.entity.dao.NodeJcrHelper;
import com.enonic.wem.core.index.IndexService;

class ContentDaoHandlerRename
    extends AbstractContentDaoHandler
{
    ContentDaoHandlerRename( final Session session, final IndexService indexService )
    {
        super( session, indexService );
    }

    boolean handle( ContentId content, String newName )
        throws RepositoryException
    {
        final Node contentNode = doGetContentNode( content );
        if ( contentNode == null )
        {
            throw new ContentNotFoundException( content );
        }

        final String srcPath = contentNode.getPath();
        final String dstPath = contentNode.getParent().getPath() + "/" + newName;

        final boolean contentMoved = moveContentNode( srcPath, dstPath );

        moveContentAsNode( srcPath, dstPath );

        return contentMoved;
    }

    private void moveContentAsNode( final String srcPath, final String dstPath )
    {
        final NodeJcrDao nodeJcrDao = new NodeJcrDao( this.session );
        nodeJcrDao.moveNode( new NodePath( translateToNodePath( srcPath ) ), new NodePath( translateToNodePath( dstPath ) ) );
    }

    private String translateToNodePath( final String original )
    {
        return Strings.replace( original, ContentDao.CONTENTS_ROOT_PATH, NodeJcrHelper.NODES_JCRPATH + "/content/" );
    }

}
