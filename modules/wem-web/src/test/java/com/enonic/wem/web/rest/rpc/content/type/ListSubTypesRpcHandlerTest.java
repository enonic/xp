package com.enonic.wem.web.rest.rpc.content.type;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.type.SubTypes;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.content.type.form.InputSubType;
import com.enonic.wem.api.content.type.form.SubType;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

import static com.enonic.wem.api.command.Commands.subType;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static com.enonic.wem.api.content.type.form.inputtype.InputTypes.TEXT_AREA;
import static com.enonic.wem.api.content.type.form.inputtype.InputTypes.TEXT_LINE;

public class ListSubTypesRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;


    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final ListSubTypesRpcHandler handler = new ListSubTypesRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void testListSubTypes()
        throws Exception
    {
        final Input inputText1 = newInput().name( "inputText1" ).type( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
            "Help text line 1" ).required( true ).build();
        final SubType subType1 = InputSubType.newInputSubType().
            module( ModuleName.from( "myModule" ) ).
            input( inputText1 ).
            build();

        final Input textArea1 =
            newInput().name( "textArea1" ).type( TEXT_AREA ).label( "Text Area" ).required( true ).helpText( "Help text area" ).required(
                true ).build();
        final SubType subType2 = InputSubType.newInputSubType().
            module( ModuleName.from( "otherModule" ) ).
            input( textArea1 ).
            build();

        final SubTypes subTypes = SubTypes.from( subType1, subType2 );
        Mockito.when( client.execute( subType().get().all() ) ).thenReturn( subTypes );

        testSuccess( "listSubTypes_result.json" );
    }
}
