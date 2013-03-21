package com.enonic.wem.core.content.binary.dao;


import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.binary.Binary;
import com.enonic.wem.api.content.binary.BinaryId;
import com.enonic.wem.core.jcr.JcrHelper;


final class BinaryDaoHandlerGet
    extends AbstractBinaryDaoHandler
{
    BinaryDaoHandlerGet( final Session session )
    {
        super( session );
    }

    Binary handle( final BinaryId binaryId )
        throws RepositoryException
    {
        Preconditions.checkNotNull( binaryId );

        final String binaryNodeId = binaryId.toString();
        final Node binaryNode = JcrHelper.getNodeById( session, binaryNodeId );
        if ( binaryNode == null )
        {
            return null;
        }

        final InputStream dataInputStream = JcrHelper.getPropertyBinaryAsInputStream( binaryNode, DATA_PROPERTY );
        return Binary.from( dataInputStream );
    }
}
