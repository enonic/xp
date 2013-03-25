package com.enonic.wem.core.content.binary.dao;


import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.binary.Binary;
import com.enonic.wem.api.content.binary.BinaryId;
import com.enonic.wem.core.jcr.JcrConstants;
import com.enonic.wem.core.jcr.JcrHelper;


final class BinaryDaoHandlerCreate
    extends AbstractBinaryDaoHandler
{
    BinaryDaoHandlerCreate( final Session session )
    {
        super( session );
    }

    BinaryId handle( final Binary binary )
        throws RepositoryException
    {
        Preconditions.checkNotNull( binary );

        final Node root = session.getRootNode();
        final String binariesNodePath = BINARIES_PATH;
        final Node binariesNode = root.getNode( binariesNodePath );

        String nodeName = getUniqueName();
        while ( binariesNode.hasNode( nodeName ) )
        {
            nodeName = getUniqueName();
        }
        final Node binaryNode = binariesNode.addNode( nodeName, JcrConstants.BINARY_NODETYPE );
        JcrHelper.setPropertyBinary( binaryNode, DATA_PROPERTY, binary.asInputStream() );

        return BinaryId.from( binaryNode.getIdentifier() );
    }

    private String getUniqueName()
    {
        final UUID id = UUID.randomUUID();
        return id.toString();
    }
}
