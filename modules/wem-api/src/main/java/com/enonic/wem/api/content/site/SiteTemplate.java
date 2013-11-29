package com.enonic.wem.api.content.site;


import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.enonic.wem.api.Identity;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplates;
import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.page.image.ImageTemplate;
import com.enonic.wem.api.content.page.image.ImageTemplates;
import com.enonic.wem.api.content.page.layout.LayoutTemplate;
import com.enonic.wem.api.content.page.layout.LayoutTemplates;
import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.page.part.PartTemplates;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.api.schema.content.ContentTypeName;

public final class SiteTemplate
    implements Iterable<Template>, Identity<SiteTemplateKey>
{
    public static final ResourcePath DEFAULT_TEMPLATES_PATH = ResourcePath.from( "components/" );

    private final SiteTemplateKey siteTemplateKey;

    private final String displayName;

    private final String info;

    private final String url;

    private final Vendor vendor;

    private final ModuleKeys modules;

    private final ContentTypeFilter contentTypeFilter;

    private final ContentTypeName rootContentType;

    private final ImmutableMap<ResourcePath, Template> templatesByPath;

    private final PageTemplates pageTemplates;

    private final PartTemplates partTemplates;

    private final LayoutTemplates layoutTemplates;

    private final ImageTemplates imageTemplates;

    private SiteTemplate( final Builder builder )
    {
        this.siteTemplateKey = builder.siteTemplateKey;
        this.displayName = builder.displayName;
        this.info = builder.info;
        this.url = builder.url;
        this.vendor = builder.vendor;
        this.modules = builder.modules;
        this.contentTypeFilter = builder.contentTypeFilter;
        this.rootContentType = builder.rootContentType;
        this.templatesByPath = builder.templates.build();

        final PageTemplates.Builder pageTemplatesBuilder = PageTemplates.newPageTemplates();
        final PartTemplates.Builder partTemplatesBuilder = PartTemplates.newPartTemplates();
        final ImageTemplates.Builder imageTemplatesBuilder = ImageTemplates.newImageTemplates();
        final LayoutTemplates.Builder layoutTemplatesBuilder = LayoutTemplates.newLayoutTemplates();

        for ( Map.Entry<ResourcePath, Template> entry : this.templatesByPath.entrySet() )
        {
            if ( entry.getValue() instanceof LayoutTemplate )
            {
                layoutTemplatesBuilder.add( (LayoutTemplate) entry.getValue() );
            }
            else if ( entry.getValue() instanceof PageTemplate )
            {
                pageTemplatesBuilder.add( (PageTemplate) entry.getValue() );
            }
            else if ( entry.getValue() instanceof PartTemplate )
            {
                partTemplatesBuilder.add( (PartTemplate) entry.getValue() );
            }
            else if ( entry.getValue() instanceof ImageTemplate )
            {
                imageTemplatesBuilder.add( (ImageTemplate) entry.getValue() );
            }
        }

        this.pageTemplates = pageTemplatesBuilder.build();
        this.partTemplates = partTemplatesBuilder.build();
        this.imageTemplates = imageTemplatesBuilder.build();
        this.layoutTemplates = layoutTemplatesBuilder.build();
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

    public String getInfo()
    {
        return info;
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

    @Override
    public Iterator<Template> iterator()
    {
        return templatesByPath.values().iterator();
    }

    public PageTemplates getPageTemplates()
    {
        return pageTemplates;
    }

    public PartTemplates getPartTemplates()
    {
        return partTemplates;
    }

    public LayoutTemplates getLayoutTemplates()
    {
        return layoutTemplates;
    }

    public ImageTemplates getImageTemplates()
    {
        return imageTemplates;
    }

    public Template getTemplate( final ResourcePath path )
    {
        return this.templatesByPath.get( path );
    }

    public Iterator<ResourcePath> resourcePathIterator()
    {
        return templatesByPath.keySet().iterator();
    }

    public static Builder newSiteTemplate()
    {
        return new Builder();
    }

    public static class Builder
    {
        private SiteTemplateKey siteTemplateKey;

        private String displayName;

        private String info;

        private String url;

        private Vendor vendor;

        private ModuleKeys modules = ModuleKeys.empty();

        private ContentTypeFilter contentTypeFilter;

        private ContentTypeName rootContentType;

        private ImmutableMap.Builder<ResourcePath, Template> templates;

        private Builder()
        {
            templates = ImmutableMap.builder();
        }

        public Builder addTemplate( final ResourcePath path, final Template template )
        {
            final ResourcePath normalizedPath = path.toRelativePath().resolve( template.getName().toString() );
            templates.put( normalizedPath, template );
            return this;
        }

        public Builder addTemplate( final Template template )
        {
            return addTemplate( DEFAULT_TEMPLATES_PATH, template );
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

        public Builder info( final String info )
        {
            this.info = info;
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

        public SiteTemplate build()
        {
            return new SiteTemplate( this );
        }
    }
}
