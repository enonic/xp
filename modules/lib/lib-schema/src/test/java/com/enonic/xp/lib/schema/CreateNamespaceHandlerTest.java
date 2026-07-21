package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.CreateNamespaceParams;
import com.enonic.xp.app.Namespace;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

class CreateNamespaceHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testCreateNamespace()
    {
        when( applicationService.createNamespace( isA( CreateNamespaceParams.class ) ) ).thenAnswer( params -> {
            final CreateNamespaceParams createParams = params.getArgument( 0, CreateNamespaceParams.class );

            return Namespace.create().key( createParams.getKey() ).description( createParams.getDescription() ).build();
        } );

        runScript( "/lib/xp/examples/schema/createNamespace.js" );
    }
}
