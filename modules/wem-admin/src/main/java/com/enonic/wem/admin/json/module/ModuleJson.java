package com.enonic.wem.admin.json.module;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.admin.json.form.FormJson;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentTypeName;

public class ModuleJson
    extends ModuleSummaryJson
{
    private final FormJson config;

    private final ImmutableList<ModuleKeyJson> moduleDependencies;

    private final ImmutableList<ContentTypeNameJson> contentTypeDependencies;

    private final ModuleFileEntryJson moduleDirectoryEntry;

    public ModuleJson( final Module module )
    {
        super( module );

        this.config = module.getConfig() != null ? new FormJson( module.getConfig() ) : null;


        final ImmutableList.Builder<ModuleKeyJson> mBuilder = ImmutableList.builder();
        for ( final ModuleKey moduleKey : module.getModuleDependencies() )
        {
            mBuilder.add( new ModuleKeyJson( moduleKey ) );
        }
        this.moduleDependencies = mBuilder.build();


        final ImmutableList.Builder<ContentTypeNameJson> cBuilder = ImmutableList.builder();
        for ( final ContentTypeName contentTypeName : module.getContentTypeDependencies() )
        {
            cBuilder.add( new ContentTypeNameJson( contentTypeName ) );
        }
        this.contentTypeDependencies = cBuilder.build();

        this.moduleDirectoryEntry = module.getModuleDirectoryEntry() != null ? new ModuleFileEntryJson( module.getModuleDirectoryEntry() ) : null;
    }

    public FormJson getConfig()
    {
        return config;
    }

    public ImmutableList<ModuleKeyJson> getModuleDependencies()
    {
        return moduleDependencies;
    }

    public ImmutableList<ContentTypeNameJson> getContentTypeDependencies()
    {
        return contentTypeDependencies;
    }

    public ModuleFileEntryJson getModuleDirectoryEntry()
    {
        return moduleDirectoryEntry;
    }

    public String getMinSystemVersion()
    {
        return module.getMinSystemVersion() != null ? module.getMinSystemVersion().toString() : null;
    }

    public String getMaxSystemVersion()
    {
        return module.getMaxSystemVersion() != null ? module.getMaxSystemVersion().toString() : null;
    }
}
