package com.enonic.wem.api.content.page.layout;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.page.AbstractDescriptorBasedPageComponentXml;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.content.page.region.RegionXml;
import com.enonic.wem.api.xml.XmlObject;

import static com.enonic.wem.api.content.page.layout.LayoutComponent.newLayoutComponent;
import static com.enonic.wem.api.content.page.layout.LayoutRegions.newLayoutRegions;

@XmlRootElement(name = "layout-component")
public final class LayoutComponentXml
    extends AbstractDescriptorBasedPageComponentXml
    implements XmlObject<LayoutComponent, LayoutComponent.Builder>
{
    @XmlElement(name = "region")
    @XmlElementWrapper(name = "regions")
    private List<RegionXml> regions = new ArrayList<>();

    @Override
    public void from( final LayoutComponent component )
    {
        super.from( component );

        if ( component.hasRegions() )
        {
            for ( Region region : component.getRegions() )
            {
                final RegionXml regionAsXml = new RegionXml();
                regionAsXml.from( region );
                this.regions.add( regionAsXml );
            }
        }
    }

    @Override
    public void to( final LayoutComponent.Builder builder )
    {
        super.to( builder );

        final LayoutRegions.Builder regionsBuilder = newLayoutRegions();
        for ( RegionXml regionAsXml : this.regions )
        {
            final Region.Builder regionBuilder = Region.newRegion();
            regionAsXml.to( regionBuilder );
            regionsBuilder.add( regionBuilder.build() );
        }
        builder.regions( regionsBuilder.build() );
    }

    @Override
    protected DescriptorKey toDescriptorKey( final String s )
    {
        return LayoutDescriptorKey.from( s );
    }

    @Override
    public PageComponent toPageComponent()
    {
        LayoutComponent.Builder builder = newLayoutComponent();
        to( builder );
        return builder.build();
    }
}
