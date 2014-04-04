package com.enonic.wem.core.module.source;

import org.junit.Test;

public class SourceResolverImplTest
{
    @Test
    public void testResolveKey()
    {
        // mymodule-1.0.0:js/test.js
    }

    @Test
    public void testResolveKeySystem()
    {
        // system-0.0.0:js/test.js
    }

    @Test
    public void testResolveKeyNotFound()
    {
        // unknown-1.0.0:js/test.js
    }

    @Test
    public void testResolveWithBase()
    {
        // base -> mymodule-1.0.0:js/test.js
        // util/misc.js
    }

    @Test
    public void testResolveWithBaseRel()
    {
        // base -> mymodule-1.0.0:js/test.js
        // ./util/misc.js
    }

    @Test
    public void testResolveWithBaseFoundSystem()
    {
        // base -> mymodule-1.0.0:js/test.js
        // logging.js
    }

    @Test
    public void testResolveWithBaseNotFoundSystem()
    {
        // base -> mymodule-1.0.0:js/test.js
        // ./logging.js
        // -> not found
    }
}
