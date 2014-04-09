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

    private final ImmutableList<String> moduleDependencies;

    private final ImmutableList<String> contentTypeDependencies;

    public ModuleJson( final Module module )
    {
        super( module );

        this.config = module.getConfig() != null ? new FormJson( module.getConfig() ) : null;

        final ImmutableList.Builder<String> mBuilder = ImmutableList.builder();
        for ( final ModuleKey moduleKey : module.getModuleDependencies() )
        {
            mBuilder.add( moduleKey.toString() );
        }
        this.moduleDependencies = mBuilder.build();

        final ImmutableList.Builder<String> cBuilder = ImmutableList.builder();
        for ( final ContentTypeName contentTypeName : module.getContentTypeDependencies() )
        {
            cBuilder.add( contentTypeName.toString() );
        }
        this.contentTypeDependencies = cBuilder.build();
    }

    public FormJson getConfig()
    {
        return config;
    }

    public ImmutableList<String> getModuleDependencies()
    {
        return moduleDependencies;
    }

    public ImmutableList<String> getContentTypeDependencies()
    {
        return contentTypeDependencies;
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
