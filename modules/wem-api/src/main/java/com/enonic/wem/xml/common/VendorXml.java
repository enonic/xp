package com.enonic.wem.xml.common;

import javax.xml.bind.annotation.XmlElement;

import com.enonic.wem.api.content.site.Vendor;
import com.enonic.wem.xml.template.AbstractTemplateXml;

public final class VendorXml
    extends AbstractTemplateXml<Vendor, Vendor.Builder>
{
    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "url")
    private String url;

    @Override
    public void from( final Vendor vendor )
    {
        this.name = vendor.getName();
        this.url = vendor.getUrl();
    }

    @Override
    public void to( final Vendor.Builder vendorBuilder )
    {
        vendorBuilder.name( this.name );
        vendorBuilder.url( this.url );
    }
}
