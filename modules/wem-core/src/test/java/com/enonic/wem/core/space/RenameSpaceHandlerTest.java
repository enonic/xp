package com.enonic.wem.core.space;

import javax.jcr.Session;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.space.RenameSpace;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.exception.SpaceNotFoundException;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.space.dao.SpaceDao;

import static com.enonic.wem.api.space.Space.newSpace;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RenameSpaceHandlerTest extends AbstractCommandHandlerTest
{
    private RenameSpaceHandler handler;

    private SpaceDao spaceDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        spaceDao = Mockito.mock( SpaceDao.class );
        handler = new RenameSpaceHandler();
        handler.setContext( this.context );
        handler.setSpaceDao( spaceDao );
    }

    @Test
    public void renameSpace()
        throws Exception
    {
        // setup
        final DateTime time = DateTime.now();
        final Space space = newSpace().
            name( "mySpace" ).
            displayName( "My Space" ).
            modifiedTime( time ).
            createdTime( time ).
            rootContent( ContentId.from( "1fad493a-6a72-41a3-bac4-88aba3d83bcc" ) ).
            build();

        Mockito.when( spaceDao.getSpace( isA( SpaceName.class ), any( Session.class ) ) ).thenReturn( space );
        Mockito.when( spaceDao.renameSpace( isA( SpaceName.class ), any( String.class ), any( Session.class ) ) ).thenReturn( true);

        // exercise
        final SpaceName spaceToRename = SpaceName.from( "mySpace" );
        final RenameSpace command = Commands.space().rename().space( spaceToRename).newName( "newSpaceName" );

        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        assertEquals( true, command.getResult() );
        final SpaceName spaceNameExpected = SpaceName.from( "mySpace" );
        verify( spaceDao, times( 1 ) ).renameSpace( eq( spaceNameExpected ),eq( "newSpaceName" ), Mockito.any( Session.class ) );
    }

    @Test(expected = SpaceNotFoundException.class)
    public void renameMissingSpace()
        throws Exception
    {
        // setup
        Mockito.when( spaceDao.getSpace( isA( SpaceName.class ), any( Session.class ) ) ).thenReturn( null );

        // exercise
        final SpaceName spaceToRename = SpaceName.from( "mySpace" );
        final RenameSpace command = Commands.space().rename().space( spaceToRename ).newName( "newSpaceName" );

        this.handler.setCommand( command );
        this.handler.handle();
    }
}
