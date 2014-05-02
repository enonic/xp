package com.enonic.wem.core.content.page.image;


import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.image.ImageComponent;
import com.enonic.wem.api.content.page.image.ImageDescriptorKey;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.core.content.page.PageComponentDataSerializer;

public class ImageComponentDataSerializer
    extends PageComponentDataSerializer<ImageComponent, ImageComponent>
{

    public DataSet toData( final ImageComponent component )
    {
        final DataSet asData = new DataSet( ImageComponent.class.getSimpleName() );
        applyPageComponentToData( component, asData );
        if ( component.getImage() != null )
        {
            asData.addProperty( "image", Value.newContentId( component.getImage() ) );
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
        return component.build();
    }

    @Override
    protected DescriptorKey toDescriptorkey( final String s )
    {
        return ImageDescriptorKey.from( s );
    }
}
