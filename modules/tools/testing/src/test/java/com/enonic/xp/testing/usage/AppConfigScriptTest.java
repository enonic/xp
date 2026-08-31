package com.enonic.xp.testing.usage;

import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.testing.ScriptRunnerSupport;

class AppConfigScriptTest
    extends ScriptRunnerSupport
{
    @Override
    public String getScriptTestFile()
    {
        return "test/usage/app-config-test.js";
    }

    @Override
    protected Configuration createAppConfig()
    {
        return ConfigBuilder.create().add( "key", "value" ).add( "dotted.key", "dotted value" ).build();
    }
}
