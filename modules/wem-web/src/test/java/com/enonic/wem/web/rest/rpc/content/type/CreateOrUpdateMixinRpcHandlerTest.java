package com.enonic.wem.web.rest.rpc.content.type;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.type.CreateMixin;
import com.enonic.wem.api.command.content.type.GetMixins;
import com.enonic.wem.api.command.content.type.UpdateMixins;
import com.enonic.wem.api.content.type.Mixins;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.content.type.form.Mixin;
import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

import static com.enonic.wem.api.content.type.form.Input.newInput;
import static com.enonic.wem.api.content.type.form.Mixin.newMixin;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CreateOrUpdateMixinRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final CreateOrUpdateMixinRpcHandler handler = new CreateOrUpdateMixinRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void testCreateMixin()
        throws Exception
    {
        Mockito.when( client.execute( isA( GetMixins.class ) ) ).thenReturn( Mixins.empty() );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", true );
        resultJson.put( "updated", false );
        testSuccess( "createOrUpdateMixin_param.json", resultJson );

        verify( client, times( 1 ) ).execute( isA( CreateMixin.class ) );
    }

    @Test
    public void testUpdateMixin()
        throws Exception
    {
        final Input input = newInput().name( "someInput" ).type( InputTypes.TEXT_LINE ).build();
        final Mixin existingMixin = newMixin().formItem( input ).module( Module.SYSTEM.getName() ).build();
        final Mixins mixins = Mixins.from( existingMixin );
        Mockito.when( client.execute( isA( GetMixins.class ) ) ).thenReturn( mixins );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", false );
        resultJson.put( "updated", true );
        testSuccess( "createOrUpdateMixin_param.json", resultJson );

        verify( client, times( 1 ) ).execute( isA( UpdateMixins.class ) );
    }

}