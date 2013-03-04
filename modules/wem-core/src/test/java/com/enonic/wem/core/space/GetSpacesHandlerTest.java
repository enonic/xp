package com.enonic.wem.core.space;

import javax.jcr.Session;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.space.GetSpaces;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.space.SpaceNames;
import com.enonic.wem.api.space.Spaces;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.space.dao.SpaceDao;

import static com.enonic.wem.api.space.Space.newSpace;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class GetSpacesHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetSpacesHandler handler;

    private SpaceDao spaceDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        spaceDao = Mockito.mock( SpaceDao.class );
        handler = new GetSpacesHandler();
        handler.setSpaceDao( spaceDao );
    }

    @Test
    public void getSpaces()
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
        final Space space2 = newSpace().
            name( "myOtherSpace" ).
            displayName( "My Other Space" ).
            modifiedTime( time ).
            createdTime( time ).
            rootContent( ContentId.from( "2fad493a-6a72-41a3-bac4-88aba3d83bcd" ) ).
            build();

        Mockito.when( spaceDao.getSpace( eq( SpaceName.from( "mySpace" ) ), any( Session.class ) ) ).thenReturn( space );
        Mockito.when( spaceDao.getSpace( eq( SpaceName.from( "myOtherSpace" ) ), any( Session.class ) ) ).thenReturn( space2 );

        // exercise
        final SpaceNames names = SpaceNames.from( "mySpace", "myOtherSpace" );
        final GetSpaces command = Commands.space().get().names( names );
        this.handler.handle( this.context, command );

        // verify
        verify( spaceDao, times( 2 ) ).getSpace( Mockito.isA( SpaceName.class ), Mockito.any( Session.class ) );
        assertEquals( 2, command.getResult().getSize() );
        assertEquals( space, command.getResult().first() );
        assertEquals( space2, command.getResult().last() );
    }

    @Test
    public void getAllSpaces()
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
        final Space space2 = newSpace().
            name( "myOtherSpace" ).
            displayName( "My Other Space" ).
            modifiedTime( time ).
            createdTime( time ).
            rootContent( ContentId.from( "2fad493a-6a72-41a3-bac4-88aba3d83bcd" ) ).
            build();

        final Spaces spaces = Spaces.from( space, space2 );
        Mockito.when( spaceDao.getAllSpaces( any( Session.class ) ) ).thenReturn( spaces );

        // exercise
        final SpaceNames names = SpaceNames.from( "mySpace", "myOtherSpace" );
        final GetSpaces command = Commands.space().get().all();
        this.handler.handle( this.context, command );

        // verify
        verify( spaceDao, times( 1 ) ).getAllSpaces( Mockito.any( Session.class ) );
        assertEquals( 2, command.getResult().getSize() );
        assertEquals( space, command.getResult().first() );
        assertEquals( space2, command.getResult().last() );
    }
}
