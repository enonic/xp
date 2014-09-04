package com.enonic.wem.script.internal.v2;

import org.junit.Assert;

import com.enonic.wem.script.ScriptLibrary;

public final class AssertScriptLibrary
    implements ScriptLibrary
{
    @Override
    public String getName()
    {
        return "lib/assert";
    }

    public void assertEquals( final Object expected, final Object actual )
    {
        Assert.assertEquals( expected, actual );
    }
}
