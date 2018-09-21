package com.enonic.xp.launcher.impl.env;

import org.junit.Test;

import com.enonic.xp.launcher.LauncherException;

import static com.google.common.base.StandardSystemProperty.JAVA_VERSION;

public class RequirementCheckerTest
{
    @Test
    public void rightJavaVersion()
    {
        final SystemProperties props = new SystemProperties();
        props.put( JAVA_VERSION.key(), "10.0.2" );

        new RequirementChecker( props ).check();
    }

    @Test
    public void rightJavaVersion_withClassifier()
    {
        final SystemProperties props = new SystemProperties();
        props.put( JAVA_VERSION.key(), "10.0.2_94-internal" );

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
