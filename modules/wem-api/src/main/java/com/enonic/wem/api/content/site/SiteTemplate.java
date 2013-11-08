package com.enonic.wem.api.content.site;


import java.util.Iterator;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.page.TemplateName;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;

import static com.google.common.collect.Maps.uniqueIndex;

public final class SiteTemplate
    implements Iterable<Template>
{
    private static final ResourcePath DEFAULT_TEMPLATES_PATH = ResourcePath.from( "components/" );

    private final SiteTemplateKey siteTemplateKey;

    private final String displayName;

    private final String description;

    private final Vendor vendor;

    private final ModuleKeys modules;

    private final ContentTypeNames supportedContentTypes;

    private final ContentTypeName rootContentType;

    private final ImmutableMap<ResourcePath, Template> templatesByPath;

    private final ImmutableMap<TemplateName, Template> templatesByName;

    private SiteTemplate( final Builder builder )
    {
        this.siteTemplateKey = builder.siteTemplateKey;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.vendor = builder.vendor;
        this.modules = builder.modules;
        this.supportedContentTypes = builder.supportedContentTypes;
        this.rootContentType = builder.rootContentType;
        this.templatesByPath = builder.templates.build();
        this.templatesByName = uniqueIndex( this.templatesByPath.values(), new ToNameFunction() );
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

    public Vendor getVendor()
    {
        return vendor;
    }

    public ModuleKeys getModules()
    {
        return modules;
    }

    public ContentTypeNames getSupportedContentTypes()
    {
        return supportedContentTypes;
    }

    public ContentTypeName getRootContentType()
    {
        return rootContentType;
    }

    public Template getTemplate( final TemplateName templateName )
    {
        return this.templatesByName.get( templateName );
    }

    public Template getTemplate( final ResourcePath path )
    {
        return this.templatesByPath.get( path );
    }

    @Override
    public Iterator<Template> iterator()
    {
        return templatesByName.values().iterator();
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

        private String description;

        private Vendor vendor;

        private ModuleKeys modules = ModuleKeys.empty();

        private ContentTypeNames supportedContentTypes = ContentTypeNames.empty();

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

        public Builder description( final String description )
        {
            this.description = description;
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

        public Builder supportedContentTypes( final ContentTypeNames supportedContentTypes )
        {
            this.supportedContentTypes = supportedContentTypes;
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

    private final static class ToNameFunction
        implements Function<Template, TemplateName>
    {
        @Override
        public TemplateName apply( final Template value )
        {
            return value.getName();
        }
    }
}
