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
import com.enonic.wem.api.support.Changes;
import com.enonic.wem.api.support.EditBuilder;

import static com.enonic.wem.api.support.PossibleChange.newPossibleChange;

public final class SiteTemplate
    implements Iterable<Template>, Identity<SiteTemplateKey>
{
    public static final ResourcePath DEFAULT_TEMPLATES_PATH = ResourcePath.from( "components/" );

    private final SiteTemplateKey siteTemplateKey;

    private final String displayName;

    private final String description;

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

    private SiteTemplate( final SiteTemplateProperties properties )
    {
        this.siteTemplateKey = properties.siteTemplateKey;
        this.displayName = properties.displayName;
        this.description = properties.description;
        this.url = properties.url;
        this.vendor = properties.vendor;
        this.modules = properties.modules;
        this.contentTypeFilter = properties.contentTypeFilter;
        this.rootContentType = properties.rootContentType;
        this.templatesByPath = properties.templates.build();

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

    public Iterable<ResourcePath> resourcePaths()
    {
        return templatesByPath.keySet();
    }

    public static Builder newSiteTemplate()
    {
        return new Builder();
    }

    public static class Builder
        extends SiteTemplateProperties
    {
        private Builder()
        {
            templates = ImmutableMap.builder();
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

        public SiteTemplate build()
        {
            return new SiteTemplate( this );
        }
    }

    public static class SiteTemplateProperties
    {
        SiteTemplateKey siteTemplateKey;

        String displayName;

        String description;

        String url;

        Vendor vendor;

        ModuleKeys modules = ModuleKeys.empty();

        ContentTypeFilter contentTypeFilter;

        ContentTypeName rootContentType;

        ImmutableMap.Builder<ResourcePath, Template> templates;
    }

    public static SiteTemplateEditBuilder editSiteTemplate( final SiteTemplate toBeEdited )
    {
        return new SiteTemplateEditBuilder( toBeEdited );
    }

    public static class SiteTemplateEditBuilder
        extends SiteTemplateProperties
        implements EditBuilder<SiteTemplate>
    {
        private final SiteTemplate original;

        private final Changes.Builder changes = new Changes.Builder();

        private SiteTemplateEditBuilder( SiteTemplate original )
        {
            this.original = original;
        }

        public SiteTemplateEditBuilder displayName( final String value )
        {
            this.displayName = value;
            return this;
        }

        public SiteTemplateEditBuilder description( final String value )
        {
            changes.recordChange( newPossibleChange( "description" ).from( this.original.getDescription() ).to( value ).build() );
            this.description = value;
            return this;
        }

        public SiteTemplateEditBuilder url( final String value )
        {
            changes.recordChange( newPossibleChange( "url" ).from( this.original.getUrl() ).to( value ).build() );
            this.url = value;
            return this;
        }

        public SiteTemplateEditBuilder vendor( final Vendor value )
        {
            changes.recordChange( newPossibleChange( "vendor" ).from( this.original.getVendor() ).to( value ).build() );
            this.vendor = value;
            return this;
        }

        public SiteTemplateEditBuilder modules( final ModuleKeys value )
        {
            changes.recordChange( newPossibleChange( "modules" ).from( this.original.getModules() ).to( value ).build() );
            this.modules = value;
            return this;
        }

        public SiteTemplateEditBuilder contentTypeFilter( final ContentTypeFilter value )
        {
            changes.recordChange(
                newPossibleChange( "contentTypeFilter" ).from( this.original.getContentTypeFilter() ).to( value ).build() );
            this.contentTypeFilter = value;
            return this;
        }

        public SiteTemplateEditBuilder rootContentType( final ContentTypeName value )
        {
            changes.recordChange( newPossibleChange( "rootContentType" ).from( this.original.getRootContentType() ).to( value ).build() );
            this.rootContentType = value;
            return this;
        }

        public SiteTemplateEditBuilder setPageTemplate( final PageTemplate value )
        {
            changes.recordChange( newPossibleChange( "pageTemplates" ).from( this.original.getPageTemplates().
                getTemplate( value.getPath() ) ).to( value ).build() );
            this.templates.put( value.getPath(), value );
            return this;
        }

        public SiteTemplateEditBuilder setPartTemplate( final PartTemplate value )
        {
            changes.recordChange( newPossibleChange( "partTemplates" ).from( this.original.getPartTemplates().
                getTemplate( value.getPath() ) ).to( value ).build() );
            this.templates.put( value.getPath(), value );
            return this;
        }

        public SiteTemplateEditBuilder setLayoutTemplate( final LayoutTemplate value )
        {
            changes.recordChange( newPossibleChange( "layoutTemplates" ).from( this.original.getLayoutTemplates().
                getTemplate( value.getPath() ) ).to( value ).build() );
            this.templates.put( value.getPath(), value );
            return this;
        }

        public SiteTemplateEditBuilder setImageTemplate( final ImageTemplate value )
        {
            changes.recordChange( newPossibleChange( "imageTemplates" ).from( this.original.getImageTemplates().
                getTemplate( value.getPath() ) ).to( value ).build() );
            this.templates.put( value.getPath(), value );
            return this;
        }

        public boolean isChanges()
        {
            return this.changes.isChanges();
        }

        public Changes getChanges()
        {
            return this.changes.build();
        }

        public SiteTemplate build()
        {
            return new SiteTemplate( this );
        }
    }
}
