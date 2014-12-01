package com.enonic.wem.core.content;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentName;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.content.thumb.Thumbnail;
import com.enonic.wem.api.data.PropertyPath;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.index.IndexConfig;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.UpdateNodeParams;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.security.acl.Permission;
import com.enonic.wem.core.content.serializer.ThumbnailAttachmentSerializer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

public class ContentNodeTranslatorTest
{
    private static final String CONTENT_DATA_PREFIX = ContentFieldNames.CONTENT_DATA_SET;

    private ContentNodeTranslator translator;

    @Before
    public void before()
    {
        final ContentTypeService contentTypeService = Mockito.mock( ContentTypeService.class );
        final BlobService blobService = Mockito.mock( BlobService.class );

        final ContentType contentType =
            ContentType.newContentType().superType( ContentTypeName.structured() ).name( "mymodule:my-content-type" ).build();
        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        translator = new ContentNodeTranslator();
        translator.setBlobService( blobService );
        translator.setContentTypeService( contentTypeService );
    }

    @Test
    public void toCreateNode_contentData_to_rootdataset()
        throws Exception
    {
        final PropertyTree contentData = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        contentData.setString( "test", "testValue" );

        final CreateContentParams mycontent = new CreateContentParams().
            name( "mycontent" ).
            parent( ContentPath.ROOT ).
            contentType( ContentTypeName.from( "mymodule:my-content-type" ) ).
            contentData( contentData );

        final CreateNodeParams createNode = translator.toCreateNode( mycontent );

        assertEquals( "testValue", createNode.getData().getString( CONTENT_DATA_PREFIX + ".test" ) );
    }

    @Test
    public void translate_entityIndexConfig_decide_by_type_for_contentdata()
        throws Exception
    {
        final PropertyTree contentData = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        contentData.setString( "test", "testValue" );

        final CreateContentParams mycontent = new CreateContentParams().
            name( "mycontent" ).
            parent( ContentPath.ROOT ).
            contentType( ContentTypeName.from( "mymodule:my-content-type" ) ).
            contentData( contentData );

        final CreateNodeParams createNode = translator.toCreateNode( mycontent );

        final IndexConfigDocument indexConfigDocument = createNode.getIndexConfigDocument();

        final IndexConfig configForData = indexConfigDocument.getConfigForPath( PropertyPath.from( CONTENT_DATA_PREFIX + ".test" ) );

        assertNotNull( configForData );
        assertEquals( true, configForData.isEnabled() );
        assertEquals( true, configForData.isDecideByType() );
    }

    @Test
    public void translate_entityIndexConfig_disabled_for_form()
        throws Exception
    {
        FormItemSet formItemSet = FormItemSet.newFormItemSet().
            name( "mySet" ).
            label( "My set" ).
            customText( "Custom text" ).
            helpText( "Help text" ).
            occurrences( 0, 10 ).
            addFormItem( Input.newInput().name( "myTextLine" ).inputType( InputTypes.TEXT_LINE ).build() ).
            addFormItem( Input.newInput().name( "myDate" ).inputType( InputTypes.DATE ).build() ).
            build();

        final Form form = Form.newForm().addFormItems( formItemSet.getFormItems() ).build();

        final CreateContentParams mycontent = new CreateContentParams().
            name( "mycontent" ).
            parent( ContentPath.ROOT ).
            contentData( new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() ) ).
            contentType( ContentTypeName.from( "mymodule:my-content-type" ) ).
            form( form );

        final CreateNodeParams createNode = translator.toCreateNode( mycontent );

        final IndexConfigDocument indexConfigDocument = createNode.getIndexConfigDocument();

        final IndexConfig indexConfig =
            indexConfigDocument.getConfigForPath( PropertyPath.from( "form.formItems.Input[0].inputType.name" ) );

