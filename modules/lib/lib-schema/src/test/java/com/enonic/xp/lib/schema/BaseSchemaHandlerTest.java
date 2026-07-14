package com.enonic.xp.lib.schema;

import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.testing.ScriptTestSupport;

public abstract class BaseSchemaHandlerTest
    extends ScriptTestSupport
{
    protected DynamicSchemaService dynamicSchemaService;

    protected ApplicationService applicationService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();

        this.dynamicSchemaService = Mockito.mock( DynamicSchemaService.class );
        addService( DynamicSchemaService.class, this.dynamicSchemaService );

        this.applicationService = Mockito.mock( ApplicationService.class );
        addService( ApplicationService.class, this.applicationService );
    }
}
