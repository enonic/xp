package com.enonic.wem.api.content.page;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.page.region.RegionDescriptor;
import com.enonic.wem.api.content.page.region.RegionDescriptorXml;
import com.enonic.wem.api.content.page.region.RegionDescriptors;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.FormXml;
import com.enonic.wem.api.xml.XmlObject;

// TODO: Probably a wrong name for the class. Should be PageComponentXml.
@XmlRootElement(name = "page-component")
public final class PageDescriptorXml
    implements XmlObject<PageDescriptor, PageDescriptor.Builder>
{
    @XmlElement(name = "display-name", required = false)
    private String displayName;

    @XmlElement(name = "config", required = false)
    private FormXml configForm = new FormXml();

    @XmlElement(name = "region")
    @XmlElementWrapper(name = "regions")
    private List<RegionDescriptorXml> regions = new ArrayList<>();

    @Override
    public void from( final PageDescriptor pageDescriptor )
    {
        this.displayName = pageDescriptor.getDisplayName();
        this.configForm.from( pageDescriptor.getConfig() );

        for ( final RegionDescriptor regionDescriptor : pageDescriptor.getRegions() )
        {
            final RegionDescriptorXml regionDescriptorXml = new RegionDescriptorXml();
            regionDescriptorXml.from( regionDescriptor );
            this.regions.add( regionDescriptorXml );
        }
    }

    @Override
    public void to( final PageDescriptor.Builder builder )
    {
        builder.displayName( this.displayName );
        final Form.Builder formBuilder = Form.newForm();
        this.configForm.to( formBuilder );
        builder.config( formBuilder.build() );

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
