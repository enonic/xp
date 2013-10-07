package com.enonic.wem.api.module;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.schema.content.form.Form;

public final class Module
{
    private final ModuleVersion moduleVersion;

    private final String displayName;

    private final String info;

    private final String url;

    private final String vendorName;

    private final String vendorUrl;

    private final Form config;

    private final ModuleFileEntry resourcesRoot;

    private final Version minSystemVersion;

    private final Version maxSystemVersion;

    private final ModuleVersions moduleDependencies;

    private final QualifiedContentTypeNames contentTypeDependencies;

    private Module( final Module.Builder builder )
    {
        Preconditions.checkNotNull( builder.moduleVersion, "moduleVersion must be specified" );
        this.moduleVersion = builder.moduleVersion;
        this.displayName = builder.displayName;
        this.info = builder.info;
        this.url = builder.url;
        this.vendorName = builder.vendorName;
        this.vendorUrl = builder.vendorUrl;
        this.minSystemVersion = builder.minSystemVersion;
        this.maxSystemVersion = builder.maxSystemVersion;
        this.moduleDependencies = builder.moduleDependencies;
        this.contentTypeDependencies = builder.contentTypeDependencies;
        this.config = builder.config;
        this.resourcesRoot = builder.resourcesRoot.build();
    }

    public ModuleVersion getModuleVersion()
    {
        return moduleVersion;
    }

    public ModuleName getName()
    {
        return moduleVersion.getName();
    }

    public Version getVersion()
    {
        return moduleVersion.getVersion();
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

    public String getVendorName()
    {
        return vendorName;
    }

    public String getVendorUrl()
    {
        return vendorUrl;
    }

    public Form getConfig()
    {
        return config;
    }

    public Version getMinSystemVersion()
    {
        return minSystemVersion;
    }

    public Version getMaxSystemVersion()
    {
        return maxSystemVersion;
    }

    public ModuleVersions getModuleDependencies()
    {
        return moduleDependencies;
    }

    public QualifiedContentTypeNames getContentTypeDependencies()
    {
        return contentTypeDependencies;
    }

    public ModuleFileEntry getResourcesRoot()
    {
        return resourcesRoot;
    }

    void validate()
    {
        //TODO check for mandatory properties, -> generic interface?
    }

    public static Builder newModule()
    {
        return new Builder();
    }

    public static Builder newModule( final Module module )
    {
        return new Builder( module );
    }

    public static class Builder
    {
        private ModuleVersion moduleVersion;

        private String displayName;

        private String info;

        private String url;

        private String vendorName;

        private String vendorUrl;

        private Version minSystemVersion;

        private Version maxSystemVersion;

        private ModuleVersions moduleDependencies;

        private QualifiedContentTypeNames contentTypeDependencies;

        private Form config;

        private ModuleFileEntry.Builder resourcesRoot;

        private Builder()
        {
            this.resourcesRoot = ModuleFileEntry.directoryBuilder( "" );
        }

        private Builder( final Module source )
        {
            this.moduleVersion = source.moduleVersion;
            this.displayName = source.displayName;
            this.info = source.info;
            this.url = source.url;
            this.vendorName = source.vendorName;
            this.vendorUrl = source.vendorUrl;
            this.minSystemVersion = source.minSystemVersion;
            this.maxSystemVersion = source.maxSystemVersion;
            this.moduleDependencies = source.moduleDependencies;
            this.contentTypeDependencies = source.contentTypeDependencies;
            this.config = source.config;
            this.resourcesRoot = ModuleFileEntry.copyOf( source.resourcesRoot );
        }

        public Builder moduleVersion( final ModuleVersion moduleVersion )
        {
            this.moduleVersion = moduleVersion;
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

        public Builder minSystemVersion( final Version minSystemVersion )
        {
            this.minSystemVersion = minSystemVersion;
            return this;
        }

        public Builder maxSystemVersion( final Version maxSystemVersion )
        {
            this.maxSystemVersion = maxSystemVersion;
            return this;
        }

        public Builder config( final Form config )
        {
            this.config = config;
            return this;
        }

        public Builder addFileEntry( final ModuleFileEntry resourcesRoot )
        {
            this.resourcesRoot.add( resourcesRoot );
            return this;
        }

        public Module build()
        {
            return new Module( this );
        }
    }

}
