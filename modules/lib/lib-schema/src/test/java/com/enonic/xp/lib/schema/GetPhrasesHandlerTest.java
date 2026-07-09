package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.schema.GetPhrasesParams;

import static com.enonic.xp.lib.schema.CreatePhrasesHandlerTest.mockPhrasesResource;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

class GetPhrasesHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testGetPhrases()
    {
        when( schemaService.getPhrases( isA( GetPhrasesParams.class ) ) ).thenAnswer( invocation -> {
            final GetPhrasesParams params = invocation.getArgument( 0, GetPhrasesParams.class );
            return mockPhrasesResource( params.getKey(), params.getName() + ".properties", "action.save=Save" );
        } );

        runScript( "/lib/xp/examples/schema/getPhrases.js" );
    }
}
