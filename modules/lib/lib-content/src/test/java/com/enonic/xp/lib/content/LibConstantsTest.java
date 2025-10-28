package com.enonic.xp.lib.content;

import org.junit.jupiter.api.Test;

class LibConstantsTest
    extends BaseContentHandlerTest
{

    @Test
    void testArchiveRootPath()
    {
        runFunction( "/test/LibConstantsTest.js", "archiveRootPath" );
    }

    @Test
    void testContentRootPath()
    {
        runFunction( "/test/LibConstantsTest.js", "contentRootPath" );
    }
}
