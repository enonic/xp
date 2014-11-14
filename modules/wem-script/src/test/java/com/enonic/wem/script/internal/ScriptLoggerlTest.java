package com.enonic.wem.script.internal;

import org.junit.Test;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.AbstractScriptTest;

public class ScriptLoggerlTest
    extends AbstractScriptTest
{
    @Test
    public void testLogging()
    {
        final ResourceKey script = ResourceKey.from( "mymodule:/log/log-test.js" );
        runTestScript( script );
    }

    @Test
    public void testFormat()
    {
        final ResourceKey script = ResourceKey.from( "mymodule:/log/format-test.js" );
        runTestScript( script );
    }
}
