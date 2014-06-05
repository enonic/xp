package com.enonic.wem.api.content.page;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.content.page.region.RegionXml;
import com.enonic.wem.api.data.DataSetXml2;
import com.enonic.wem.api.data.DataSetXmlAdapter;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.xml.XmlObject;

import static com.enonic.wem.api.content.page.PageRegions.newPageRegions;

@XmlRootElement(name = "page-template")
public final class PageTemplateXml
    implements XmlObject<PageTemplate, PageTemplate.Builder>
{
    @XmlElement(name = "display-name", required = false)
    private String displayName;

    @XmlElement(name = "descriptor", required = false)
    private String descriptor;

    @XmlElement(name = "config", required = false)
    @XmlJavaTypeAdapter(DataSetXmlAdapter.class)
    private DataSetXml2 config = new DataSetXml2();

    @XmlElement(name = "content-type", required = false)
    @XmlElementWrapper(name = "can-render")
    private List<String> canRender = new ArrayList<>();

    @XmlElement(name = "region")
    @XmlElementWrapper(name = "regions")
    private List<RegionXml> regions = new ArrayList<>();

    @Override
    public void from( final PageTemplate template )
    {
        this.displayName = template.getDisplayName();
        this.descriptor = template.getDescriptor().toString();
        final RootDataSet cfgDataSet = template.getConfig();
        if ( cfgDataSet != null )
        {
            this.config.from( cfgDataSet );
        }

        for ( ContentTypeName contentType : template.getCanRender() )
        {
            this.canRender.add( contentType.toString() );
        }

        if ( template.hasRegions() )
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
        builder.displayName( this.displayName );
        builder.descriptor( PageDescriptorKey.from( this.descriptor ) );
        builder.canRender( ContentTypeNames.from( this.canRender ) );
        final RootDataSet configAsData = new RootDataSet();
        this.config.to( configAsData );
        builder.config( configAsData );

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
