package com.enonic.xp.testing.script;

import org.junit.runner.RunWith;

@RunWith(ScriptRunner.class)
public abstract class ScriptRunnerSupport
    extends ScriptTestSupport
{
    public abstract String getScriptTestFile();
}
