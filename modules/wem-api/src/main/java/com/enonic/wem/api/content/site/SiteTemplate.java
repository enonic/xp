package com.enonic.wem.api.content.site;


import java.util.LinkedHashMap;

import com.enonic.wem.api.Identity;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplateParams;
import com.enonic.wem.api.content.page.PageTemplates;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.schema.content.ContentTypeFilter;
import com.enonic.wem.api.schema.content.ContentTypeName;

import static com.enonic.wem.api.content.page.PageTemplateParams.newPageTemplateParams;

public final class SiteTemplate
    implements Identity<SiteTemplateKey, SiteTemplateName>
{
    private final SiteTemplateKey siteTemplateKey;

    private final String displayName;

    private final String description;

    private final String url;

    private final Vendor vendor;

    private final ModuleKeys modules;

    private final ContentTypeFilter contentTypeFilter;

    private final ContentTypeName rootContentType;

    private final PageTemplates pageTemplates;

    private SiteTemplate( final Builder builder )
    {
        this.siteTemplateKey = builder.siteTemplateKey;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.url = builder.url;
        this.vendor = builder.vendor;
        this.modules = builder.modules;
        this.contentTypeFilter = builder.contentTypeFilter;
        this.rootContentType = builder.rootContentType;

        final PageTemplates.Builder pageTemplatesBuilder = PageTemplates.newPageTemplates();
        pageTemplatesBuilder.addAll( builder.pageTemplates.values() );

        this.pageTemplates = pageTemplatesBuilder.build();
    }

    public SiteTemplateKey getKey()
    {
        return siteTemplateKey;
    }

    public SiteTemplateName getName()
    {
        return siteTemplateKey.getName();
    }

    public SiteTemplateVersion getVersion()
    {
        return siteTemplateKey.getVersion();
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public String getUrl()
    {
        return url;
    }

    public Vendor getVendor()
    {
        return vendor;
    }

    public ModuleKeys getModules()
    {
        return modules;
    }

    public ContentTypeFilter getContentTypeFilter()
    {
        return contentTypeFilter;
    }

    public ContentTypeName getRootContentType()
    {
        return rootContentType;
    }

    public PageTemplates getPageTemplates()
    {
        return pageTemplates;
    }

    public PageTemplate getDefaultPageTemplate( final ContentTypeName contentType )
    {
        final PageTemplateParams params = newPageTemplateParams().canRender( contentType ).build();
        return pageTemplates.filter( params ).first();
    }

    public static Builder newSiteTemplate()
    {
        return new Builder();
    }

    public static Builder copyOf( final SiteTemplate siteTemplate )
    {
        return new Builder( siteTemplate );
    }

    public static class Builder
    {
        private SiteTemplateKey siteTemplateKey;

        private String displayName;

        private String description;

        private String url;

        private Vendor vendor;

        private ModuleKeys modules = ModuleKeys.empty();

        private ContentTypeFilter contentTypeFilter;

        private ContentTypeName rootContentType;

        private final LinkedHashMap<PageTemplateKey, PageTemplate> pageTemplates;

        private Builder()
        {
            this.pageTemplates = new LinkedHashMap<>();
        }

        private Builder( final SiteTemplate source )
        {
            this.pageTemplates = new LinkedHashMap<>();
            for ( PageTemplate pageTemplate : source.getPageTemplates() )
            {
                this.pageTemplates.put( pageTemplate.getKey(), PageTemplate.copyOf( pageTemplate ).build() );
            }
            this.siteTemplateKey = source.siteTemplateKey;
            this.displayName = source.displayName;
            this.description = source.description;
            this.url = source.url;
            this.vendor = source.vendor;
            this.modules = source.modules;
            this.contentTypeFilter = source.contentTypeFilter;
            this.rootContentType = source.rootContentType;
        }

        public Builder key( final SiteTemplateKey siteTemplateKey )
        {
            this.siteTemplateKey = siteTemplateKey;
            return this;
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder description( final String description )
        {
            this.description = description;
            return this;
        }

        public Builder url( final String url )
        {
            this.url = url;
            return this;
        }

        public Builder vendor( final Vendor vendor )
        {
            this.vendor = vendor;
            return this;
        }

        public Builder modules( final ModuleKeys modules )
        {
            this.modules = modules;
            return this;
        }

        public Builder contentTypeFilter( final ContentTypeFilter contentTypeFilter )
        {
            this.contentTypeFilter = contentTypeFilter;
            return this;
        }

        public Builder rootContentType( final ContentTypeName rootContentType )
        {
            this.rootContentType = rootContentType;
            return this;
        }

        public Builder addPageTemplate( final PageTemplate pageTemplate )
        {
            this.pageTemplates.put( pageTemplate.getKey(), pageTemplate );
            return this;
        }

        public Builder removeTemplate( final PageTemplateKey pageTemplateKey )
        {
            this.pageTemplates.remove( pageTemplateKey );
            return this;
        }

        public SiteTemplate build()
        {
            return new SiteTemplate( this );
        }
    }

}
