package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.CreateNamespaceParams;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

class CreateNamespaceHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testCreateNamespace()
    {
        when( applicationService.createNamespace( isA( CreateNamespaceParams.class ) ) ).thenAnswer( params -> {
            final ApplicationKey key = params.getArgument( 0, CreateNamespaceParams.class ).getKey();

            final Application application = Mockito.mock( Application.class );
            when( application.getKey() ).thenReturn( key );

            return application;
        } );

        runScript( "/lib/xp/examples/schema/createNamespace.js" );
    }
}
