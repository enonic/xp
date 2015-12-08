package com.enonic.xp.lib.io;

import org.junit.Test;

import com.enonic.xp.testing.script.ScriptTestSupport;

public class IOLibTest
    extends ScriptTestSupport
{
    @Test
    public void testReadText()
    {
        runFunction( "/site/test/io-test.js", "testReadText" );
    }

    @Test
    public void testReadLines()
    {
        runFunction( "/site/test/io-test.js", "testReadLines" );
    }

    @Test
    public void testProcessLines()
    {
        runFunction( "/site/test/io-test.js", "testProcessLines" );
    }

    @Test
    public void testGetSize()
    {
        runFunction( "/site/test/io-test.js", "testGetSize" );
    }

    @Test
    public void testGetMimeType()
    {
        runFunction( "/site/test/io-test.js", "testGetMimeType" );
    }

    @Test
    public void testGetResource()
    {
        runFunction( "/site/test/io-test.js", "testGetResource" );
    }

    @Test
    public void testExample_readText()
    {
        runScript( "/site/lib/xp/examples/readText.js" );
    }

    @Test
    public void testExample_readLines()
    {
        runScript( "/site/lib/xp/examples/readLines.js" );
    }

    @Test
    public void testExample_processLines()
    {
        runScript( "/site/lib/xp/examples/processLines.js" );
    }

    @Test
    public void testExample_getSize()
    {
        runScript( "/site/lib/xp/examples/getSize.js" );
    }

    @Test
    public void testExample_getMimeType()
    {
        runScript( "/site/lib/xp/examples/getMimeType.js" );
    }

    @Test
    public void testExample_getResource()
    {
        runScript( "/site/lib/xp/examples/getResource.js" );
    }
}
