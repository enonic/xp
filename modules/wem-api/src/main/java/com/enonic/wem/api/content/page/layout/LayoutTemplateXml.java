package com.enonic.wem.api.content.page.layout;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.content.page.region.RegionXml;
import com.enonic.wem.xml.template.AbstractTemplateXml;

import static com.enonic.wem.api.content.page.layout.LayoutRegions.newLayoutRegions;

@XmlRootElement(name = "layout-template")
public final class LayoutTemplateXml
    extends AbstractTemplateXml<LayoutTemplate, LayoutTemplate.Builder>
{

    @XmlElement(name = "region")
    @XmlElementWrapper(name = "regions")
    private List<RegionXml> regions = new ArrayList<>();

    @Override
    public void from( final LayoutTemplate template )
    {
        fromTemplate( template );

        if ( template.getRegions() != null )
        {
            for ( Region region : template.getRegions() )
            {
                final RegionXml regionAsXml = new RegionXml();
                regionAsXml.from( region );
                this.regions.add( regionAsXml );
            }
        }
    }

    @Override
    public void to( final LayoutTemplate.Builder builder )
    {
        toTemplate( builder );
        builder.name( new LayoutTemplateName( getName() ) );

        final LayoutRegions.Builder regionsBuilder = newLayoutRegions();
        for ( RegionXml regionAsXml : this.regions )
        {
            final Region.Builder regionBuilder = Region.newRegion();
            regionAsXml.to( regionBuilder );
            regionsBuilder.add( regionBuilder.build() );
        }
        builder.regions( regionsBuilder.build() );
    }
}
