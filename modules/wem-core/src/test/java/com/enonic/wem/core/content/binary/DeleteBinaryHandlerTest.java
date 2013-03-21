package com.enonic.wem.core.content.binary;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.binary.DeleteBinary;
import com.enonic.wem.api.command.content.binary.DeleteBinaryResult;
import com.enonic.wem.api.content.binary.BinaryId;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.binary.dao.BinaryDao;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.only;

public class DeleteBinaryHandlerTest
    extends AbstractCommandHandlerTest
{
    private DeleteBinaryHandler handler;

    private BinaryDao binaryDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        binaryDao = Mockito.mock( BinaryDao.class );
        handler = new DeleteBinaryHandler();
        handler.setBinaryDao( binaryDao );
    }

    @Test
    public void deleteBinary()
        throws Exception
    {
        //setup
        Mockito.when( binaryDao.deleteBinary( Mockito.isA( BinaryId.class ), Mockito.any( Session.class ) ) ).
            thenReturn( true );

        // exercise
        final BinaryId binaryId = BinaryId.from( "edda7c84-d1ef-4d4b-b79e-71b696a716df" );
        final DeleteBinary command = Commands.binary().delete().binaryId( binaryId );
        this.handler.handle( this.context, command );

        // verify
        Mockito.verify( binaryDao, only() ).deleteBinary( isA( BinaryId.class ), any( Session.class ) );

        DeleteBinaryResult result = command.getResult();
        assertEquals( DeleteBinaryResult.SUCCESS, result );
    }

    @Test
    public void deleteBinaryNotFound()
        throws Exception
    {
        //setup
        Mockito.when( binaryDao.deleteBinary( Mockito.isA( BinaryId.class ), Mockito.any( Session.class ) ) ).
            thenReturn( false );

        // exercise
        final BinaryId binaryId = BinaryId.from( "edda7c84-d1ef-4d4b-b79e-71b696a716df" );
        final DeleteBinary command = Commands.binary().delete().binaryId( binaryId );
        this.handler.handle( this.context, command );

        // verify
        Mockito.verify( binaryDao, only() ).deleteBinary( isA( BinaryId.class ), any( Session.class ) );

        DeleteBinaryResult result = command.getResult();
        assertEquals( DeleteBinaryResult.NOT_FOUND, result );
    }
}
