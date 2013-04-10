package com.enonic.wem.web.rest.rpc.content.schema.content;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.schema.content.GetContentTypes;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.ContentTypes;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.content.schema.content.form.Input;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

import static com.enonic.wem.api.content.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.content.schema.content.form.Input.newInput;
import static com.enonic.wem.api.content.schema.content.form.inputtype.InputTypes.TEXT_AREA;
import static com.enonic.wem.api.content.schema.content.form.inputtype.InputTypes.TEXT_LINE;

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
        final Input textArea1 = newInput().name( "textArea1" ).inputType( TEXT_AREA ).label( "Text Area" ).required( true ).helpText(
            "Help text area" ).required( true ).build();

        final ContentType contentType = newContentType().
            module( ModuleName.from( "mymodule" ) ).
            name( "myCtype" ).
            addFormItem( inputText1 ).
            addFormItem( inputText2 ).
            addFormItem( textArea1 ).
            build();

        final ContentTypes contentTypes = ContentTypes.from( contentType );
        final QualifiedContentTypeNames names = QualifiedContentTypeNames.from( new QualifiedContentTypeName( "mymodule:myCtype" ) );
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
        final Input textArea1 = newInput().name( "textArea1" ).inputType( TEXT_AREA ).label( "Text Area" ).required( true ).helpText(
            "Help text area" ).required( true ).build();

        final ContentType contentType = newContentType().
            module( ModuleName.from( "mymodule" ) ).
            name( "myCtype" ).
            addFormItem( inputText1 ).
            addFormItem( inputText2 ).
            addFormItem( textArea1 ).
            build();

        final ContentTypes contentTypes = ContentTypes.from( contentType );
        final QualifiedContentTypeNames names = QualifiedContentTypeNames.from( new QualifiedContentTypeName( "mymodule:myCtype" ) );
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
        resultJson.put( "error", "ContentType [mymodule:myCtype] was not found" );
        testSuccess( "getContentTypeJson_param.json", resultJson );
    }

    @Test
    public void testRequestGetContentTypeXml_not_found()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.any( GetContentTypes.class ) ) ).thenReturn( ContentTypes.empty() );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", false );
        resultJson.put( "error", "ContentType [mymodule:myCtype] was not found" );
        testSuccess( "getContentTypeXml_param.json", resultJson );
    }
}