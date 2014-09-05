package com.enonic.wem.core.initializer;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang.StringUtils;

import com.google.common.io.ByteSource;
import com.google.common.io.Resources;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypesParams;

import static com.enonic.wem.api.content.attachment.Attachment.newAttachment;

@Singleton
public final class DemoInitializer
{
    private static final Context STAGE_CONTEXT = new Context( ContentConstants.WORKSPACE_STAGE );

    private static final String[] FOLDER_IMAGES_POP =
        {"Pop_01.jpg", "Pop_02.jpg", "Pop_03.jpg", "Pop_04.jpg", "Pop_05.jpg", "Pop_06.jpg", "Pop_07.jpg", "Pop_08.jpg", "Pop-Black.jpg",
            "Pop-Green.jpg", "Pop-Silverpink.jpg"};

    private static final String[] FOLDER_IMAGES_BIG =
        {"Big Bounce - R\u00f8d Tattoo.jpg", "Big Bounce - R\u00f8d.jpg", "Big Bounce_01.jpg", "Big Bounce_02.jpg", "Big Bounce_03.jpg",
            "Big Bounce_04.jpg", "Big Bounce_05.jpg", "Big Bounce_06.jpg", "Big Bounce_07.jpg", "Big Bounce_08.jpg", "Big Bounce_10.jpg",
            "Big Bounce_11.jpg", "Big Bounce_12.jpg"};

    private static final Form MEDIA_IMAGE_FORM = createMediaImageForm();

    private static final String IMAGE_ARCHIVE_PATH_ELEMENT = "imagearchive";

    private static final String TRAMPOLINE_PATH_ELEMENT = "trampoliner";

    private static final String JUMPING_JACK_BIG_BOUNCE_PATH_ELEMENT = "jumping-jack-big-bounce";

    private static final String JUMPING_JACK_POP_PATH_ELEMENT = "jumping-jack-pop";

    @Inject
    private BlobService blobService;

    @Inject
    private ContentService contentService;

    @Inject
    private ContentTypeService contentTypeService;

    public void initialize()
        throws Exception
    {
        createImages();
    }

    private boolean hasContent( final ContentPath path )
    {
        try
        {
            return this.contentService.getByPath( path, STAGE_CONTEXT ) != null;
        }
        catch ( final Exception e )
        {
            return false;
        }
    }

    private void createImages()
        throws Exception
    {
        final Context context = STAGE_CONTEXT;

        final ContentPath imageArchivePath = ContentPath.from( ContentPath.ROOT, IMAGE_ARCHIVE_PATH_ELEMENT );
        if ( hasContent( imageArchivePath ) )
        {
            return;
        }

        contentService.create( createFolder().
            name( IMAGE_ARCHIVE_PATH_ELEMENT ).
            parent( ContentPath.ROOT ).
            displayName( "Image Archive" ), context );

        contentService.create( createFolder().
            name( "misc" ).
            parent( imageArchivePath ).
            displayName( "Misc" ), context );

        contentService.create( createFolder().
            name( "people" ).
            parent( imageArchivePath ).
            displayName( "People" ), context );

        ContentPath trampolinerPath = contentService.create( createFolder().
            name( TRAMPOLINE_PATH_ELEMENT ).
            parent( imageArchivePath ).
            displayName( "Trampoliner" ), context ).getPath();

        final ContentPath folderImagesBig = contentService.create( createFolder().
            name( JUMPING_JACK_BIG_BOUNCE_PATH_ELEMENT ).
            parent( trampolinerPath ).
            displayName( "Jumping Jack - Big Bounce" ), context ).getPath();

        final ContentPath folderImagesPop = contentService.create( createFolder().
            name( JUMPING_JACK_POP_PATH_ELEMENT ).
            parent( trampolinerPath ).
            displayName( "Jumping Jack - Pop" ).
            contentType( ContentTypeName.folder() ), context ).getPath();

        for ( final String fileName : FOLDER_IMAGES_BIG )
        {
            createImageContent( folderImagesBig, fileName, StringUtils.substringBefore( fileName, "." ) );
        }

        for ( final String fileName : FOLDER_IMAGES_POP )
        {
            createImageContent( folderImagesPop, fileName, StringUtils.substringBefore( fileName, "." ) );
        }
    }

    private void createImageContent( final ContentPath parent, final String fileName, final String displayName )
        throws Exception
    {
        // TODO: fix due to Intellij failing when building jar with ø in resource file
        final String fixedFileName = fileName.replace( "\u00f8", "_o_" );
        final byte[] bytes = loadImageFileAsBytes( fixedFileName );
        if ( bytes == null )
        {
            return;
        }

        // FIXME: hack to avoid exception from NodeName preconditions
        final String filteredFileName =
            fileName.replace( " ", "_" ).replace( "ø", "o" ).replace( "æ", "ae" ).replace( "å", "aa" ).toLowerCase();

        final ContentData dataSet = createContentData( filteredFileName );

        final Blob blob = blobService.create( ByteSource.wrap( bytes ).openStream() );
        final Attachment attachment = newAttachment().name( filteredFileName ).blobKey( blob.getKey() ).mimeType( "image/jpeg" ).build();

        final CreateContentParams params = new CreateContentParams().
            contentType( ContentTypeName.imageMedia() ).
            form( MEDIA_IMAGE_FORM ).
            displayName( displayName ).
            name( filteredFileName ).
            parent( parent ).
            contentData( dataSet ).
            attachments( attachment );
        contentService.create( params, STAGE_CONTEXT ).getId();
    }

    private ContentData createContentData( final String attachmentName )
    {
        final ContentData dataSet = new ContentData();
        dataSet.add( Property.newString( "mimeType", "image/png" ) );
        dataSet.add( Property.newString( "image", attachmentName ) );
        return dataSet;
    }

    private byte[] loadImageFileAsBytes( final String fileName )
    {
        final String filePath = "/META-INF/demo-images/" + fileName;

        try
        {
            return Resources.toByteArray( getClass().getResource( filePath ) );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    private CreateContentParams createFolder()
    {
        return new CreateContentParams().
            owner( AccountKey.anonymous() ).
            contentData( new ContentData() ).
            form( getContentType( ContentTypeName.folder() ).form() ).
            contentType( ContentTypeName.folder() );
    }

    private ContentType getContentType( ContentTypeName name )
    {
        final GetContentTypesParams params = new GetContentTypesParams().contentTypeNames( ContentTypeNames.from( name ) );
        return contentTypeService.getByNames( params ).first();
    }

    private static Form createMediaImageForm()
    {
        return Form.newForm().
            addFormItem( Input.newInput().name( "image" ).
                inputType( InputTypes.IMAGE ).build() ).
            addFormItem( Input.newInput().name( "mimeType" ).
                inputType( InputTypes.TEXT_LINE ).
                label( "Mime type" ).
                occurrences( 1, 1 ).
                build() ).

            build();
    }
}
