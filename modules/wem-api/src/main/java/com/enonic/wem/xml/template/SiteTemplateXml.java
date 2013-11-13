package com.enonic.wem.xml.template;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Iterables;

import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.Vendor;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.xml.common.VendorXml;

@XmlRootElement(name = "site-template")
public final class SiteTemplateXml
    extends AbstractTemplateXml<SiteTemplate, SiteTemplate.Builder>
{
    @XmlElement(name = "info", required = false)
    protected String info;

    @XmlElement(name = "url", required = false)
    protected String url;

    @XmlElement(name = "vendor", required = false)
    protected VendorXml vendor;

    @XmlElement(name = "module", required = false)
    @XmlElementWrapper(name = "modules")
    private List<String> modules;

    @XmlElement(name = "content-filter", required = false)
    protected ContentFilterXml contentFilter;

    @XmlElement(name = "site-content", required = true)
    protected String siteContent;

    @Override
    public void from( final SiteTemplate template )
    {
        this.displayName = template.getDisplayName();
        this.info = template.getInfo();
        this.url = template.getUrl();
        final Vendor vendor = template.getVendor();
        if ( vendor != null )
        {
            this.vendor = new VendorXml();
            this.vendor.name = vendor.getName();
            this.vendor.url = vendor.getUrl();
        }
        this.modules = new ArrayList<>();
        for ( ModuleKey moduleKey : template.getModules() )
        {
            this.modules.add( moduleKey.toString() );
        }

        this.siteContent = template.getRootContentType().getContentTypeName();
    }

    @Override
    public void to( final SiteTemplate.Builder builder )
    {
        builder.
            displayName( this.displayName ).
            info( this.info ).
            url( this.url ).
            rootContentType( ContentTypeName.from( this.siteContent ) );
        if ( this.vendor != null )
        {
            builder.vendor( Vendor.newVendor().name( this.vendor.name ).url( this.vendor.url ).build() );
        }
        builder.modules( ModuleKeys.from( Iterables.toArray( this.modules, String.class ) ) );

    }

}
