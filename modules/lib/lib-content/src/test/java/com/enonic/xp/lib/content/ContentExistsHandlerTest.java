package com.enonic.xp.lib.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;

public class ContentExistsHandlerTest
    extends BaseContentHandlerTest
{

    @Test
    public void testExistsById()
    {
        final Content content = TestDataFixtures.newExampleContent();
        Mockito.when( this.contentService.contentExists( content.getId() ) ).thenReturn( true );

        runFunction( "/test/ContentExistsHandlerTest.js", "existsById" );
    }

    @Test
    public void testExistsByPath()
        throws Exception
    {
        final Content content = TestDataFixtures.newExampleContent();
        Mockito.when( this.contentService.contentExists( content.getPath() ) ).thenReturn( true );

        runScript( "/lib/xp/examples/content/exists.js" );
    }

    @Test
    public void testEmptyKey()
        throws Exception
    {
        final Content content = TestDataFixtures.newExampleContent();

        runFunction( "/test/ContentExistsHandlerTest.js", "emptyKey" );
    }

}
