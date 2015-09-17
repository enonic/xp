package com.enonic.xp.tools.gradle;

import org.gradle.api.GradleException;
import org.gradle.api.invocation.Gradle;
import org.osgi.framework.Version;

public final class VersionCheck
{
    private final static String MIN_VERSION = "2.6";

    public static void checkGradleVersion( final Gradle gradle )
    {
        checkGradleVersion( gradle.getGradleVersion() );
    }

    private static void checkGradleVersion( final String version )
    {
        if ( isRightVersion( MIN_VERSION, version ) )
        {
            return;
        }

        throw new GradleException(
            String.format( "Incompatible Gradle version [%s]. Please upgrade to [%s] or above.", version, MIN_VERSION ) );
    }

    private static boolean isRightVersion( final String min, final String current )
    {
        return isRightVersion( Version.parseVersion( min ), Version.parseVersion( current ) );
    }

    private static boolean isRightVersion( final Version min, final Version current )
    {
        return current.compareTo( min ) >= 0;
    }
}
