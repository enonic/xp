package com.enonic.wem.core.content.schema.content;

import org.apache.commons.io.IOUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.binary.Binary;
import com.enonic.wem.api.content.binary.BinaryId;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.data.type.DataTypes;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.core.initializer.InitializerTask;
import com.enonic.wem.core.support.BaseInitializer;

@Component
@Order(20)
public class DemoImagesInitializer
    extends BaseInitializer
    implements InitializerTask
{

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
        final Binary binary = loadBinary( "enonic-logo.png" );
        if ( binary == null )
        {
            return;
        }
        final BinaryId binaryId = client.execute( Commands.binary().create().binary( binary ) );

        final RootDataSet dataSet = RootDataSet.newRootDataSet();
        dataSet.add( Data.newData( "mimeType" ).type( DataTypes.TEXT ).value( "image/png" ).build() );
        dataSet.add( Data.newData( "binaryId" ).type( DataTypes.TEXT ).value( binaryId.toString() ).build() );

        final CreateContent createContent = Commands.content().create().
            contentType( QualifiedContentTypeName.imageFile() ).
            displayName( "Enonic logo" ).
            parentContentPath( ContentPath.from( "default:/" ) ).
            rootDataSet( dataSet );
        client.execute( createContent );
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
