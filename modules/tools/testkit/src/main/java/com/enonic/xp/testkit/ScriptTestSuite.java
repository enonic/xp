package com.enonic.xp.testkit;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.script.runtime.ScriptSettings;
import com.enonic.xp.testkit.mock.MockServiceRegistry;

public interface ScriptTestSuite
{
    ApplicationKey getAppKey();

    String getAppVersion();

    void setupConfig( ConfigBuilder builder );

    void setupSettings( ScriptSettings.Builder builder );

    void setupServices( MockServiceRegistry registry );

    String[] getTestFiles();

    void initialize();

    void setUp()
        throws Exception;

    void tearDown()
        throws Exception;
}
