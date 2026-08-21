package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.Namespace;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

class GetNamespaceHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testGetNamespace()
    {
        when( schemaService.getNamespace( isA( ApplicationKey.class ) ) ).thenAnswer(
            params -> Namespace.create().key( params.getArgument( 0, ApplicationKey.class ) ).description( "My namespace" ).build() );

        runScript( "/lib/xp/examples/schema/getNamespace.js" );
    }
}
