package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.descriptor.DescriptorKey;

import static org.mockito.Mockito.when;

class DeleteComponentHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testPart()
    {
        when( schemaService.deletePart( DescriptorKey.from( "myapp:mypart" ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deletePart.js" );

        Mockito.verify( schemaService ).deletePart( DescriptorKey.from( "myapp:mypart" ) );
    }

    @Test
    void testLayout()
    {
        when( schemaService.deleteLayout( DescriptorKey.from( "myapp:mylayout" ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deleteLayout.js" );

        Mockito.verify( schemaService ).deleteLayout( DescriptorKey.from( "myapp:mylayout" ) );
    }

    @Test
    void testPage()
    {
        when( schemaService.deletePage( DescriptorKey.from( "myapp:mypage" ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/schema/deletePage.js" );

        Mockito.verify( schemaService ).deletePage( DescriptorKey.from( "myapp:mypage" ) );
    }

}