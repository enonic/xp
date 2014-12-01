package com.enonic.wem.script.internal.logger;

import javax.script.Bindings;

import org.junit.Test;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.internal.NashornScriptTest;

public class ScriptLoggerTest
    extends NashornScriptTest
{
    @Override
    protected void configure( final Bindings bindings )
    {
        new ScriptLogger( ResourceKey.from( "mymodule:/some/script.js" ) ).register( bindings );
    }

    @Test
    public void testLog()
        throws Exception
    {
        execute( "log" );
    }

    @Test
    public void testFormat()
        throws Exception
    {
        execute( "format" );
    }
}
