package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.Namespace;
import com.enonic.xp.app.UpdateNamespaceParams;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

class UpdateNamespaceHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testUpdateNamespace()
    {
        when( schemaService.updateNamespace( isA( UpdateNamespaceParams.class ) ) ).thenAnswer( invocation -> {
            final UpdateNamespaceParams params = invocation.getArgument( 0, UpdateNamespaceParams.class );
            return Namespace.create().key( params.getKey() ).description( params.getDescription() ).build();
        } );

        runScript( "/lib/xp/examples/schema/updateNamespace.js" );
    }
}