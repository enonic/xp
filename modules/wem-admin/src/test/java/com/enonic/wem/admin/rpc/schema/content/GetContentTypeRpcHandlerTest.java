package com.enonic.wem.admin.rpc.schema.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.jsonrpc.JsonRpcHandler;
import com.enonic.wem.admin.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.TextAreaConfig;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypes;

import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.form.inputtype.InputTypes.TEXT_AREA;
import static com.enonic.wem.api.form.inputtype.InputTypes.TEXT_LINE;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;

public class GetContentTypeRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final GetContentTypeRpcHandler handler = new GetContentTypeRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void testRequestGetContentTypeJson_existing()
        throws Exception
    {
        final Input inputText1 = newInput().name( "inputText1" ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
            "Help text line 1" ).required( true ).build();
        final Input inputText2 =
            newInput().name( "inputText2" ).inputType( TEXT_LINE ).label( "Line Text 2" ).helpText( "Help text line 2" ).immutable(
                true ).build();
        final Input textArea1 = newInput().name( "textArea1" ).inputType( TEXT_AREA ).inputTypeConfig(
            TextAreaConfig.newTextAreaConfig().columns( 10 ).rows( 10 ).build() ).label( "Text Area" ).required( true ).helpText(
            "Help text area" ).required( true ).build();

        final ContentType contentType = newContentType().
            name( "my_type" ).
            superType( ContentTypeName.unstructured() ).
            addFormItem( inputText1 ).
            addFormItem( inputText2 ).
            addFormItem( textArea1 ).
            build();

        final ContentTypes contentTypes = ContentTypes.from( contentType );
        final ContentTypeNames names = ContentTypeNames.from( ContentTypeName.from( "my_type" ) );
        Mockito.when( client.execute( Commands.contentType().get().qualifiedNames( names ) ) ).thenReturn( contentTypes );

        testSuccess( "getContentTypeJson_param.json", "getContentTypeJson_result.json" );
    }

    @Test
    public void testRequestGetContentTypeXml_existing()
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

        final ContentType contentType = newContentType().
            name( "my_type" ).
            superType( ContentTypeName.unstructured() ).
            addFormItem( inputText1 ).
            addFormItem( inputText2 ).
            addFormItem( textArea1 ).
            build();

        final ContentTypes contentTypes = ContentTypes.from( contentType );
        final ContentTypeNames names = ContentTypeNames.from( ContentTypeName.from( "my_type" ) );
        Mockito.when( client.execute( Commands.contentType().get().qualifiedNames( names ) ) ).thenReturn( contentTypes );

        testSuccess( "getContentTypeXml_param.json", "getContentTypeXml_result.json" );
    }

    @Test
    public void testRequestGetContentTypeJson_not_found()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.any( GetContentTypes.class ) ) ).thenReturn( ContentTypes.empty() );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", false );
        resultJson.put( "error", "ContentTypes [my_type] not found" );
        testSuccess( "getContentTypeJson_param.json", resultJson );
    }

    @Test
    public void testRequestGetContentTypeXml_not_found()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.any( GetContentTypes.class ) ) ).thenReturn( ContentTypes.empty() );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", false );
        resultJson.put( "error", "ContentTypes [my_type] not found" );
        testSuccess( "getContentTypeXml_param.json", resultJson );
    }
}