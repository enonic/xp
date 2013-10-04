package com.enonic.wem.core.space;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.space.DeleteSpace;
import com.enonic.wem.api.exception.SpaceNotFoundException;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.space.dao.SpaceDao;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;

public class DeleteSpaceHandlerTest
    extends AbstractCommandHandlerTest
{
    private DeleteSpaceHandler handler;

    private SpaceDao spaceDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        spaceDao = Mockito.mock( SpaceDao.class );
        handler = new DeleteSpaceHandler();
        handler.setContext( this.context );
        handler.setSpaceDao( spaceDao );
    }

    @Test
    public void deleteSpace()
        throws Exception
    {
        // exercise
        final DeleteSpace command = Commands.space().delete().name( SpaceName.from( "mySpace" ) );
        this.handler.handle( command );

        // verify
        Mockito.verify( spaceDao, only() ).deleteSpace( isA( SpaceName.class ), any( Session.class ) );

        boolean deleted = command.getResult();
        assertTrue( deleted );
    }

    @Test
    public void deleteSpace_not_found()
        throws Exception
    {
        // setup
        doThrow( new SpaceNotFoundException( SpaceName.from( "mySpace" ) ) ).
            when( spaceDao ).deleteSpace( isA( SpaceName.class ), any( Session.class ) );

        // exercise
        final DeleteSpace command = Commands.space().delete().name( SpaceName.from( "mySpace" ) );
        this.handler.handle( command );

        // verify
        Mockito.verify( spaceDao, only() ).deleteSpace( isA( SpaceName.class ), any( Session.class ) );

        boolean deleted = command.getResult();
        assertFalse( deleted );
    }

}
