package com.enonic.wem.api.content.page.region;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.page.AbstractPageComponentXml;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.image.ImageComponentXml;
import com.enonic.wem.api.content.page.layout.LayoutComponentXml;
import com.enonic.wem.api.content.page.part.PartComponentXml;
import com.enonic.wem.api.xml.XmlObject;

@XmlRootElement(name = "region")
public final class RegionXml
    implements XmlObject<Region, Region.Builder>
{
    @XmlAttribute(name = "name", required = true)
    String name;

    @XmlElements({@XmlElement(name = "part-component", type = PartComponentXml.class),
                     @XmlElement(name = "image-component", type = ImageComponentXml.class),
                     @XmlElement(name = "layout-component", type = LayoutComponentXml.class)})
    private List<AbstractPageComponentXml> components = new ArrayList<>();

    @Override
    public void from( final Region region )
    {
        this.name = region.getName();
        this.components = new ArrayList<>();
        for ( PageComponent component : region.getComponents() )
        {
            this.components.add( component.getType().toXml( component ) );
        }
    }

    @Override
    public void to( final Region.Builder builder )
    {
        builder.name( this.name );

        for ( AbstractPageComponentXml componentXml : components )
        {
            builder.add( componentXml.toPageComponent() );
        }
    }
}
