package com.enonic.wem.core.content.schema.content;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.binary.Binary;
import com.enonic.wem.api.content.binary.BinaryId;
import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.data.type.ValueTypes;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.core.initializer.InitializerTask;
import com.enonic.wem.core.support.BaseInitializer;

@Component
@Order(20)
public class DemoImagesInitializer
    extends BaseInitializer
    implements InitializerTask
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
        super( "demo-images" );
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
        final BinaryId binaryId = client.execute( Commands.binary().create().binary( binary ) );

        final RootDataSet dataSet = createContentData( binaryId );

        final CreateContent createContent = Commands.content().create().
            contentType( QualifiedContentTypeName.imageMedia() ).
            displayName( displayName ).
            name( fileName ).
            parentContentPath( parent ).
            rootDataSet( dataSet );
        client.execute( createContent );
    }

    private RootDataSet createContentData( final BinaryId binaryId )
    {
        final RootDataSet dataSet = RootDataSet.newRootDataSet();
        dataSet.add( Property.newProperty( "mimeType" ).type( ValueTypes.TEXT ).value( "image/png" ).build() );
        dataSet.add( Property.newProperty( "binary" ).type( ValueTypes.BINARY_ID ).value( binaryId ).build() );
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
