package com.enonic.wem.core.space;

import javax.jcr.Session;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.space.CreateSpace;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.dao.ContentIdFactory;
import com.enonic.wem.core.content.dao.SpaceDao;

import static com.enonic.wem.api.space.Space.newSpace;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CreateSpaceHandlerTest
    extends AbstractCommandHandlerTest
{
    private CreateSpaceHandler handler;

    private SpaceDao spaceDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        spaceDao = Mockito.mock( SpaceDao.class );
        handler = new CreateSpaceHandler();
        handler.setSpaceDao( spaceDao );
    }

    @Test
    public void createSpace()
        throws Exception
    {
        // setup
        final DateTime time = DateTime.now();
        final byte[] icon = "imagedata".getBytes();
        final Space space = newSpace().
            name( "mySpace" ).
            displayName( "My Space" ).
            modifiedTime( time ).
            createdTime( time ).
            rootContent( ContentIdFactory.from( "1fad493a-6a72-41a3-bac4-88aba3d83bcc" ) ).
            icon( icon ).
            build();

        Mockito.when( spaceDao.createSpace( isA( Space.class ), any( Session.class ) ) ).thenReturn( space );

        // exercise
        final CreateSpace command = Commands.space().create().displayName( "My Space" ).name( "mySpace" ).icon( icon );
        this.handler.handle( this.context, command );

        // verify
        verify( spaceDao, times( 1 ) ).createSpace( isA( Space.class ), Mockito.any( Session.class ) );
        final Space spaceResult = command.getResult();
        assertNotNull( spaceResult );
        assertEquals( "My Space", spaceResult.getDisplayName() );
        assertEquals( "mySpace", spaceResult.getName().name() );
        assertEquals( time, spaceResult.getModifiedTime() );
        assertEquals( time, spaceResult.getCreatedTime() );
        assertArrayEquals( icon, spaceResult.getIcon() );
        assertEquals( ContentIdFactory.from( "1fad493a-6a72-41a3-bac4-88aba3d83bcc" ), spaceResult.getRootContent() );
    }

}
