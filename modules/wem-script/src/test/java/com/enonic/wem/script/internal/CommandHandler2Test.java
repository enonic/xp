package com.enonic.wem.script.internal;

import org.junit.Test;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.AbstractScriptTest;

public class CommandHandler2Test
    extends AbstractScriptTest
{
    @Test
    public void executeCommand()
    {
        final ResourceKey script = ResourceKey.from( "mymodule:/command/command-v2-test.js" );
        runTestScript( script );
    }
}
