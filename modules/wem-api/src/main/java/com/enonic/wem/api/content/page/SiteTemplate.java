package com.enonic.wem.api.content.page;


import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;

public final class SiteTemplate
{
    private final SiteTemplateId id;

    private final String displayName;

    private final String description;

    private final String vendorName;

    private final String vendorUrl;

    private final ModuleKeys modules;

    private final ContentTypeNames supportedContentTypes;

    private final ContentTypeName rootContentType;

    private SiteTemplate( final Builder builder )
    {
        this.id = builder.id;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.vendorName = builder.vendorName;
        this.vendorUrl = builder.vendorUrl;
        this.modules = builder.modules;
        this.supportedContentTypes = builder.supportedContentTypes;
        this.rootContentType = builder.rootContentType;
    }

    public SiteTemplateId getId()
    {
        return id;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public String getVendorName()
    {
        return vendorName;
    }

    public String getVendorUrl()
    {
        return vendorUrl;
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

    public static Builder newSiteTemplate()
    {
        return new Builder();
    }

    public static class Builder
    {
        private SiteTemplateId id;

        private String displayName;

        private String description;

        private String vendorName;

        private String vendorUrl;

        private ModuleKeys modules = ModuleKeys.empty();

        private ContentTypeNames supportedContentTypes = ContentTypeNames.empty();

        private ContentTypeName rootContentType;

        private Builder()
        {
        }

        public Builder id( final SiteTemplateId id )
        {
            this.id = id;
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

        public Builder vendorName( final String vendorName )
        {
            this.vendorName = vendorName;
            return this;
        }

        public Builder vendorUrl( final String vendorUrl )
        {
            this.vendorUrl = vendorUrl;
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
}
