package com.enonic.xp.testkit;

import org.junit.runner.RunWith;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.script.runtime.ScriptSettings;
import com.enonic.xp.testkit.mock.MockServiceRegistry;

@RunWith(ScriptRunner.class)
public abstract class ScriptTestSupport
    implements ScriptTestSuite
{
    private ApplicationKey appKey;

    private String appVersion;

    private String[] testFiles;

    public ScriptTestSupport()
    {
        setAppKey( "myapp" );
        setAppVersion( "1.0.0" );
        setTestFiles();
    }

    @Override
    public final ApplicationKey getAppKey()
    {
        return this.appKey;
    }

    public final void setAppKey( final String value )
    {
        this.appKey = ApplicationKey.from( value );
    }

    @Override
    public final String getAppVersion()
    {
        return this.appVersion;
    }

    public final void setAppVersion( final String value )
    {
        this.appVersion = value;
    }

    @Override
    public void setupConfig( final ConfigBuilder builder )
    {
        // Do nothing
    }

    @Override
    public void setupSettings( final ScriptSettings.Builder builder )
    {
        // Do nothing
    }

    @Override
    public void setupServices( final MockServiceRegistry registry )
    {
        // Do nothing
    }

    @Override
    public final String[] getTestFiles()
    {
        return this.testFiles;
    }

    public final void setTestFiles( final String... files )
    {
        this.testFiles = files;
    }

    @Override
    public void initialize()
    {
        // Do nothing
    }

    @Override
    public void setUp()
        throws Exception
    {
        // Do nothing
    }

    @Override
    public void tearDown()
        throws Exception
    {
        // Do nothing
    }
}
