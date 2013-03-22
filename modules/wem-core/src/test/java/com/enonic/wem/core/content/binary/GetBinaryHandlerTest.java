package com.enonic.wem.core.content.binary;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.binary.GetBinary;
import com.enonic.wem.api.content.binary.Binary;
import com.enonic.wem.api.content.binary.BinaryId;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.binary.dao.BinaryDao;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class GetBinaryHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetBinaryHandler handler;

    private BinaryDao binaryDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        binaryDao = Mockito.mock( BinaryDao.class );
        handler = new GetBinaryHandler();
        handler.setBinaryDao( binaryDao );
    }

    @Test
    public void getBinary()
        throws Exception
    {
        // setup
        final Binary binary = Binary.from( "some binary data".getBytes() );
        Mockito.when( binaryDao.getBinary( isA( BinaryId.class ), any( Session.class ) ) ).thenReturn( binary );

        // exercise
        final BinaryId binaryId = BinaryId.from( "edda7c84-d1ef-4d4b-b79e-71b696a716df" );
        final GetBinary command = Commands.binary().get().binaryId( binaryId );
        this.handler.handle( this.context, command );

        // verify
        verify( binaryDao, atLeastOnce() ).getBinary( Mockito.eq( binaryId ), Mockito.any( Session.class ) );
        assertEquals( binary, command.getResult() );
    }

}
