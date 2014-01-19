package com.enonic.wem.api.content.page;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.content.page.region.RegionXml;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.xml.template.AbstractTemplateXml;

import static com.enonic.wem.api.content.page.PageRegions.newPageRegions;

@XmlRootElement(name = "page-template")
public final class PageTemplateXml
    extends AbstractTemplateXml<PageTemplate, PageTemplate.Builder>
{
    @XmlElement(name = "content-type", required = false)
    @XmlElementWrapper(name = "can-render")
    private List<String> canRender = new ArrayList<>();

    @XmlElement(name = "region")
    @XmlElementWrapper(name = "regions")
    private List<RegionXml> regions = new ArrayList<>();

    @Override
    public void from( final PageTemplate template )
    {
        fromTemplate( template );
        for ( ContentTypeName contentType : template.getCanRender() )
        {
            this.canRender.add( contentType.toString() );
        }

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
    public void to( final PageTemplate.Builder builder )
    {
        toTemplate( builder );
        builder.canRender( ContentTypeNames.from( this.canRender ) );
        builder.name( new PageTemplateName( getName() ) );

        final PageRegions.Builder regionsBuilder = newPageRegions();
        for ( RegionXml regionAsXml : this.regions )
        {
            final Region.Builder regionBuilder = Region.newRegion();
            regionAsXml.to( regionBuilder );
            regionsBuilder.add( regionBuilder.build() );
        }
        builder.regions( regionsBuilder.build() );
    }
}
