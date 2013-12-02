package com.enonic.wem.xml.template;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Iterables;

import com.enonic.wem.api.content.site.ContentTypeFilter;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.Vendor;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.xml.XmlObject;
import com.enonic.wem.xml.common.VendorXml;

@XmlRootElement(name = "site-template")
public final class SiteTemplateXml
    implements XmlObject<SiteTemplate, SiteTemplate.Builder>
{
    @XmlElement(name = "display-name", required = false)
    private String displayName;

    @XmlElement(name = "description", required = false)
    private String description;

    @XmlElement(name = "url", required = false)
    private String url;

    @XmlElement(name = "vendor", required = false)
    private VendorXml vendor = new VendorXml();

    @XmlElement(name = "module", required = false)
    @XmlElementWrapper(name = "modules")
    private List<String> modules = new ArrayList<>();

    @XmlElement(name = "content-filter", required = false)
    private ContentFilterXml contentFilter = new ContentFilterXml();

    @XmlElement(name = "site-content", required = true)
    private String siteContent;

    @Override
    public void from( final SiteTemplate template )
    {
        this.displayName = template.getDisplayName();
        this.description = template.getDescription();
        this.url = template.getUrl();

        final Vendor vendor = template.getVendor();
        if ( vendor != null )
        {
            this.vendor.from( vendor );
        }

        for ( ModuleKey moduleKey : template.getModules() )
        {
            this.modules.add( moduleKey.toString() );
        }

        final ContentTypeFilter filter = template.getContentTypeFilter();
        if ( filter != null )
        {
            this.contentFilter.from( filter );
        }

        this.siteContent = template.getRootContentType().getContentTypeName();
    }

    @Override
    public void to( final SiteTemplate.Builder builder )
    {
        builder.
            displayName( this.displayName ).
            description( this.description ).
            url( this.url ).
            rootContentType( ContentTypeName.from( this.siteContent ) );

        final Vendor.Builder vendorBuilder = Vendor.newVendor();
        this.vendor.to( vendorBuilder );
        builder.vendor( vendorBuilder.build() );

        builder.modules( ModuleKeys.from( Iterables.toArray( this.modules, String.class ) ) );

        final ContentTypeFilter.Builder filter = ContentTypeFilter.newContentFilter();
        this.contentFilter.to( filter );
        builder.contentTypeFilter( filter.build() );
    }

}
