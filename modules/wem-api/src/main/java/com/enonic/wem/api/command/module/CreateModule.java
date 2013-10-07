package com.enonic.wem.api.command.module;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleFileEntry;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.schema.content.form.Form;

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

    private ModuleFileEntry resourcesRoot;

    private ModuleVersion minSystemVersion;

    private ModuleVersion maxSystemVersion;

    private ModuleKeys moduleDependencies;

    private QualifiedContentTypeNames contentTypeDependencies;

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
        createModule.resourcesRoot = module.getResourcesRoot();
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

    public CreateModule resourcesRoot( final ModuleFileEntry fileEntryRoot )
    {
        this.resourcesRoot = resourcesRoot;
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

    public CreateModule contentTypeDependencies( final QualifiedContentTypeNames contentTypeDependencies )
    {
        this.contentTypeDependencies = contentTypeDependencies;
        return this;
    }

    String getName()
    {
        return name;
    }

    ModuleVersion getVersion()
    {
        return version;
    }

    String getDisplayName()
    {
        return displayName;
    }

    String getInfo()
    {
        return info;
    }

    String getUrl()
    {
        return url;
    }

    String getVendorName()
    {
        return vendorName;
    }

    String getVendorUrl()
    {
        return vendorUrl;
    }

    Form getConfig()
    {
        return config;
    }

    ModuleFileEntry getResourcesRoot()
    {
        return resourcesRoot;
    }

    ModuleVersion getMinSystemVersion()
    {
        return minSystemVersion;
    }

    ModuleVersion getMaxSystemVersion()
    {
        return maxSystemVersion;
    }

    ModuleKeys getModuleDependencies()
    {
        return moduleDependencies;
    }

    QualifiedContentTypeNames getContentTypeDependencies()
    {
        return contentTypeDependencies;
    }

    @Override
    public void validate()
    {

    }
}
