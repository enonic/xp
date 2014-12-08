package com.enonic.wem.jsapi.internal;

import org.junit.Before;

import junit.framework.Assert;

import com.enonic.wem.script.AbstractScriptTest;
import com.enonic.wem.script.ScriptExports;
import com.enonic.wem.script.command.CommandHandler;

public abstract class AbstractHandlerTest
    extends AbstractScriptTest
{
    @Before
    public final void setup()
        throws Exception
    {
        addHandler( createHandler() );
    }

    protected abstract CommandHandler createHandler()
        throws Exception;

    protected void execute( final String name )
        throws Exception
    {
        final String path = getClass().getName().replace( '.', '/' ) + ".js";
        final ScriptExports exports = runTestScript( path );

        Assert.assertTrue( "No functions exported named [" + name + "]", exports.hasMethod( name ) );
        exports.executeMethod( name );
    }
}
