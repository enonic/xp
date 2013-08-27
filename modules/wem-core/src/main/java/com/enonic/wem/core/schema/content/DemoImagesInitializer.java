package com.enonic.wem.core.schema.content;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.binary.Binary;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
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
    {
        final ContentPath folderImagesBig = ContentPath.from( "bildearkiv:/trampoliner/jumping-jack-big-bounce" );
        for ( String fileName : FOLDER_IMAGES_BIG )
        {
            createImageContent( folderImagesBig, fileName, StringUtils.substringBefore( fileName, "." ) );
        }
        final ContentPath folderImagesPop = ContentPath.from( "bildearkiv:/trampoliner/jumping-jack-pop" );
        for ( String fileName : FOLDER_IMAGES_POP )
        {
            createImageContent( folderImagesPop, fileName, StringUtils.substringBefore( fileName, "." ) );
        }
    }

    private void createImageContent( final ContentPath parent, final String fileName, final String displayName )
    {
        // TODO: fix due to Intellij failing when building jar with ø in resource file
        final String fixedFileName = fileName.replace( "\u00f8", "_o_" );
        final Binary binary = loadBinary( fixedFileName );
        if ( binary == null )
        {
            return;
        }
        final ContentData dataSet = createContentData( fileName );

        final Attachment attachment = newAttachment().name( fileName ).binary( binary ).mimeType( "image/jpeg" ).build();
        final CreateContent createContent = Commands.content().create().
            contentType( QualifiedContentTypeName.imageMedia() ).
            displayName( displayName ).
            name( fileName ).
            parentContentPath( parent ).
            contentData( dataSet ).
            attachments( attachment );
        client.execute( createContent ).getContentId();
    }

    private ContentData createContentData( final String attachmentName )
    {
        final ContentData dataSet = new ContentData();
        dataSet.add( Property.newProperty( "mimeType" ).type( ValueTypes.TEXT ).value( "image/png" ).build() );
        dataSet.add( Property.newProperty( "image" ).type( ValueTypes.ATTACHMENT_NAME ).value( attachmentName ).build() );
        return dataSet;
    }

    protected Binary loadBinary( final String fileName )
    {
        final String filePath = "/META-INF/demo-images/" + fileName;
        try
        {
            final byte[] iconData = IOUtils.toByteArray( this.getClass().getResourceAsStream( filePath ) );
            return Binary.from( iconData );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

}
