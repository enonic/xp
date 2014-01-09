package com.enonic.wem.api.content.page;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.page.region.PageRegions;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.DataSetXml;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.xml.template.AbstractTemplateXml;

@XmlRootElement(name = "page-template")
public final class PageTemplateXml
    extends AbstractTemplateXml<PageTemplate, PageTemplate.Builder>
{
    @XmlElement(name = "content-type", required = false)
    @XmlElementWrapper(name = "can-render")
    private List<String> canRender = new ArrayList<>();

    @XmlElement(name = "region")
    @XmlElementWrapper(name = "regions")
    private List<DataSetXml> regions = new ArrayList<>();

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
                final DataSet regionAsDataSet = region.toData().toDataSet( region.getName() );
                final DataSetXml regionAsXml = new DataSetXml();
                regionAsXml.from( regionAsDataSet );
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

        PageRegions.Builder regionsBuilder = PageRegions.newPageRegions();
        for ( DataSetXml regionAsXml : this.regions )
        {
            final RootDataSet regionAsData = new RootDataSet();
            regionAsXml.to( regionAsData );
            final Region region = Region.newRegion( regionAsData ).build();
            regionsBuilder.add( region );
        }
        builder.regions( regionsBuilder.build() );
    }
}
