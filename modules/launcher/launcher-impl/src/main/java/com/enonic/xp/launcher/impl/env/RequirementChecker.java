package com.enonic.xp.launcher.impl.env;

import com.enonic.xp.launcher.LauncherException;

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
        final JavaVersion version = new JavaVersion( this.properties );
        if ( !version.isJava11() )
        {
            throw new LauncherException( javaVersionRequirementsMessage( version ) );
        }
    }

    private String javaVersionRequirementsMessage( final JavaVersion version )
    {
        throw new LauncherException( String.format( "Java 11 is required. You are running %s.", version ) );
    }
}
