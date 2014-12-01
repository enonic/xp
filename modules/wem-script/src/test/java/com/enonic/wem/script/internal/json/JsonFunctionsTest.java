package com.enonic.wem.script.internal.json;

import javax.script.Bindings;

import org.junit.Test;

import com.enonic.wem.script.internal.NashornScriptTest;

public class JsonFunctionsTest
    extends NashornScriptTest
{
    @Override
    protected void configure( final Bindings bindings )
    {
        new JsonFunctions().register( bindings );
    }

    @Test
    public void testStringify()
        throws Exception
    {
        execute( "stringify" );
    }

    @Test
    public void testParse()
        throws Exception
    {
        execute( "parse" );
    }
}
