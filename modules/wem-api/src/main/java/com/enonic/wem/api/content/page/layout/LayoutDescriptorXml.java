package com.enonic.wem.api.content.page.layout;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.page.DescriptorXml;
import com.enonic.wem.api.content.page.region.RegionDescriptor;
import com.enonic.wem.api.content.page.region.RegionDescriptorXml;
import com.enonic.wem.api.content.page.region.RegionDescriptors;

@XmlRootElement(name = "layout-component")
public final class LayoutDescriptorXml
    extends DescriptorXml<LayoutDescriptor, LayoutDescriptor.Builder>
{
    @XmlElement(name = "region")
    @XmlElementWrapper(name = "regions")
    private List<RegionDescriptorXml> regions = new ArrayList<>();

    @Override
    public void from( final LayoutDescriptor layoutDescriptor )
    {
        fromDescriptor( layoutDescriptor );
        for ( final RegionDescriptor regionDescriptor : layoutDescriptor.getRegions() )
        {
            final RegionDescriptorXml regionDescriptorXml = new RegionDescriptorXml();
            regionDescriptorXml.from( regionDescriptor );
            this.regions.add( regionDescriptorXml );
        }
    }

    @Override
    public void to( final LayoutDescriptor.Builder builder )
    {
        toDescriptor( builder );

        final RegionDescriptors.Builder regionDescriptors = RegionDescriptors.newRegionDescriptors();
        for ( final RegionDescriptorXml xml : regions )
        {
            final RegionDescriptor.Builder regionDescriptor = RegionDescriptor.newRegionDescriptor();
            xml.to( regionDescriptor );
            regionDescriptors.add( regionDescriptor.build() );
        }
        builder.regions( regionDescriptors.build() );
    }
}
