package com.enonic.wem.api.content.page.region;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.xml.XmlObject;

@XmlRootElement(name = "regions")
public final class RegionDescriptorXml
    implements XmlObject<RegionDescriptor, RegionDescriptor.Builder>
{

    @XmlAttribute(name = "name", required = true)
    String name;

    @Override
    public void from( final RegionDescriptor regionDescriptor )
    {
        this.name = regionDescriptor.getName();
    }

    @Override
    public void to( final RegionDescriptor.Builder builder )
    {
        builder.name( this.name );
    }
}
