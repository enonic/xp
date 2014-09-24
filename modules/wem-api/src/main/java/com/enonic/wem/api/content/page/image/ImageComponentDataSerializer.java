package com.enonic.wem.api.content.page.image;


import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.AbstractPageComponentDataSerializer;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Value;

public class ImageComponentDataSerializer
    extends AbstractPageComponentDataSerializer<ImageComponent, ImageComponent>
{

    public DataSet toData( final ImageComponent component )
    {
        final DataSet asData = new DataSet( ImageComponent.class.getSimpleName() );
        applyPageComponentToData( component, asData );
        if ( component.getImage() != null )
        {
            asData.addProperty( "image", Value.newContentId( component.getImage() ) );
        }
        if ( component.hasConfig() )
        {
            asData.add( component.getConfig().toDataSet( "config" ) );
        }
        return asData;
    }

    public ImageComponent fromData( final DataSet asData )
    {
        ImageComponent.Builder component = ImageComponent.newImageComponent();
        applyPageComponentFromData( component, asData );
        if ( asData.hasData( "image" ) )
        {
            component.image( ContentId.from( asData.getProperty( "image" ).getString() ) );
        }
        if ( asData.hasData( "config" ) )
        {
            component.config( asData.getData( "config" ).toDataSet().toRootDataSet() );
        }
        return component.build();
    }
}
