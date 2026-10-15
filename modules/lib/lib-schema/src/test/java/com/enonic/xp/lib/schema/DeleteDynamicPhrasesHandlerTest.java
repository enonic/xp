package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.DeleteDynamicPhrasesParams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteDynamicPhrasesHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testDeletePhrases()
    {
        when( dynamicSchemaService.deletePhrases( isA( DeleteDynamicPhrasesParams.class ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deletePhrases.js" );

        final ArgumentCaptor<DeleteDynamicPhrasesParams> captor = ArgumentCaptor.forClass( DeleteDynamicPhrasesParams.class );
        verify( dynamicSchemaService ).deletePhrases( captor.capture() );
        assertEquals( ApplicationKey.from( "myapp" ), captor.getValue().getKey() );
        assertEquals( "phrases_en", captor.getValue().getName() );
    }
}
