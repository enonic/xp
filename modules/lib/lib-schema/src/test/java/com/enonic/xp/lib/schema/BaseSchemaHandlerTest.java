package com.enonic.xp.lib.schema;

import org.mockito.Mockito;

import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.testing.ScriptTestSupport;

public abstract class BaseSchemaHandlerTest
    extends ScriptTestSupport
{
    protected DynamicSchemaService dynamicSchemaService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();

        this.dynamicSchemaService = Mockito.mock( DynamicSchemaService.class );

        addService( DynamicSchemaService.class, this.dynamicSchemaService );
    }
}
