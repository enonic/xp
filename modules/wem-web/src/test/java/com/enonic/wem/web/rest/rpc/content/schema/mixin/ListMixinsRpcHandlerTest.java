package com.enonic.wem.web.rest.rpc.content.schema.mixin;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.schema.content.form.Input;
import com.enonic.wem.api.content.schema.mixin.Mixin;
import com.enonic.wem.api.content.schema.mixin.Mixins;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

import static com.enonic.wem.api.command.Commands.mixin;
import static com.enonic.wem.api.content.schema.content.form.Input.newInput;
import static com.enonic.wem.api.content.schema.content.form.inputtype.InputTypes.TEXT_AREA;
import static com.enonic.wem.api.content.schema.content.form.inputtype.InputTypes.TEXT_LINE;

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
        Input inputText1 = newInput().name( "inputText1" ).type( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
            "Help text line 1" ).required( true ).build();
        Mixin mixin1 = Mixin.newMixin().
            module( ModuleName.from( "myModule" ) ).
            formItem( inputText1 ).
            build();

        Input textArea1 =
            newInput().name( "textArea1" ).type( TEXT_AREA ).label( "Text Area" ).required( true ).helpText( "Help text area" ).required(
                true ).build();
        Mixin mixin2 = Mixin.newMixin().
            module( ModuleName.from( "otherModule" ) ).
            formItem( textArea1 ).
            build();

        Mixins mixins = Mixins.from( mixin1, mixin2 );
        Mockito.when( client.execute( mixin().get().all() ) ).thenReturn( mixins );

        testSuccess( "listMixins_result.json" );
    }
}
