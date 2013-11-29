package com.enonic.wem.core.blob.binary.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.binary.BinaryId;
import com.enonic.wem.core.jcr.JcrHelper;

public class BinaryDaoHandlerDelete
    extends AbstractBinaryDaoHandler
{
    public BinaryDaoHandlerDelete( final Session session )
    {
        super( session );
    }

    public boolean handle( final BinaryId binaryId )
        throws RepositoryException

    {
        Preconditions.checkNotNull( binaryId );

        final String binaryNodeId = binaryId.toString();
        final Node binaryNode = JcrHelper.getNodeById( session, binaryNodeId );
        if ( binaryNode == null )
        {
            return false;
        }

        binaryNode.remove();
        return true;
    }
}
