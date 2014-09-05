package com.enonic.wem.core.schema;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.schema.SchemaProvider;
import com.enonic.wem.api.schema.Schemas;

public final class ModuleSchemaProvider
    implements SchemaProvider
{
    private final Module module;

    public ModuleSchemaProvider( final Module module )
    {
        this.module = module;
    }

    @Override
    public Schemas getSchemas()
    {
        return loadSchemas();
    }

    private Schemas loadSchemas()
    {
        return new SchemaLoader().loadSchemas( module );
    }

}
