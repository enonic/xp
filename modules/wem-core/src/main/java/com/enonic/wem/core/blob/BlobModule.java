package com.enonic.wem.core.blob;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import com.enonic.wem.core.blob.binary.CreateBinaryHandler;
import com.enonic.wem.core.blob.binary.GetBinaryHandler;
import com.enonic.wem.core.blob.binary.dao.BlobDao;
import com.enonic.wem.core.blob.binary.dao.BlobDaoImpl;
import com.enonic.wem.core.command.CommandBinder;

public final class BlobModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( BlobDao.class ).to( BlobDaoImpl.class ).in( Scopes.SINGLETON );

        final CommandBinder commands = CommandBinder.from( binder() );

        commands.add( CreateBinaryHandler.class );
        commands.add( GetBinaryHandler.class );


    }
}
