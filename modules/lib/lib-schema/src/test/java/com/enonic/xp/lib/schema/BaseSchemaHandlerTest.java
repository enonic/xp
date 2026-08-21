package com.enonic.xp.lib.schema;

import java.nio.charset.StandardCharsets;

import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.schema.SchemaService;
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

    public ByteSource createByteSource( final String value )
    {
        return ByteSource.wrap( value.getBytes( StandardCharsets.UTF_8 ) );
    }
}