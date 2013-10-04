package com.enonic.wem.core.content.binary;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.binary.CreateBinary;
import com.enonic.wem.api.content.binary.Binary;
import com.enonic.wem.api.content.binary.BinaryId;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.binary.dao.BinaryDao;

import static junit.framework.Assert.assertNotNull;

public class CreateBinaryHandlerTest
    extends AbstractCommandHandlerTest
{
    private CreateBinaryHandler handler;

    private BinaryDao binaryDao;


    @Before
    public void setUp()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );
        super.initialize();

        binaryDao = Mockito.mock( BinaryDao.class );

        handler = new CreateBinaryHandler();
        handler.setContext( this.context );
        handler.setBinaryDao( binaryDao );
    }

    @Test
    public void createContent()
        throws Exception
    {
        // setup
        Mockito.when( binaryDao.createBinary( Mockito.isA( Binary.class ), Mockito.any( Session.class ) ) ).
            thenReturn( BinaryId.from( "edda7c84-d1ef-4d4b-b79e-71b696a716df" ) );
        CreateBinary command = Commands.binary().create().binary( Binary.from( "some binary data".getBytes() ) );

        // exercise
        this.handler.handle( command );

        // verify
        Mockito.verify( binaryDao, Mockito.times( 1 ) ).createBinary( Mockito.isA( Binary.class ), Mockito.any( Session.class ) );

        BinaryId binaryId = command.getResult();
        assertNotNull( binaryId );
    }
}
