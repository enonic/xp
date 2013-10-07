package com.enonic.wem.core.space;

import javax.jcr.Session;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.space.UpdateSpace;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.exception.SpaceNotFoundException;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.space.editor.SpaceEditor;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.space.dao.SpaceDao;

import static com.enonic.wem.api.space.Space.newSpace;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class UpdateSpaceHandlerTest
    extends AbstractCommandHandlerTest
{
    private UpdateSpaceHandler handler;

    private SpaceDao spaceDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        spaceDao = Mockito.mock( SpaceDao.class );
        handler = new UpdateSpaceHandler();
        handler.setContext( this.context );
        handler.setSpaceDao( spaceDao );
    }

    @Test
    public void updateSpace()
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

        // exercise
        final SpaceName spaceToEdit = SpaceName.from( "mySpace" );
        final UpdateSpace command = Commands.space().update().name( spaceToEdit ).editor( new SpaceEditor()
        {
            @Override
            public Space edit( final Space space )
                throws Exception
            {
                return newSpace( space ).
                    displayName( space.getDisplayName().toUpperCase() ).
                    build();
            }
        } );
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        assertEquals( true, command.getResult() );
        final Space space1EditExpected = newSpace().
            name( "mySpace" ).
            displayName( "MY SPACE" ).
            modifiedTime( time ).
            createdTime( time ).
            rootContent( ContentId.from( "1fad493a-6a72-41a3-bac4-88aba3d83bcc" ) ).
            build();
        verify( spaceDao, times( 1 ) ).updateSpace( eq( space1EditExpected ), Mockito.any( Session.class ) );
    }

    @Test(expected = SpaceNotFoundException.class)
    public void updateMissingSpace()
        throws Exception
    {
        // setup
        Mockito.when( spaceDao.getSpace( isA( SpaceName.class ), any( Session.class ) ) ).thenReturn( null );

        // exercise
        final SpaceName spaceToEdit = SpaceName.from( "mySpace" );
        final UpdateSpace command = Commands.space().update().name( spaceToEdit ).editor( new SpaceEditor()
        {
            @Override
            public Space edit( final Space space )
                throws Exception
            {
                return newSpace( space ).
                    displayName( space.getDisplayName().toUpperCase() ).
                    build();
            }
        } );
        this.handler.setCommand( command );
        this.handler.handle();
    }

}
