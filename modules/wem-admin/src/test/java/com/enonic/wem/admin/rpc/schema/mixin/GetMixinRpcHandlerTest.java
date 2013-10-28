package com.enonic.wem.admin.rpc.schema.mixin;

import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.jsonrpc.JsonRpcHandler;
import com.enonic.wem.admin.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.schema.mixin.GetMixin;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;

import static com.enonic.wem.api.command.Commands.mixin;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.form.inputtype.InputTypes.TEXT_LINE;

public class GetMixinRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        GetMixinRpcHandler handler = new GetMixinRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void testRequestGetMixin_existing_asJson()
        throws Exception
    {
        Input inputText1 = newInput().name( "input_text1" ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
            "Help text line 1" ).required( true ).build();
        Mixin mixin = Mixin.newMixin().
            name( "input_text1" ).
            addFormItem( inputText1 ).
            build();

        Mockito.when( client.execute( mixin().get().byName( MixinName.from( "mymixin" ) ) ) ).thenReturn( mixin );

        testSuccess( "getMixin_asJson_param.json", "getMixin_asJson_result.json" );
    }

    @Test
    public void testRequestGetMixin_existing_asXml()
        throws Exception
    {
        Input inputText1 = newInput().name( "input_text1" ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
            "Help text line 1" ).required( true ).build();
        Mixin mixin = Mixin.newMixin().
            name( "input_text1" ).
            addFormItem( inputText1 ).
            build();

        Mockito.when( client.execute( mixin().get().byName( MixinName.from( "mymixin" ) ) ) ).thenReturn( mixin );

        testSuccess( "getMixin_asXml_param.json", "getMixin_asXml_result.json" );
    }

    @Test
    public void testRequestGetMixin_not_found()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.any( GetMixin.class ) ) ).thenReturn( null );

        ObjectNode resultJson = objectNode();
        resultJson.put( "success", false );
        resultJson.put( "error", "Mixin [mymixin] was not found" );
        testSuccess( "getMixin_asJson_param.json", resultJson );
    }
}