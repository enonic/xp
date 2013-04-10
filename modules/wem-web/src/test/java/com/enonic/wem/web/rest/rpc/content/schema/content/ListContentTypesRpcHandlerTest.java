package com.enonic.wem.web.rest.rpc.content.schema.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.ContentTypes;
import com.enonic.wem.api.content.schema.content.form.Input;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

import static com.enonic.wem.api.content.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.content.schema.content.form.Input.newInput;
import static com.enonic.wem.api.content.schema.content.form.inputtype.InputTypes.TEXT_AREA;
import static com.enonic.wem.api.content.schema.content.form.inputtype.InputTypes.TEXT_LINE;

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
        final Input inputText1 = newInput().name( "inputText1" ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
            "Help text line 1" ).required( true ).build();
        final Input inputText2 =
            newInput().name( "inputText2" ).inputType( TEXT_LINE ).label( "Line Text 2" ).helpText( "Help text line 2" ).immutable(
                true ).build();
        final Input textArea1 = newInput().name( "textArea1" ).inputType( TEXT_AREA ).label( "Text Area" ).required( true ).helpText(
            "Help text area" ).required( true ).build();

        final ContentType contentType1 = newContentType().
            module( ModuleName.from( "mymodule" ) ).
            name( "myCtype" ).
            addFormItem( inputText1 ).
            addFormItem( inputText2 ).
            addFormItem( textArea1 ).
            build();

        final Input inputTextCty2 =
            newInput().name( "inputText_1" ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
                "Help text line 1" ).required( true ).build();
        final ContentType contentType2 = newContentType().
            module( ModuleName.from( "othermodule" ) ).
            name( "theContentType" ).
            addFormItem( inputTextCty2 ).
            build();
        final ContentTypes contentTypes = ContentTypes.from( contentType1, contentType2 );
        Mockito.when( client.execute( Commands.contentType().get().all() ) ).thenReturn( contentTypes );

        testSuccess( "listContentTypes_result.json" );
    }
}