        assertNotNull( indexConfig );
        assertTrue( !indexConfig.isEnabled() && !indexConfig.isFulltext() && !indexConfig.isnGram() );
    }

    @Test
    public void update_content_translate_thumbnail_to_node_attachment()
        throws Exception
    {
        final Content content = Content.newContent().
            id( ContentId.from( "myId" ) ).
            displayName( "displayName" ).
            name( ContentName.from( "myname" ) ).
            parentPath( ContentPath.from( ContentPath.from( "parent" ), "myname" ) ).
            thumbnail( Thumbnail.from( new BlobKey( "myBlobKey" ), "image/png", 100 ) ).
            build();

        final Attachments attachments = Attachments.from( Attachment.newAttachment().
            blobKey( new BlobKey( "myBlobKey" ) ).
            name( "MyAttachment" ).size( 1200 ).
            mimeType( "image/png" ).
            build() );

        final UpdateNodeParams updateNode = translator.toUpdateNodeCommand( content, attachments );

        final Node node = Node.newNode().build();

        final Node editedNode = updateNode.getEditor().edit( node ).build();

        final com.enonic.wem.api.node.Attachment thumbnailAttachment =
            editedNode.attachments().getAttachment( ThumbnailAttachmentSerializer.THUMB_NAME );

        assertNotNull( thumbnailAttachment );

    }

    @Test
    public void node_to_content_thumbnail()
    {
        final com.enonic.wem.api.node.Attachments attachments =
            com.enonic.wem.api.node.Attachments.from( com.enonic.wem.api.node.Attachment.newAttachment().
                blobKey( new BlobKey( "myThumbnail" ) ).
                name( ThumbnailAttachmentSerializer.THUMB_NAME ).
                size( 1200 ).
                mimeType( "image/png" ).
                build() );

        final PropertyTree data = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        data.addString( "contentType", "my-type" );
        data.addBoolean( "draft", false );
        data.addSet( "data" );
        data.addSet( "form" );

        final Node node = Node.newNode().id( NodeId.from( "myId" ) ).
            attachments( attachments ).
            parent( NodePath.ROOT ).
            path( "myPath" ).
            name( NodeName.from( "myname" ) ).
            data( data ).
            build();

        final Content content = translator.fromNode( node );

        Assert.assertNotNull( content.getThumbnail() );
    }

    @Test
    public void test_create_node_with_empty_name()
    {
        final CreateContentParams mycontent = new CreateContentParams().
            parent( ContentPath.ROOT ).
            contentData( new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() ) ).
            displayName( "test Name" );
        final CreateNodeParams createNodeParams = translator.toCreateNode( mycontent );
        Assert.assertNotNull( createNodeParams );
        Assert.assertEquals( createNodeParams.getName(), "test-name" );

    }

    @Test
    public void node_to_content_acl()
    {
        final AccessControlEntry entry1 = AccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            allow( Permission.READ ).
            deny( Permission.DELETE ).
            build();
        final AccessControlEntry entry2 = AccessControlEntry.create().
            principal( PrincipalKey.ofUser( UserStoreKey.system(), "user1" ) ).
            allow( Permission.MODIFY ).
            deny( Permission.PUBLISH ).
            build();
        AccessControlList acl = AccessControlList.create().add( entry1 ).add( entry2 ).build();
        AccessControlList effectiveAcl = acl.getEffective( AccessControlList.empty() );

        final PropertyTree contentAsData = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        contentAsData.addString( "contentType", "my-type" );
        contentAsData.addBoolean( "draft", false );
        contentAsData.addSet( "data" );
        contentAsData.addSet( "form" );

        final Node node = Node.newNode().id( NodeId.from( "myId" ) ).
            parent( NodePath.ROOT ).
            path( "myPath" ).
            name( NodeName.from( "myname" ) ).
            data( contentAsData ).
            accessControlList( acl ).
            effectiveAcl( effectiveAcl ).
            build();

        final Content content = translator.fromNode( node );

        Assert.assertNotNull( content.getAccessControlList() );
        Assert.assertNotNull( content.getEffectiveAccessControlList() );
    }

}
