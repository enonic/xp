package com.enonic.wem.core.schema.mixin;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.entity.DeleteNodeByPath;
import com.enonic.wem.api.command.entity.DeleteNodeResult;
import com.enonic.wem.api.command.schema.mixin.DeleteMixin;
import com.enonic.wem.api.command.schema.mixin.DeleteMixinResult;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static org.junit.Assert.*;

public class DeleteMixinHandlerTest
    extends AbstractCommandHandlerTest
{
    private DeleteMixinHandler handler;

    //private NodeJcrDao nodeDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );
        super.initialize();

        // this.nodeDao = Mockito.mock( NodeJcrDao.class );

        handler = new DeleteMixinHandler();
        handler.setContext( this.context );
        //handler.setNodeJcrDao( nodeDao );
    }


    @Test
    public void delete_given_mixin_then_delete_node_by_path()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( DeleteNodeByPath.class ) ) ).thenReturn( DeleteNodeResult.SUCCESS );

        // exercise
        final MixinName name = MixinName.from( "mixin" );
        final DeleteMixin command = Commands.mixin().delete().name( name );

        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        Mockito.verify( client, Mockito.times( 1 ) ).execute( Mockito.isA( DeleteNodeByPath.class ) );

        final DeleteMixinResult result = command.getResult();
        assertEquals( DeleteMixinResult.SUCCESS, result );
    }

    @Test
    public void delete_given_no_node_found_then_not_found()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( DeleteNodeByPath.class ) ) ).thenReturn( DeleteNodeResult.NOT_FOUND );

        // exercise
        final MixinName name = MixinName.from( "mixin" );
        final DeleteMixin command = Commands.mixin().delete().name( name );

        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        Mockito.verify( client, Mockito.times( 1 ) ).execute( Mockito.isA( DeleteNodeByPath.class ) );

        final DeleteMixinResult result = command.getResult();
        assertEquals( DeleteMixinResult.NOT_FOUND, result );
    }

}
