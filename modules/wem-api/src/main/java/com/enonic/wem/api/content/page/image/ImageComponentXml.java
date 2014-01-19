package com.enonic.wem.api.content.page.image;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.PageComponentXml;
import com.enonic.wem.api.content.page.TemplateKey;
import com.enonic.wem.xml.XmlObject;

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
        this.image = component.getImage().toString();
    }

    @Override
    public void to( final ImageComponent.Builder builder )
    {
        super.to( builder );
        builder.image( ContentId.from( this.image ) );
    }

    @Override
    protected TemplateKey toTemplateKey( final String s )
    {
        return ImageTemplateKey.from( s );
    }
}
