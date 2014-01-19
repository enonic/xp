package com.enonic.wem.core.content.page.image;


import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.TemplateKey;
import com.enonic.wem.api.content.page.image.ImageComponent;
import com.enonic.wem.api.content.page.image.ImageTemplateKey;
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
        asData.addProperty( "image", new Value.ContentId( component.getImage() ) );
        return asData;
    }

    public ImageComponent fromData( final DataSet asData )
    {
        ImageComponent.Builder component = ImageComponent.newImageComponent();
        applyPageComponentFromData( component, asData );
        component.image( ContentId.from( asData.getProperty( "image" ).getString() ) );
        return component.build();
    }

    @Override
    protected TemplateKey toTemplatekey( final String s )
    {
        return ImageTemplateKey.from( s );
    }
}
