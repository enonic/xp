package com.enonic.wem.admin.rpc.schema.mixin;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.jsonrpc.JsonRpcHandler;
import com.enonic.wem.admin.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.api.Client;

import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;

import static com.enonic.wem.api.command.Commands.mixin;
import static com.enonic.wem.api.schema.content.form.Input.newInput;
import static com.enonic.wem.api.schema.content.form.inputtype.InputTypes.TEXT_AREA;
import static com.enonic.wem.api.schema.content.form.inputtype.InputTypes.TEXT_LINE;

public class ListMixinsRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;


    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final ListMixinsRpcHandler handler = new ListMixinsRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void testListMixins()
        throws Exception
    {
        Input inputText1 = newInput().name( "input_text1" ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
            "Help text line 1" ).required( true ).build();
        Mixin mixin1 = Mixin.newMixin().
            name( "input_text1" ).
            addFormItem( inputText1 ).
            build();

        Input textArea1 = newInput().name( "text_area_1" ).inputType( TEXT_AREA ).label( "Text Area" ).required( true ).helpText(
            "Help text area" ).required( true ).build();
        Mixin mixin2 = Mixin.newMixin().
            name( "text_area_1" ).
            addFormItem( textArea1 ).
            build();

        Mixins mixins = Mixins.from( mixin1, mixin2 );
        Mockito.when( client.execute( mixin().get().all() ) ).thenReturn( mixins );

        testSuccess( "listMixins_result.json" );
    }
}
