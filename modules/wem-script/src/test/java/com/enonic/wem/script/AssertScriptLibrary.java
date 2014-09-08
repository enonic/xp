package com.enonic.wem.script;

import org.junit.Assert;

import com.enonic.wem.script.ScriptLibrary;

public final class AssertScriptLibrary
    implements ScriptLibrary
{
    @Override
    public String getName()
    {
        return "assert";
    }

    public void assertEquals( final Object expected, final Object actual )
    {
        Assert.assertEquals( expected, actual );
    }
}
