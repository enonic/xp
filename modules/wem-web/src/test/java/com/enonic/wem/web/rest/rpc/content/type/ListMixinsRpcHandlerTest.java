package com.enonic.wem.web.rest.rpc.content.type;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.type.Mixins;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.content.type.form.InputMixin;
import com.enonic.wem.api.content.type.form.Mixin;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

import static com.enonic.wem.api.command.Commands.mixin;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static com.enonic.wem.api.content.type.form.inputtype.InputTypes.TEXT_AREA;
import static com.enonic.wem.api.content.type.form.inputtype.InputTypes.TEXT_LINE;

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
        final Input inputText1 = newInput().name( "inputText1" ).type( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
            "Help text line 1" ).required( true ).build();
        final Mixin mixin1 = InputMixin.newInputMixin().
            module( ModuleName.from( "myModule" ) ).
            input( inputText1 ).
            build();

        final Input textArea1 =
            newInput().name( "textArea1" ).type( TEXT_AREA ).label( "Text Area" ).required( true ).helpText( "Help text area" ).required(
                true ).build();
        final Mixin mixin2 = InputMixin.newInputMixin().
            module( ModuleName.from( "otherModule" ) ).
            input( textArea1 ).
            build();

        final Mixins mixins = Mixins.from( mixin1, mixin2 );
        Mockito.when( client.execute( mixin().get().all() ) ).thenReturn( mixins );

        testSuccess( "listMixins_result.json" );
    }
}
