package com.enonic.wem.core.content.binary.dao;


import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.binary.Binary;
import com.enonic.wem.api.content.binary.BinaryId;


public class BinaryDaoImpl
    implements BinaryDao
{

    @Override
    public BinaryId createBinary( final Binary binary, final Session session )
    {
        try
        {
            return new BinaryDaoHandlerCreate( session ).handle( binary );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Binary getBinary( final BinaryId binaryId, final Session session )
    {
        try
        {
            return new BinaryDaoHandlerGet( session ).handle( binaryId );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public boolean deleteBinary( final BinaryId binaryId, final Session session )
    {
        try
        {
            return new BinaryDaoHandlerDelete( session ).handle( binaryId );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }
}
