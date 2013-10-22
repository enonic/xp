package com.enonic.wem.admin.rpc.schema.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.jsonrpc.JsonRpcHandler;
import com.enonic.wem.admin.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.api.schema.content.form.inputtype.TextAreaConfig;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.content.form.Input.newInput;
import static com.enonic.wem.api.schema.content.form.inputtype.InputTypes.TEXT_AREA;
import static com.enonic.wem.api.schema.content.form.inputtype.InputTypes.TEXT_LINE;

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
        final Input textArea1 = newInput().name( "textArea1" ).inputType( TEXT_AREA ).inputTypeConfig(
            TextAreaConfig.newTextAreaConfig().rows( 10 ).columns( 10 ).build() ).label( "Text Area" ).required( true ).helpText(
            "Help text area" ).required( true ).build();

        final ContentType contentType1 = newContentType().
            name( "my_type" ).
            superType( QualifiedContentTypeName.unstructured() ).
            addFormItem( inputText1 ).
            addFormItem( inputText2 ).
            addFormItem( textArea1 ).
            build();

        final Input inputTextCty2 =
            newInput().name( "inputText_1" ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
                "Help text line 1" ).required( true ).build();
        final ContentType contentType2 = newContentType().
            name( "the_content_type" ).
            superType( QualifiedContentTypeName.unstructured() ).
            addFormItem( inputTextCty2 ).
            build();
        final ContentTypes contentTypes = ContentTypes.from( contentType1, contentType2 );
        Mockito.when( client.execute( Commands.contentType().get().all() ) ).thenReturn( contentTypes );

        testSuccess( "listContentTypes_result.json" );
    }
}
