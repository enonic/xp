package com.enonic.wem.api.module;

import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.wem.api.Identity;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;

@Immutable
public final class Module
    implements Identity<ModuleKey>
{
    private final ModuleKey moduleKey;

    private final String displayName;

    private final String info;

    private final String url;

    private final String vendorName;

    private final String vendorUrl;

    private final Form config;

    private final ModuleFileEntry moduleDirectoryEntry;

    private final ModuleVersion minSystemVersion;

    private final ModuleVersion maxSystemVersion;

    private final ModuleKeys moduleDependencies;

    private final ContentTypeNames contentTypeDependencies;

    private Module( final Module.Builder builder )
    {
        Preconditions.checkNotNull( builder.moduleKey, "moduleKey must be specified" );
        builder.moduleDirectoryEntry.name( builder.moduleKey.toString() );

        this.moduleKey = builder.moduleKey;
        this.displayName = builder.displayName;
        this.info = builder.info;
        this.url = builder.url;
        this.vendorName = builder.vendorName;
        this.vendorUrl = builder.vendorUrl;
        this.minSystemVersion = builder.minSystemVersion;
        this.maxSystemVersion = builder.maxSystemVersion;
        this.moduleDependencies = ModuleKeys.from( builder.moduleDependencies );
        this.contentTypeDependencies = ContentTypeNames.from( builder.contentTypeDependencies );
        this.config = builder.config == null ? null : builder.config.copy();
        this.moduleDirectoryEntry = builder.moduleDirectoryEntry.build();
    }

    public ModuleKey getKey()
    {
        return getModuleKey();
    }

    public ModuleKey getModuleKey()
    {
        return moduleKey;
    }

    public ModuleName getName()
    {
        return moduleKey.getName();
    }

    public ModuleVersion getVersion()
    {
        return moduleKey.getVersion();
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

    public ModuleVersion getMinSystemVersion()
    {
        return minSystemVersion;
    }

    public ModuleVersion getMaxSystemVersion()
    {
        return maxSystemVersion;
    }

    public ModuleKeys getModuleDependencies()
    {
        return moduleDependencies;
    }

    public ContentTypeNames getContentTypeDependencies()
    {
        return contentTypeDependencies;
    }

    public ModuleFileEntry getModuleDirectoryEntry()
    {
        return moduleDirectoryEntry;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper( this ).
            add( "moduleKey", moduleKey ).
            add( "displayName", displayName ).
            add( "info", info ).
            add( "url", url ).
            add( "vendorName", vendorName ).
            add( "vendorUrl", vendorUrl ).
            add( "config", config ).
            add( "minSystemVersion", minSystemVersion ).
            add( "maxSystemVersion", maxSystemVersion ).
            add( "moduleDependencies", moduleDependencies ).
            add( "contentTypeDependencies", contentTypeDependencies ).
            add( "directoryEntry", moduleDirectoryEntry ).
            omitNullValues().
            toString();
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
        private ModuleKey moduleKey;

        private String displayName;

        private String info;

        private String url;

        private String vendorName;

        private String vendorUrl;

        private ModuleVersion minSystemVersion;

        private ModuleVersion maxSystemVersion;

        private List<ModuleKey> moduleDependencies = Lists.newArrayList();

        private Set<ContentTypeName> contentTypeDependencies = Sets.newHashSet();

        private Form config;

        private final ModuleFileEntry.Builder moduleDirectoryEntry;

        private Builder()
        {
            this.moduleDirectoryEntry = ModuleFileEntry.newModuleDirectory( "" );
        }

        private Builder( final Module source )
        {
            this.moduleKey = source.moduleKey;
            this.displayName = source.displayName;
            this.info = source.info;
            this.url = source.url;
            this.vendorName = source.vendorName;
            this.vendorUrl = source.vendorUrl;
            this.minSystemVersion = source.minSystemVersion;
            this.maxSystemVersion = source.maxSystemVersion;
            this.moduleDependencies.addAll( source.moduleDependencies.getList() );
            this.contentTypeDependencies.addAll( source.contentTypeDependencies.getSet() );
            this.config = source.config;
            this.moduleDirectoryEntry = ModuleFileEntry.copyOf( source.moduleDirectoryEntry );
        }

        public Builder moduleKey( final ModuleKey moduleKey )
        {
            this.moduleKey = moduleKey;
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

        public Builder minSystemVersion( final ModuleVersion minSystemVersion )
        {
            this.minSystemVersion = minSystemVersion;
            return this;
        }

        public Builder maxSystemVersion( final ModuleVersion maxSystemVersion )
        {
            this.maxSystemVersion = maxSystemVersion;
            return this;
        }

        public Builder addModuleDependency( final ModuleKey moduleDependency )
        {
            this.moduleDependencies.add( moduleDependency );
            return this;
        }

        public Builder addModuleDependencies( final Iterable<ModuleKey> moduleDependencies )
        {
            Iterables.addAll( this.moduleDependencies, moduleDependencies );
            return this;
        }

        public Builder addContentTypeDependency( final ContentTypeName contentTypeDependency )
        {
            this.contentTypeDependencies.add( contentTypeDependency );
            return this;
        }

        public Builder addContentTypeDependencies( final Iterable<ContentTypeName> contentTypeDependencies )
        {
            Iterables.addAll( this.contentTypeDependencies, contentTypeDependencies );
            return this;
        }

        public Builder config( final Form config )
        {
            this.config = config;
            return this;
        }

        public ModuleFileEntry.Builder getModuleDirectoryEntry()
        {
            return this.moduleDirectoryEntry;
        }

        public Builder addFileEntry( final ModuleFileEntry resourcesRoot )
        {
            this.moduleDirectoryEntry.addEntry( resourcesRoot );
            return this;
        }

        public Builder removeFileEntry( final String entryName )
        {
            this.moduleDirectoryEntry.remove( entryName );
            return this;
        }

        public Builder removeFileEntries()
        {
            this.moduleDirectoryEntry.removeAll();
            return this;
        }

        public Module build()
        {
            return new Module( this );
        }
    }

}
