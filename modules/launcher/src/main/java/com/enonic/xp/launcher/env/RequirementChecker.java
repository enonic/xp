package com.enonic.xp.launcher.env;

import com.enonic.xp.launcher.LauncherException;

import static com.google.common.base.StandardSystemProperty.JAVA_VERSION;

public final class RequirementChecker
{
    private final SystemProperties properties;

    public RequirementChecker( final SystemProperties properties )
    {
        this.properties = properties;
    }

    public void check()
    {
        checkJavaVersion();
    }

    private void checkJavaVersion()
    {
        final String version = this.properties.get( JAVA_VERSION.key() );
        if ( !isJava8( version ) )
        {
            throw new LauncherException( "Java 1.8 is required. You are running %s.", version );
        }
    }

    private boolean isJava8( final String version )
    {
        return version.startsWith( "1.8." );
    }
}
