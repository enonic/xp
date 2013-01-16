package com.enonic.wem.web.rest.rpc.content.type;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.type.GetContentTypeTree;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypeTreeNode;
import com.enonic.wem.api.content.type.ContentTypeTree;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

import static com.enonic.wem.api.content.type.ContentType.newContentType;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static com.enonic.wem.api.content.type.form.inputtype.InputTypes.TEXT_AREA;
import static com.enonic.wem.api.content.type.form.inputtype.InputTypes.TEXT_LINE;
import static org.mockito.Matchers.isA;

public class GetContentTypeTreeRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final GetContentTypeTreeRpcHandler handler = new GetContentTypeTreeRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void testRequestGetContentTypeTree()
        throws Exception
    {
        // setup
        mockCurrentContextHttpRequest();
        final Input inputText1 = newInput().name( "inputText1" ).type( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
            "Help text line 1" ).required( true ).build();
        final Input inputText2 =
            newInput().name( "inputText2" ).type( TEXT_LINE ).label( "Line Text 2" ).helpText( "Help text line 2" ).immutable(
                true ).build();
        final Input textArea1 =
            newInput().name( "textArea1" ).type( TEXT_AREA ).label( "Text Area" ).required( true ).helpText( "Help text area" ).required(
                true ).build();

        final ContentType contentType1 = newContentType().
            name( "Root" ).
            module( Module.SYSTEM.getName() ).
            displayName( "Some root content type" ).
            build();
        final ContentType contentType2 = newContentType().
            name( "myType" ).
            module( ModuleName.from( "myModule" ) ).
            displayName( "My content type" ).
            superType( new QualifiedContentTypeName( "System:Root" ) ).
            addFormItem( inputText1 ).
            addFormItem( inputText2 ).
            addFormItem( textArea1 ).
            build();
        final ContentType contentType3 = newContentType().
            name( "subType" ).
            module( ModuleName.from( "myModule" ) ).
            displayName( "My sub-content type" ).
            superType( new QualifiedContentTypeName( "myModule:myType" ) ).
            build();

        final ContentTypeTree contentTypesTree = new ContentTypeTree( Lists.newArrayList( contentType1 ) );
        final ContentTypeTreeNode contentTypeNode2 = contentTypesTree.getFirstChild().add( contentType2 );
        final ContentTypeTreeNode contentTypeNode3 = contentTypeNode2.add( contentType3 );
        Mockito.when( client.execute( isA( GetContentTypeTree.class ) ) ).thenReturn( contentTypesTree );

        // verify
        final ObjectNode requestJson = objectNode();
        testSuccess( requestJson, "getContentTypeTree_result.json" );
    }

}
