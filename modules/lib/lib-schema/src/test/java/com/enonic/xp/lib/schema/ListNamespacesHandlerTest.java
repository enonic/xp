package com.enonic.xp.lib.schema;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.Namespace;

import static org.mockito.Mockito.when;

class ListNamespacesHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testListNamespaces()
    {
        when( schemaService.listNamespaces() ).thenReturn(
            List.of( Namespace.create().key( ApplicationKey.from( "myapp1" ) ).description( "My namespace 1" ).build(),
                     Namespace.create().key( ApplicationKey.from( "myapp2" ) ).build() ) );

        runScript( "/lib/xp/examples/schema/listNamespaces.js" );
    }
}
