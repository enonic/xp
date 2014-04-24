package com.enonic.wem.core.schema.content;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.io.ByteStreams;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.content.ContentInitializer;
import com.enonic.wem.core.support.BaseInitializer;

import static com.enonic.wem.api.content.attachment.Attachment.newAttachment;


public class DemoImagesInitializer
    extends BaseInitializer
{
    private static final String[] FOLDER_IMAGES_POP =
        {"Pop_01.jpg", "Pop_02.jpg", "Pop_03.jpg", "Pop_04.jpg", "Pop_05.jpg", "Pop_06.jpg", "Pop_07.jpg", "Pop_08.jpg", "Pop-Black.jpg",
            "Pop-Green.jpg", "Pop-Silverpink.jpg"};

    private static final String[] FOLDER_IMAGES_BIG =
        {"Big Bounce - R\u00f8d Tattoo.jpg", "Big Bounce - R\u00f8d.jpg", "Big Bounce_01.jpg", "Big Bounce_02.jpg", "Big Bounce_03.jpg",
            "Big Bounce_04.jpg", "Big Bounce_05.jpg", "Big Bounce_06.jpg", "Big Bounce_07.jpg", "Big Bounce_08.jpg", "Big Bounce_10.jpg",
            "Big Bounce_11.jpg", "Big Bounce_12.jpg"};

    @Inject
    private BlobService blobService;

    @Inject
    private ContentService contentService;

    protected DemoImagesInitializer()
    {
        super( 20, "demo-images" );
    }

    @Override
    public void initialize()
        throws Exception
    {
        createImages();
    }

    private void createImages()
        throws IOException
    {
        final ContentPath folderImagesBig = ContentPath.from( ContentInitializer.IMAGE_ARCHIVE_PATH_ELEMENT + "/" +
                                                                  ContentInitializer.TRAMPOLINE_PATH_ELEMENT + "/" +
                                                                  ContentInitializer.JUMPING_JACK_BIG_BOUNCE_PATH_ELEMENT );

        for ( String fileName : FOLDER_IMAGES_BIG )
        {
            createImageContent( folderImagesBig, fileName, StringUtils.substringBefore( fileName, "." ) );
        }
        final ContentPath folderImagesPop = ContentPath.from( ContentInitializer.IMAGE_ARCHIVE_PATH_ELEMENT + "/" +
                                                                  ContentInitializer.TRAMPOLINE_PATH_ELEMENT + "/" +
                                                                  ContentInitializer.JUMPING_JACK_POP_PATH_ELEMENT );
        for ( String fileName : FOLDER_IMAGES_POP )
        {
            createImageContent( folderImagesPop, fileName, StringUtils.substringBefore( fileName, "." ) );
        }
    }

    private void createImageContent( final ContentPath parent, final String fileName, final String displayName )
        throws IOException
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

        final Blob blob = blobService.create( ByteStreams.newInputStreamSupplier( bytes ).getInput() );
        final Attachment attachment = newAttachment().name( filteredFileName ).blobKey( blob.getKey() ).mimeType( "image/jpeg" ).build();

        final CreateContentParams params = new CreateContentParams().
            contentType( ContentTypeName.imageMedia() ).
            form( ContentTypesInitializer.MEDIA_IMAGE_FORM ).
            displayName( displayName ).
            name( filteredFileName ).
            parent( parent ).
            contentData( dataSet ).
            attachments( attachment );
        contentService.create( params ).getId();
    }

    private ContentData createContentData( final String attachmentName )
    {
        final ContentData dataSet = new ContentData();
        dataSet.add( new Property.String( "mimeType", "image/png" ) );
        dataSet.add( new Property.String( "image", attachmentName ) );
        return dataSet;
    }

    protected byte[] loadImageFileAsBytes( final String fileName )
    {
        final String filePath = "/META-INF/demo-images/" + fileName;
        try
        {
            return IOUtils.toByteArray( this.getClass().getResourceAsStream( filePath ) );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

}
