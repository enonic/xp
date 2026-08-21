package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.schema.DeletePhrasesParams;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

class DeletePhrasesHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testDeletePhrases()
    {
        when( schemaService.deletePhrases( isA( DeletePhrasesParams.class ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deletePhrases.js" );
    }
}
