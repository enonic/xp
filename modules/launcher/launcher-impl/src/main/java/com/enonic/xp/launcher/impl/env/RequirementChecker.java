package com.enonic.xp.launcher.impl.env;

import com.enonic.xp.launcher.LauncherException;

public final class RequirementChecker
{
    private final static int JAVA_UPDATE_MIN = 92;

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
        if ( !version.isJava8() || ( version.getUpdate() < JAVA_UPDATE_MIN ) )
        {
            throw new LauncherException( javaVersionRequirementsMessage( version ) );
        }
    }

    private String javaVersionRequirementsMessage( final JavaVersion version )
    {
        throw new LauncherException( "Java 1.8 update " + JAVA_UPDATE_MIN + " and above is required. You are running %s.", version );
    }
}
