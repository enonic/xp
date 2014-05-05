package com.enonic.wem.api.content.page.image;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageComponentXml;
import com.enonic.wem.api.xml.XmlObject;

import static com.enonic.wem.api.content.page.image.ImageComponent.newImageComponent;

@XmlRootElement(name = "image-component")
public final class ImageComponentXml
    extends PageComponentXml
    implements XmlObject<ImageComponent, ImageComponent.Builder>
{
    @XmlAttribute(name = "image", required = true)
    String image;

    @Override
    public void from( final ImageComponent component )
    {
        super.from( component );
        if ( component.getImage() != null )
        {
            this.image = component.getImage().toString();
        }
    }

    @Override
    public void to( final ImageComponent.Builder builder )
    {
        super.to( builder );
        if ( this.image != null )
        {
            builder.image( ContentId.from( this.image ) );
        }
    }

    @Override
    protected DescriptorKey toDescriptorKey( final String s )
    {
        return ImageDescriptorKey.from( s );
    }

    @Override
    protected PageComponent toPageComponent()
    {
        ImageComponent.Builder builder = newImageComponent();
        to( builder );
        return builder.build();
    }
}
