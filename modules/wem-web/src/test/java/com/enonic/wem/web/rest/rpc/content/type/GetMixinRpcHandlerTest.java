package com.enonic.wem.web.rest.rpc.content.type;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.type.GetMixins;
import com.enonic.wem.api.content.QualifiedMixinNames;
import com.enonic.wem.api.content.type.Mixins;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.content.type.form.InputMixin;
import com.enonic.wem.api.content.type.form.Mixin;
import com.enonic.wem.api.content.type.form.QualifiedMixinName;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

import static com.enonic.wem.api.command.Commands.mixin;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static com.enonic.wem.api.content.type.form.inputtype.InputTypes.TEXT_LINE;

public class GetMixinRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final GetMixinRpcHandler handler = new GetMixinRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void testRequestGetMixin_existing()
        throws Exception
    {
        final Input inputText1 = newInput().name( "inputText1" ).type( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
            "Help text line 1" ).required( true ).build();
        final Mixin mixin = InputMixin.newInputMixin().
            module( ModuleName.from( "myModule" ) ).
            input( inputText1 ).
            build();

        final Mixins mixins = Mixins.from( mixin );
        final QualifiedMixinNames names = QualifiedMixinNames.from( new QualifiedMixinName( "myModule:mymixin" ) );
        Mockito.when( client.execute( mixin().get().names( names ) ) ).thenReturn( mixins );

        testSuccess( "getMixin_param.json", "getMixin_result.json" );
    }

    @Test
    public void testRequestGetMixin_not_found()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.any( GetMixins.class ) ) ).thenReturn( Mixins.empty() );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", false );
        resultJson.put( "error", "Mixin [myModule:mymixin] was not found" );
        testSuccess( "getMixin_param.json", resultJson );
    }
}