package com.enonic.wem.core.content;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIndexPaths;
import com.enonic.wem.api.content.ContentName;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.thumb.Thumbnail;
import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.index.IndexConfig;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.core.content.serializer.ThumbnailAttachmentSerializer;
import com.enonic.wem.core.entity.CreateNodeParams;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodeId;
import com.enonic.wem.core.entity.NodeName;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.entity.UpdateNodeParams;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

public class ContentNodeTranslatorTest
{
    private static final String CONTENT_DATA_PREFIX = ContentIndexPaths.CONTENT_DATA;

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
    public void toNode_contentData_to_rootdataset()
        throws Exception
    {
        final DataSet rootDataSet = RootDataSet.newDataSet().set( "test", "testValue", ValueTypes.STRING ).build();

        final CreateContentParams mycontent = new CreateContentParams().
            name( "mycontent" ).
            parent( ContentPath.ROOT ).
            contentType( ContentTypeName.from( "mymodule:my-content-type" ) ).
            contentData( new ContentData( rootDataSet.toRootDataSet() ) );

        final CreateNodeParams createNode = translator.toCreateNode( mycontent );

        final Property testProperty = createNode.getData().getProperty( CONTENT_DATA_PREFIX + ".test" );

        assertNotNull( testProperty );
        assertEquals( "testValue", testProperty.getValue().asString() );
    }

    @Test
    public void translate_entityIndexConfig_decide_by_type_for_contentdata()
        throws Exception
    {
        final DataSet rootDataSet = RootDataSet.newDataSet().set( "test", "testValue", ValueTypes.STRING ).build();

        final CreateContentParams mycontent = new CreateContentParams().
            name( "mycontent" ).
            parent( ContentPath.ROOT ).
            contentType( ContentTypeName.from( "mymodule:my-content-type" ) ).
            contentData( new ContentData( rootDataSet.toRootDataSet() ) );

        final CreateNodeParams createNode = translator.toCreateNode( mycontent );

        final IndexConfigDocument indexConfigDocument = createNode.getIndexConfigDocument();

        final IndexConfig configForData = indexConfigDocument.getConfigForPath( DataPath.from( CONTENT_DATA_PREFIX + ".test" ) );

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
            contentType( ContentTypeName.from( "mymodule:my-content-type" ) ).
            form( form );

        final CreateNodeParams createNode = translator.toCreateNode( mycontent );

        final IndexConfigDocument indexConfigDocument = createNode.getIndexConfigDocument();

        final IndexConfig indexConfig = indexConfigDocument.getConfigForPath( DataPath.from( "form.formItems.Input[0].inputType.name" ) );

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

        final com.enonic.wem.core.entity.Attachment thumbnailAttachment =
            editedNode.attachments().getAttachment( ThumbnailAttachmentSerializer.THUMB_NAME );

        assertNotNull( thumbnailAttachment );

    }

    @Test
    public void node_to_content_thumbnail()
    {

        final com.enonic.wem.core.entity.Attachments attachments =
            com.enonic.wem.core.entity.Attachments.from( com.enonic.wem.core.entity.Attachment.newAttachment().
                blobKey( new BlobKey( "myThumbnail" ) ).
                name( ThumbnailAttachmentSerializer.THUMB_NAME ).
                size( 1200 ).
                mimeType( "image/png" ).
                build() );

        final RootDataSet data = new RootDataSet();
        data.addProperty( "contentType", Value.newString( "my-type" ) );

        final Node node = Node.newNode().id( NodeId.from( "myId" ) ).
            attachments( attachments ).
            parent( NodePath.ROOT ).
            path( "myPath" ).
            name( NodeName.from( "myname" ) ).
            rootDataSet( data ).
            build();

        final Content content = translator.fromNode( node );

        Assert.assertNotNull( content.getThumbnail() );
    }

    @Test
    public void test_create_node_with_empty_name()
    {
        final CreateContentParams mycontent = new CreateContentParams().
            parent( ContentPath.ROOT ).
            displayName( "test Name" );
        final CreateNodeParams createNodeParams = translator.toCreateNode( mycontent );
        Assert.assertNotNull( createNodeParams );
        Assert.assertEquals( createNodeParams.getName(), "test-name" );

    }
}
