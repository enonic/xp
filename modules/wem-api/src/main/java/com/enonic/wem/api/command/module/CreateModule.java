package com.enonic.wem.api.command.module;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleFileEntry;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.api.schema.content.ContentTypeNames;

public final class CreateModule
    extends Command<Module>
{
    private String name;

    private ModuleVersion version;

    private String displayName;

    private String info;

    private String url;

    private String vendorName;

    private String vendorUrl;

    private Form config;

    private ModuleFileEntry moduleDirectoryEntry;

    private ModuleVersion minSystemVersion;

    private ModuleVersion maxSystemVersion;

    private ModuleKeys moduleDependencies;

    private ContentTypeNames contentTypeDependencies;

    public static CreateModule fromModule( Module module )
    {
        CreateModule createModule = new CreateModule();
        createModule.displayName( module.getDisplayName() );
        createModule.name = module.getName().toString();
        createModule.version = module.getVersion();
        createModule.displayName = module.getDisplayName();
        createModule.info = module.getInfo();
        createModule.url = module.getUrl();
        createModule.vendorName = module.getVendorName();
        createModule.vendorUrl = module.getVendorUrl();
        createModule.config = module.getConfig();
        createModule.moduleDirectoryEntry = module.getModuleDirectoryEntry();
        createModule.minSystemVersion = module.getMinSystemVersion();
        createModule.maxSystemVersion = module.getMaxSystemVersion();
        createModule.moduleDependencies = module.getModuleDependencies();
        createModule.contentTypeDependencies = module.getContentTypeDependencies();
        return createModule;
    }

    public CreateModule name( final String name )
    {
        this.name = name;
        return this;
    }

    public CreateModule version( final ModuleVersion version )
    {
        this.version = version;
        return this;
    }

    public CreateModule displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreateModule info( final String info )
    {
        this.info = info;
        return this;
    }

    public CreateModule url( final String url )
    {
        this.url = url;
        return this;
    }

    public CreateModule vendorName( final String vendorName )
    {
        this.vendorName = vendorName;
        return this;
    }

    public CreateModule vendorUrl( final String vendorUrl )
    {
        this.vendorUrl = vendorUrl;
        return this;
    }

    public CreateModule config( final Form config )
    {
        this.config = config;
        return this;
    }

    public CreateModule moduleDirectoryEntry( final ModuleFileEntry fileEntryRoot )
    {
        this.moduleDirectoryEntry = fileEntryRoot;
        return this;
    }

    public CreateModule minSystemVersion( final ModuleVersion version )
    {
        this.minSystemVersion = version;
        return this;
    }

    public CreateModule maxSystemVersion( final ModuleVersion version )
    {
        this.maxSystemVersion = version;
        return this;
    }

    public CreateModule moduleDependencies( final ModuleKeys moduleDependencies )
    {
        this.moduleDependencies = moduleDependencies;
        return this;
    }

    public CreateModule contentTypeDependencies( final ContentTypeNames contentTypeDependencies )
    {
        this.contentTypeDependencies = contentTypeDependencies;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public ModuleVersion getVersion()
    {
        return version;
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

    public ModuleFileEntry getModuleDirectoryEntry()
    {
        return moduleDirectoryEntry;
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

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.name, "name cannot be null" );
        Preconditions.checkNotNull( this.displayName, "displayName cannot be null" );
    }
}
