package com.enonic.wem.api.content.site;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplates;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.schema.content.ContentTypeName;

public final class SetSiteTemplateEditor
    implements SiteTemplateEditor
{
    private final String displayName;

    private final String description;

    private final String url;

    private final Vendor vendor;

    private final ModuleKeys modules;

    private final ContentTypeFilter contentTypeFilter;

    private final ContentTypeName rootContentType;

    private final ImmutableMap<PageTemplateKey, PageTemplate> templatesAdded;

    private final ImmutableSet<PageTemplateKey> templatesRemoved;

    private SetSiteTemplateEditor( final Builder builder )
    {
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.url = builder.url;
        this.vendor = builder.vendor;
        this.modules = builder.modules;
        this.contentTypeFilter = builder.contentTypeFilter;
        this.rootContentType = builder.rootContentType;
        this.templatesAdded = ImmutableMap.copyOf( builder.templatesAdded );
        this.templatesRemoved = ImmutableSet.copyOf( builder.templatesRemoved );
    }

    public static Builder newEditor()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String displayName;

        private String description;

        private String url;

        private Vendor vendor;

        private ModuleKeys modules;

        private ContentTypeFilter contentTypeFilter;

        private ContentTypeName rootContentType;

        private final LinkedHashMap<PageTemplateKey, PageTemplate> templatesAdded;

        private final LinkedHashSet<PageTemplateKey> templatesRemoved;

        private Builder()
        {
            this.templatesAdded = new LinkedHashMap<>();
            this.templatesRemoved = new LinkedHashSet<>();
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

        public Builder addTemplate( final PageTemplate template )
        {
            this.templatesAdded.put( template.getKey(), template );
            return this;
        }

        public Builder removeTemplate( final PageTemplateKey template )
        {
            this.templatesRemoved.add( template );
            return this;
        }

        public SetSiteTemplateEditor build()
        {
            return new SetSiteTemplateEditor( this );
        }
    }

    @Override
    public SiteTemplate edit( final SiteTemplate source )
    {
        boolean isUpdated = false;
        final SiteTemplate.Builder edited = SiteTemplate.copyOf( source );
        if ( this.displayName != null && !this.displayName.equals( source.getDisplayName() ) )
        {
            edited.displayName( this.displayName );
            isUpdated = true;
        }
        if ( this.description != null && !this.description.equals( source.getDescription() ) )
        {
            edited.description( this.description );
            isUpdated = true;
        }
        if ( this.url != null && !this.url.equals( source.getUrl() ) )
        {
            edited.url( this.url );
            isUpdated = true;
        }
        if ( this.vendor != null && !this.vendor.equals( source.getVendor() ) )
        {
            edited.vendor( this.vendor );
            isUpdated = true;
        }
        if ( this.modules != null && !this.modules.equals( source.getModules() ) )
        {
            edited.modules( this.modules );
            isUpdated = true;
        }
        if ( this.contentTypeFilter != null && !this.contentTypeFilter.equals( source.getContentTypeFilter() ) )
        {
            edited.contentTypeFilter( this.contentTypeFilter );
            isUpdated = true;
        }
        if ( this.rootContentType != null && !this.rootContentType.equals( source.getRootContentType() ) )
        {
            edited.rootContentType( this.rootContentType );
            isUpdated = true;
        }

        // merge templates
        final PageTemplates sourcePageTemplates = source.getPageTemplates();
        for ( PageTemplate template : sourcePageTemplates )
        {
            if ( this.templatesRemoved.contains( template.getKey() ) )
            {
                isUpdated = true;
            }
            else if ( this.templatesAdded.containsKey( template.getKey() ) )
            {
                if ( sourcePageTemplates.getTemplate( template.getName() ) != null )
                {
                    edited.removeTemplate( template.getKey() );
                }
                edited.addPageTemplate( PageTemplate.copyOf( template ).build() );
                isUpdated = true; // TODO check if template.equals(templatesAdded.get(key))
            }
            else
            {
                edited.addPageTemplate( PageTemplate.copyOf( template ).build() );
            }
        }

        return isUpdated ? edited.build() : null;
    }

}
