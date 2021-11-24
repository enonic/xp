package com.enonic.xp.lib.content;

import org.junit.jupiter.api.Test;

public class LibConstantsTest
    extends BaseContentHandlerTest
{

    @Test
    public void testArchiveRootPath()
    {
        runFunction( "/test/LibConstantsTest.js", "archiveRootPath" );
    }

    @Test
    public void testContentRootPath()
    {
        runFunction( "/test/LibConstantsTest.js", "contentRootPath" );
    }
}
