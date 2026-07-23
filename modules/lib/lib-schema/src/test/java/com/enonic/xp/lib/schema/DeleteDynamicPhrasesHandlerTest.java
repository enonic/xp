package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.resource.DeleteDynamicPhrasesParams;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

class DeleteDynamicPhrasesHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testDeletePhrases()
    {
        when( schemaService.deletePhrases( isA( DeleteDynamicPhrasesParams.class ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deletePhrases.js" );
    }
}
