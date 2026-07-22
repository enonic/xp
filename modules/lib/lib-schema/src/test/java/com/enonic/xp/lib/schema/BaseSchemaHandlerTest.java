package com.enonic.xp.lib.schema;

import org.mockito.Mockito;

import com.enonic.xp.resource.SchemaService;
import com.enonic.xp.testing.ScriptTestSupport;

public abstract class BaseSchemaHandlerTest
    extends ScriptTestSupport
{
    protected SchemaService schemaService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();

        this.schemaService = Mockito.mock( SchemaService.class );
        addService( SchemaService.class, this.schemaService );
    }
}