package com.enonic.wem.web.rest.rpc.content.type;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.component.Input;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

import static com.enonic.wem.api.content.type.component.Input.newInput;
import static com.enonic.wem.api.content.type.component.inputtype.InputTypes.TEXT_AREA;
import static com.enonic.wem.api.content.type.component.inputtype.InputTypes.TEXT_LINE;

public class ListContentTypesRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;


    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final ListContentTypesRpcHandler handler = new ListContentTypesRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void testListContentTypes()
        throws Exception
    {
        final ContentType contentType1 = new ContentType();
        contentType1.setModule( Module.newModule().name( "myModule" ).build() );
        contentType1.setName( "myCtype" );
        final Input inputText1 = newInput().name( "inputText1" ).type( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
            "Help text line 1" ).required( true ).build();
        final Input inputText2 =
            newInput().name( "inputText2" ).type( TEXT_LINE ).label( "Line Text 2" ).helpText( "Help text line 2" ).immutable(
                true ).build();
        final Input textArea1 =
            newInput().name( "textArea1" ).type( TEXT_AREA ).label( "Text Area" ).required( true ).helpText( "Help text area" ).required(
                true ).build();
        contentType1.addComponent( inputText1 );
        contentType1.addComponent( inputText2 );
        contentType1.addComponent( textArea1 );

        final ContentType contentType2 = new ContentType();
        contentType2.setModule( Module.newModule().name( "otherModule" ).build() );
        contentType2.setName( "theContentType" );
        final Input inputTextCty2 = newInput().name( "inputText_1" ).type( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
            "Help text line 1" ).required( true ).build();
        contentType2.addComponent( inputTextCty2 );

        final ContentTypes contentTypes = ContentTypes.from( contentType1, contentType2 );
        Mockito.when( client.execute( Commands.contentType().get().all() ) ).thenReturn( contentTypes );

        testSuccess( "listContentTypes_result.json" );
    }
}
