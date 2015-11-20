package com.enonic.xp.lib.cache;

import org.junit.Test;

import com.enonic.xp.testing.script.ScriptTestSupport;

public class CacheLibTest
    extends ScriptTestSupport
{
    @Test
    public void testCache()
    {
        runScript( "/site/test/cache.js" );
    }
}
