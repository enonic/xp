package com.enonic.xp.lib.content;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.DuplicateContentsResult;

class DuplicateContentHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    void testExample()
    {
        mockVariant();

        runScript( "/lib/xp/examples/content/duplicate.js" );
    }

    @Test
    void testDuplicate()
    {
        final Content content = Content.create( TestDataFixtures.newExampleContent() ).variantOf( ContentId.from( "9876543210" ) ).build();

        Mockito.when( contentService.duplicate( Mockito.any( DuplicateContentParams.class ) ) )
            .thenReturn( DuplicateContentsResult.create()
                             .setContentName( content.getName().toString() )
                             .setSourceContentPath( content.getPath() )
                             .addDuplicated( content.getId() )
                             .build() );

        runFunction( "/test/DuplicateContentHandlerTest.js", "duplicate" );
    }

    @Test
    void testDuplicateAsVariant()
    {
        mockVariant();

        runFunction( "/test/DuplicateContentHandlerTest.js", "duplicateAsVariant" );
    }

    private void mockVariant()
    {
        final Content source = TestDataFixtures.newExampleContent();
        final Content content = Content.create( source )
            .name( "variant-name" )
            .path( source.getParentPath() + "/variant-name" )
            .variantOf( ContentId.from( "9876543210" ) )
            .build();

        Mockito.when( contentService.duplicate( Mockito.any( DuplicateContentParams.class ) ) )
            .thenReturn( DuplicateContentsResult.create()
                             .setContentName( content.getName().toString() )
                             .setSourceContentPath( content.getPath() )
                             .addDuplicated( content.getId() )
                             .build() );
    }

}
