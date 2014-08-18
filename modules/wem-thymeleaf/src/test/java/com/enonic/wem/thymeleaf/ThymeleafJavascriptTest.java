package com.enonic.wem.thymeleaf;

import org.junit.Test;

import com.enonic.wem.script.ScriptRunner;
import com.enonic.wem.script.internal.RhinoScriptRunnerFactory;

public class ThymeleafJavascriptTest
{
    @Test
    public void testSomething()
    {
        final RhinoScriptRunnerFactory runnerFactory = new RhinoScriptRunnerFactory();
        final ScriptRunner runner = runnerFactory.newRunner();
    }
}
