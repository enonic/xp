package com.enonic.wem.core.schema.metadata;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.schema.metadata.MetadataProvider;
import com.enonic.wem.api.schema.metadata.MetadataSchemas;

public final class ModuleMetadataProvider
    implements MetadataProvider
{
    private final Module module;

    public ModuleMetadataProvider( final Module module )
    {
        this.module = module;
    }

    @Override
    public MetadataSchemas get()
    {
        return new MetadataLoader().loadMetadatas( module );
    }

}
