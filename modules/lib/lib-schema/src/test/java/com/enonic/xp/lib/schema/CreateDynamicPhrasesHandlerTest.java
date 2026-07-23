package com.enonic.xp.lib.schema;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.CreateDynamicPhrasesParams;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateDynamicPhrasesHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testCreatePhrases()
    {
        when( schemaService.createPhrases( isA( CreateDynamicPhrasesParams.class ) ) ).thenAnswer( invocation -> {
            final CreateDynamicPhrasesParams params = invocation.getArgument( 0, CreateDynamicPhrasesParams.class );
            return mockPhrasesResource( params.getKey(), params.getName() + ".properties", params.getResource() );
        } );

        runScript( "/lib/xp/examples/schema/createPhrases.js" );
    }

    static Resource mockPhrasesResource( final ApplicationKey key, final String fileName, final String content )
    {
        final Resource resource = mock( Resource.class );
        when( resource.getKey() ).thenReturn( ResourceKey.from( key, "/cms/i18n/phrases/" + fileName ) );
        when( resource.getTimestamp() ).thenReturn( Instant.parse( "2021-09-25T10:00:00.00Z" ).toEpochMilli() );
        when( resource.readString() ).thenReturn( content );
        return resource;
    }
}
