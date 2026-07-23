package com.enonic.xp.lib.schema;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;

import static com.enonic.xp.lib.schema.CreateDynamicPhrasesHandlerTest.mockPhrasesResource;
import static org.mockito.Mockito.when;

class ListDynamicPhrasesHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testListPhrases()
    {
        final ApplicationKey key = ApplicationKey.from( "myapp" );

        when( schemaService.listPhrases( key ) ).thenReturn(
            List.of( mockPhrasesResource( key, "phrases.properties", "action.save=Save" ),
                     mockPhrasesResource( key, "phrases_no.properties", "action.save=Lagre" ) ) );

        runScript( "/lib/xp/examples/schema/listPhrases.js" );
    }
}
