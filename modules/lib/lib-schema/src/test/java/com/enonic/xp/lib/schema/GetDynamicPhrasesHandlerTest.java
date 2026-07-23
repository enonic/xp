package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.resource.GetDynamicPhrasesParams;

import static com.enonic.xp.lib.schema.CreateDynamicPhrasesHandlerTest.mockPhrasesResource;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

class GetDynamicPhrasesHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testGetPhrases()
    {
        when( schemaService.getPhrases( isA( GetDynamicPhrasesParams.class ) ) ).thenAnswer( invocation -> {
            final GetDynamicPhrasesParams params = invocation.getArgument( 0, GetDynamicPhrasesParams.class );
            return mockPhrasesResource( params.getKey(), params.getName() + ".properties", "action.save=Save" );
        } );

        runScript( "/lib/xp/examples/schema/getPhrases.js" );
    }
}
