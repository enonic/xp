package com.enonic.wem.api.content.page.image;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.AbstractPageComponentXml;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.data.DataSetXml;
import com.enonic.wem.api.data.DataSetXmlAdapter;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.xml.XmlObject;

import static com.enonic.wem.api.content.page.image.ImageComponent.newImageComponent;

@XmlRootElement(name = "image-component")
public final class ImageComponentXml
    extends AbstractPageComponentXml
    implements XmlObject<ImageComponent, ImageComponent.Builder>
{
    @XmlAttribute(name = "image", required = true)
    String image;

    @XmlElement(name = "config", required = true)
    @XmlJavaTypeAdapter(DataSetXmlAdapter.class)
    private DataSetXml config = new DataSetXml();

    @Override
    public void from( final ImageComponent component )
    {
        super.from( component );
        if ( component.getImage() != null )
        {
            this.image = component.getImage().toString();
        }
        this.config = new DataSetXml();
        this.config.from( component.getConfig() );
    }

    @Override
    public void to( final ImageComponent.Builder builder )
    {
        super.to( builder );
        if ( this.image != null )
        {
            builder.image( ContentId.from( this.image ) );
        }

        final RootDataSet config = new RootDataSet();
        if ( this.config != null )
        {
            this.config.to( config );
        }
        builder.config( config );
    }

    @Override
    public PageComponent toPageComponent()
    {
        ImageComponent.Builder builder = newImageComponent();
        to( builder );
        return builder.build();
    }
}
