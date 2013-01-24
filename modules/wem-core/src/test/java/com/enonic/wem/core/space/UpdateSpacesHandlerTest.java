package com.enonic.wem.core.space;

import javax.jcr.Session;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.space.UpdateSpaces;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.space.SpaceNames;
import com.enonic.wem.api.space.editor.SpaceEditor;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.dao.ContentIdFactory;
import com.enonic.wem.core.content.dao.SpaceDao;

import static com.enonic.wem.api.space.Space.newSpace;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class UpdateSpacesHandlerTest
    extends AbstractCommandHandlerTest
{
    private UpdateSpacesHandler handler;

    private SpaceDao spaceDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        spaceDao = Mockito.mock( SpaceDao.class );
        handler = new UpdateSpacesHandler();
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
            rootContent( ContentIdFactory.from( "1fad493a-6a72-41a3-bac4-88aba3d83bcc" ) ).
            build();
        final Space space2 = newSpace().
            name( "myOtherSpace" ).
            displayName( "My Other Space" ).
            modifiedTime( time ).
            createdTime( time ).
            rootContent( ContentIdFactory.from( "2fad493a-6a72-41a3-bac4-88aba3d83bcd" ) ).
            build();

        Mockito.when( spaceDao.getSpace( isA( SpaceName.class ), any( Session.class ) ) ).thenReturn( space ).thenReturn( space2 );

        // exercise
        final SpaceNames spacesToEdit = SpaceNames.from( "mySpace", "myOtherSpace" );
        final UpdateSpaces command = Commands.space().update().names( spacesToEdit ).editor( new SpaceEditor()
        {
            @Override
            public Space edit( final Space space )
                throws Exception
            {
                return Space.newSpace( space ).displayName( space.getDisplayName().toUpperCase() ).build();
            }
        } );
        this.handler.handle( this.context, command );

        // verify
        assertEquals( 2, command.getResult().intValue() );
        final Space space1EditExpected = newSpace().
            name( "mySpace" ).
            displayName( "MY SPACE" ).
            modifiedTime( time ).
            createdTime( time ).
            rootContent( ContentIdFactory.from( "1fad493a-6a72-41a3-bac4-88aba3d83bcc" ) ).
            build();
        verify( spaceDao, times( 1 ) ).updateSpace( eq( space1EditExpected ), Mockito.any( Session.class ) );
        final Space space2EditExpected = newSpace().
            name( "myOtherSpace" ).
            displayName( "MY OTHER SPACE" ).
            modifiedTime( time ).
            createdTime( time ).
            rootContent( ContentIdFactory.from( "2fad493a-6a72-41a3-bac4-88aba3d83bcd" ) ).
            build();
        verify( spaceDao, times( 1 ) ).updateSpace( eq( space2EditExpected ), Mockito.any( Session.class ) );
    }

}
