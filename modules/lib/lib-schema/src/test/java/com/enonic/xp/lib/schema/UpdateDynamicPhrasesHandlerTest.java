package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.resource.UpdateDynamicPhrasesParams;

import static com.enonic.xp.lib.schema.CreateDynamicPhrasesHandlerTest.mockPhrasesResource;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

class UpdateDynamicPhrasesHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testUpdatePhrases()
    {
        when( schemaService.updatePhrases( isA( UpdateDynamicPhrasesParams.class ) ) ).thenAnswer( invocation -> {
            final UpdateDynamicPhrasesParams params = invocation.getArgument( 0, UpdateDynamicPhrasesParams.class );
            return mockPhrasesResource( params.getKey(), params.getName() + ".properties", params.getResource() );
        } );

        runScript( "/lib/xp/examples/schema/updatePhrases.js" );
    }
}
