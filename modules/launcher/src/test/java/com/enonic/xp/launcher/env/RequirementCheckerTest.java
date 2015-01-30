package com.enonic.xp.launcher.env;

import org.junit.Test;

import com.enonic.xp.launcher.LauncherException;

import static com.google.common.base.StandardSystemProperty.JAVA_VERSION;

public class RequirementCheckerTest
{
    @Test
    public void rightJavaVersion()
    {
        final SystemProperties props = new SystemProperties();
        props.put( JAVA_VERSION.key(), "1.8.0_20" );

        new RequirementChecker( props ).check();
    }

    @Test(expected = LauncherException.class)
    public void wrongJavaVersion()
    {
        final SystemProperties props = new SystemProperties();
        props.put( JAVA_VERSION.key(), "1.7.0" );

        new RequirementChecker( props ).check();
    }
}
