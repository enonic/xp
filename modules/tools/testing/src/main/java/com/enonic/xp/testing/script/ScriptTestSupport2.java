package com.enonic.xp.testing.script;

import org.junit.runner.RunWith;

@RunWith(ScriptRunner.class)
public abstract class ScriptTestSupport2
    extends AbstractScriptTest2
{
    public abstract String getScriptTestFile();
}
