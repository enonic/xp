package com.enonic.wem.core.schema.mixin;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.mixin.DeleteMixin;
import com.enonic.wem.api.command.schema.mixin.DeleteMixinResult;
import com.enonic.wem.api.entity.NoNodeAtPathFound;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.item.dao.NodeJcrDao;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;

public class DeleteMixinHandlerTest
    extends AbstractCommandHandlerTest
{
    private DeleteMixinHandler handler;

    private NodeJcrDao nodeDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );
        super.initialize();

        this.nodeDao = Mockito.mock( NodeJcrDao.class );

        handler = new DeleteMixinHandler();
        handler.setContext( this.context );
        handler.setNodeJcrDao( nodeDao );
    }


    @Test
    public void deleteSingleMixin()
        throws Exception
    {
        // exercise
        final QualifiedMixinName name = QualifiedMixinName.from( "my:mixin" );
        final DeleteMixin command = Commands.mixin().delete().name( name );

        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        Mockito.verify( nodeDao, only() ).deleteNodeByPath( isA( NodePath.class ) );

        final DeleteMixinResult result = command.getResult();
        assertEquals( DeleteMixinResult.SUCCESS, result );
    }

    @Test
    public void deleteMissingMixin()
        throws Exception
    {
        // exercise
        final NodePath notFoundName = new NodePath( "/mixins/not_found_mixin" );

        Mockito.doThrow( new NoNodeAtPathFound( notFoundName ) ).
            when( nodeDao ).deleteNodeByPath( eq( notFoundName ) );

        final DeleteMixin command = Commands.mixin().delete().name( QualifiedMixinName.from( "not_found_mixin" ) );

        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        Mockito.verify( nodeDao, times( 1 ) ).deleteNodeByPath( isA( NodePath.class ) );

        DeleteMixinResult result = command.getResult();
        assertEquals( DeleteMixinResult.NOT_FOUND, result );
    }
}
