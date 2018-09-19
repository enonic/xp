package com.enonic.xp.core.impl.content.processor;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.EditableContent;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.Media;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataService;

import static com.enonic.xp.media.MediaInfo.GPS_INFO_GEO_POINT;
import static org.junit.Assert.*;

public class ImageContentProcessorTest
{

    private final ImageContentProcessor imageContentProcessor = new ImageContentProcessor();

    private ContentService contentService;

    private ContentTypeService contentTypeService;

    private XDataService xDataService;

    @Before
    public void setUp()
        throws Exception
    {

        this.contentService = Mockito.mock( ContentService.class );
        this.xDataService = Mockito.mock( XDataService.class );
        this.contentTypeService = Mockito.mock( ContentTypeService.class );

        imageContentProcessor.setContentService( this.contentService );
        imageContentProcessor.setContentTypeService( this.contentTypeService );
        imageContentProcessor.setXDataService( this.xDataService );
    }

    @Test
    public void testSupports()
    {

        ContentType contentType =
            ContentType.create().superType( ContentTypeName.structured() ).name( ContentTypeName.imageMedia() ).build();
        assertTrue( imageContentProcessor.supports( contentType ) );

        contentType = ContentType.create().superType( ContentTypeName.structured() ).name( ContentTypeName.media() ).build();
        assertFalse( imageContentProcessor.supports( contentType ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateMoreThanOneAttachmentFails()
    {
        final CreateAttachments createAttachments = CreateAttachments.create().
            add( CreateAttachment.create().
                name( "myAtt1" ).
                byteSource( ByteSource.wrap( "this is 1st stuff".getBytes() ) ).
                text( "This is the text" ).
                build() ).
            add( CreateAttachment.create().
                name( "myAtt2" ).
                byteSource( ByteSource.wrap( "this is 2nd stuff".getBytes() ) ).
                text( "This is the text" ).
                build() ).
            build();

        final CreateContentParams params = createContentParams( createAttachments );

        final ProcessCreateParams processCreateParams = new ProcessCreateParams( params, MediaInfo.create().
            build() );

        this.imageContentProcessor.processCreate( processCreateParams );
    }

    @Test
    public void testProcessCreate()
        throws IOException
    {
        final CreateAttachments createAttachments = createAttachments();
        final CreateContentParams params = createContentParams( createAttachments );

        final ProcessCreateParams processCreateParams = new ProcessCreateParams( params, MediaInfo.create().
            build() );

        final ProcessCreateResult result = this.imageContentProcessor.processCreate( processCreateParams );

        assertEquals( result.getCreateContentParams().getCreateAttachments().first(), createAttachments.first() );
    }

    @Test
    public void testProcessUpdate()
        throws IOException
    {
        Mockito.when( contentService.getBinary( Mockito.any(), Mockito.any() ) ).thenReturn( this.loadImage( "cat-small.jpg" ) );

        final ProcessUpdateParams processUpdateParams = ProcessUpdateParams.create().
            contentType( ContentType.create().
                name( ContentTypeName.imageMedia() ).
                superType( ContentTypeName.imageMedia() ).
                build() ).
            build();

        final ProcessUpdateResult result = this.imageContentProcessor.processUpdate( processUpdateParams );

        final PropertyTree data = new PropertyTree();
        data.addProperty( ContentPropertyNames.MEDIA, ValueFactory.newString( "MyImage.jpg" ) );

        final EditableContent editableContent = new EditableContent( Media.create().
            name( "myContentName" ).
            type( ContentTypeName.imageMedia() ).
            parentPath( ContentPath.ROOT ).
            data( data ).
            addExtraData( new ExtraData( MediaInfo.IMAGE_INFO_METADATA_NAME, new PropertyTree() ) ).
            attachments( Attachments.from( Attachment.create().
                mimeType( "image/jpg" ).
                name( "MyImage.jpg" ).
                build() ) ).
            build() );

        result.getEditor().edit( editableContent );

        assertNotNull( editableContent.extraDatas.first().getData().getLong( "pixelSize", 0 ) );
        assertNotNull( editableContent.extraDatas.first().getData().getLong( "imageHeight", 0 ) );
        assertNotNull( editableContent.extraDatas.first().getData().getLong( "imageWidth", 0 ) );
        assertNotNull( editableContent.extraDatas.first().getData().getLong( "byteSize", 0 ) );
    }

    private static Form createGpsInfoMixinForm()
    {
        final Form.Builder form = Form.create();
        form.addFormItem( createGeoPoint( GPS_INFO_GEO_POINT, "Geo Point" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "altitude", "Altitude" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "direction", "Direction" ).occurrences( 0, 1 ).build() );

        return form.build();
    }

    private static Input.Builder createTextLine( final String name, final String label )
    {
        return Input.create().inputType( InputTypeName.TEXT_LINE ).label( label ).name( name ).immutable( true );
    }

    private static Input.Builder createGeoPoint( final String name, final String label )
    {
        return Input.create().inputType( InputTypeName.GEO_POINT ).label( label ).name( name ).immutable( true );
    }

    private XData createXData( final XDataName name, final String displayName, final Form form )
    {
        return XData.create().
            name( name ).
            displayName( displayName ).
            form( form ).
            build();
    }

    private CreateAttachments createAttachments()
    {
        return CreateAttachments.create().
            add( CreateAttachment.create().
                name( "imageAttach" ).
                byteSource( ByteSource.wrap( "this is image".getBytes() ) ).
                text( "This is the image" ).
                build() ).
            build();
    }

    private CreateContentParams createContentParams( final CreateAttachments createAttachments )
    {
        return CreateContentParams.create().
            parent( ContentPath.ROOT ).
            name( "myContent" ).
            contentData( new PropertyTree() ).
            type( ContentTypeName.imageMedia() ).
            createAttachments( createAttachments ).
            build();
    }

    protected ByteSource loadImage( final String name )
        throws IOException
    {
        final InputStream imageStream = this.getClass().getResourceAsStream( name );

        return ByteSource.wrap( ByteStreams.toByteArray( imageStream ) );
    }
}
