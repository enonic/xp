package com.enonic.xp.core.impl.content.serializer;


import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.region.ImageComponent;

public class ImageComponentDataSerializer
    extends ComponentDataSerializer<ImageComponent, ImageComponent>
{
    @Override
    public void toData( final ImageComponent component, final PropertySet parent )
    {
        final PropertySet asData = parent.addSet( ImageComponent.class.getSimpleName() );
        applyComponentToData( component, asData );
        if ( component.getImage() != null )
        {
            asData.addString( "image", component.getImage().toString() );
        }
        if ( component.hasConfig() )
        {
            asData.addSet( "config", component.getConfig().getRoot().copy( asData.getTree() ) );
        }
    }

    @Override
    public ImageComponent fromData( final PropertySet asData )
    {
        ImageComponent.Builder component = ImageComponent.create();
        applyComponentFromData( component, asData );
        if ( asData.isNotNull( "image" ) )
        {
            component.image( ContentId.from( asData.getString( "image" ) ) );
        }
        if ( asData.hasProperty( "config" ) )
        {
            component.config( asData.getSet( "config" ).toTree() );
        }
        return component.build();
    }
}
