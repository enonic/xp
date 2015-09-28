package com.enonic.xp.tools.gradle;

import org.gradle.api.GradleException;
import org.gradle.api.invocation.Gradle;
import org.junit.Test;
import org.mockito.Mockito;

public class VersionCheckTest
{
    private Gradle mockVersion( final String version )
    {
        final Gradle gradle = Mockito.mock( Gradle.class );
        Mockito.when( gradle.getGradleVersion() ).thenReturn( version );
        return gradle;
    }

    @Test
    public void testRightVersions()
    {
        VersionCheck.checkGradleVersion( mockVersion( "2.6" ) );
        VersionCheck.checkGradleVersion( mockVersion( "2.8" ) );
        VersionCheck.checkGradleVersion( mockVersion( "3.0" ) );
    }

    @Test(expected = GradleException.class)
    public void testWrongVersion()
    {
        VersionCheck.checkGradleVersion( mockVersion( "2.5" ) );
    }
}
