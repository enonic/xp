package com.enonic.wem.script.internal.nashorn;

import javax.script.ScriptEngine;

import org.junit.Test;

import junit.framework.Assert;

public class NashornHelperTest
{
    @Test
    public void getScriptEngine()
    {
        final ScriptEngine engine = NashornHelper.getScriptEngine();
        Assert.assertNotNull( engine );
    }
}
