package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.schema.UpdatePhrasesParams;

import static com.enonic.xp.lib.schema.CreatePhrasesHandlerTest.mockPhrasesResource;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

class UpdatePhrasesHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testUpdatePhrases()
    {
        when( schemaService.updatePhrases( isA( UpdatePhrasesParams.class ) ) ).thenAnswer( invocation -> {
            final UpdatePhrasesParams params = invocation.getArgument( 0, UpdatePhrasesParams.class );
            return mockPhrasesResource( params.getKey(), params.getName() + ".properties", params.getResource() );
        } );

        runScript( "/lib/xp/examples/schema/updatePhrases.js" );
    }
}
