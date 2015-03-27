package com.enonic.xp.launcher.env;

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
        if ( !version.isJava8() )
        {
            throw throwJavaVersionRequirements( version );
        }

        if ( version.getUpdate() < 40 )
        {
            throw throwJavaVersionRequirements( version );
        }
    }

    private LauncherException throwJavaVersionRequirements( final JavaVersion version )
    {
        throw new LauncherException( "Java 1.8 update 40 and above is required. You are running %s.", version );
    }
}
