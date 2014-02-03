package com.enonic.wem.api.module;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.schema.content.ContentTypeNames;

public final class CreateModuleSpec
{
    private ModuleName name;

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

    public static CreateModuleSpec fromModule( Module module )
    {
        CreateModuleSpec createModule = new CreateModuleSpec();
        createModule.displayName( module.getDisplayName() );
        createModule.name = module.getName();
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

    public CreateModuleSpec name( final ModuleName name )
    {
        this.name = name;
        return this;
    }

    public CreateModuleSpec name( final String name )
    {
        this.name = ModuleName.from( name );
        return this;
    }

    public CreateModuleSpec version( final ModuleVersion version )
    {
        this.version = version;
        return this;
    }

    public CreateModuleSpec displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreateModuleSpec info( final String info )
    {
        this.info = info;
        return this;
    }

    public CreateModuleSpec url( final String url )
    {
        this.url = url;
        return this;
    }

    public CreateModuleSpec vendorName( final String vendorName )
    {
        this.vendorName = vendorName;
        return this;
    }

    public CreateModuleSpec vendorUrl( final String vendorUrl )
    {
        this.vendorUrl = vendorUrl;
        return this;
    }

    public CreateModuleSpec config( final Form config )
    {
        this.config = config;
        return this;
    }

    public CreateModuleSpec moduleDirectoryEntry( final ModuleFileEntry fileEntryRoot )
    {
        this.moduleDirectoryEntry = fileEntryRoot;
        return this;
    }

    public CreateModuleSpec minSystemVersion( final ModuleVersion version )
    {
        this.minSystemVersion = version;
        return this;
    }

    public CreateModuleSpec maxSystemVersion( final ModuleVersion version )
    {
        this.maxSystemVersion = version;
        return this;
    }

    public CreateModuleSpec moduleDependencies( final ModuleKeys moduleDependencies )
    {
        this.moduleDependencies = moduleDependencies;
        return this;
    }

    public CreateModuleSpec contentTypeDependencies( final ContentTypeNames contentTypeDependencies )
    {
        this.contentTypeDependencies = contentTypeDependencies;
        return this;
    }

    public ModuleName getName()
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

    public void validate()
    {
        Preconditions.checkNotNull( this.name, "name cannot be null" );
        Preconditions.checkNotNull( this.displayName, "displayName cannot be null" );
    }
}
