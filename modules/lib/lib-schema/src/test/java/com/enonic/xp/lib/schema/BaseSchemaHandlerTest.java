package com.enonic.xp.lib.schema;

import java.nio.charset.StandardCharsets;

import org.mockito.Mockito;

import com.google.common.io.ByteSource;

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

    public ByteSource createByteSource( final String value )
    {
        return ByteSource.wrap( value.getBytes( StandardCharsets.UTF_8 ) );
    }
}
