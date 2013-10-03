package com.enonic.wem.admin.rpc.schema.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

import com.enonic.wem.admin.jsonrpc.JsonRpcHandler;
import com.enonic.wem.admin.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.schema.content.GetContentTypeTree;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.api.schema.content.form.inputtype.TextAreaConfig;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.api.support.tree.TreeNode;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.content.form.Input.newInput;
import static com.enonic.wem.api.schema.content.form.inputtype.InputTypes.TEXT_AREA;
import static com.enonic.wem.api.schema.content.form.inputtype.InputTypes.TEXT_LINE;
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
        final Input inputText1 = newInput().name( "inputText1" ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
            "Help text line 1" ).required( true ).build();
        final Input inputText2 =
            newInput().name( "inputText2" ).inputType( TEXT_LINE ).label( "Line Text 2" ).helpText( "Help text line 2" ).immutable(
                true ).build();
        final Input textArea1 = newInput().name( "textArea1" ).inputType( TEXT_AREA ).inputTypeConfig(
            TextAreaConfig.newTextAreaConfig().columns( 10 ).rows( 10 ).build() ).label( "Text Area" ).required( true ).helpText(
            "Help text area" ).required( true ).build();

        final ContentType contentType1 = newContentType().
            name( "root" ).
            displayName( "Some root content type" ).
            build();
        final ContentType contentType2 = newContentType().
            name( "my_type" ).
            displayName( "My content type" ).
            superType( QualifiedContentTypeName.from( "root" ) ).
            addFormItem( inputText1 ).
            addFormItem( inputText2 ).
            addFormItem( textArea1 ).
            build();
        final ContentType contentType3 = newContentType().
            name( "sub_type" ).
            displayName( "My sub-content type" ).
            superType( QualifiedContentTypeName.from( "my_type" ) ).
            build();

        final Tree<ContentType> contentTypeTree = new Tree<ContentType>( Lists.newArrayList( contentType1 ) );
        final TreeNode<ContentType> contentTypeNode2 = contentTypeTree.getRootNode( 0 ).addChild( contentType2 );
        contentTypeNode2.addChild( contentType3 );
        Mockito.when( client.execute( isA( GetContentTypeTree.class ) ) ).thenReturn( contentTypeTree );

        // verify
        final ObjectNode requestJson = objectNode();
        testSuccess( requestJson, "getContentTypeTree_result.json" );
    }

}
