package com.enonic.xp.lib.content;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;

class ContentExistsHandlerTest
    extends BaseContentHandlerTest
{

    @Test
    void testExistsById()
    {
        final Content content = TestDataFixtures.newExampleContent();
        Mockito.when( this.contentService.contentExists( content.getId() ) ).thenReturn( true );

        runFunction( "/test/ContentExistsHandlerTest.js", "existsById" );
    }

    @Test
    void testExistsByPath()
    {
        final Content content = TestDataFixtures.newExampleContent();
        Mockito.when( this.contentService.contentExists( content.getPath() ) ).thenReturn( true );

        runScript( "/lib/xp/examples/content/exists.js" );
    }

    @Test
    void testEmptyKey()
    {
        final Content content = TestDataFixtures.newExampleContent();

        runFunction( "/test/ContentExistsHandlerTest.js", "emptyKey" );
    }

}
