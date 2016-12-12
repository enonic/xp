package com.enonic.xp.launcher.impl.provision;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.enonic.xp.launcher.impl.SharedConstants;
import com.enonic.xp.launcher.impl.config.ConfigProperties;

import static org.junit.Assert.*;

public class BundleLocationResolverTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File systemDir;

    private ConfigProperties props;

    private BundleLocationResolver resolver;

    @Before
    public void setup()
        throws Exception
    {
        this.systemDir = this.folder.newFolder( "system" );
        final File projectDir = this.folder.newFolder( "project" );

        this.props = new ConfigProperties();
        this.props.put( SharedConstants.DEV_PROJECT_DIR, projectDir.getAbsolutePath() );
        this.props.put( SharedConstants.DEV_GROUP_ID, "com.enonic.xp" );
    }

    private String getRelativeLocation( final String location )
    {
        return location.substring( this.folder.getRoot().toURI().toString().length() );
    }

    private void newResolver( final boolean devMode )
    {
        this.props.put( SharedConstants.DEV_MODE, String.valueOf( devMode ) );
        this.resolver = new BundleLocationResolver( this.systemDir, this.props );
    }

    @Test
    public void testResolve()
    {
        newResolver( false );

        final String location1 = this.resolver.resolve( "com.enonic.xp:portal-api:1.0.0" );
        assertEquals( "system/com/enonic/xp/portal-api/1.0.0/portal-api-1.0.0.jar", getRelativeLocation( location1 ) );

        final String location2 = this.resolver.resolve( "org.slf4j:slf4j-api:1.7.10" );
        assertEquals( "system/org/slf4j/slf4j-api/1.7.10/slf4j-api-1.7.10.jar", getRelativeLocation( location2 ) );

        final String location3 = this.resolver.resolve( "org.slf4j:slf4j-api:1.7.10:all:jar" );
        assertEquals( "system/org/slf4j/slf4j-api/1.7.10/slf4j-api-1.7.10-all.jar", getRelativeLocation( location3 ) );
    }

    @Test
    public void testResolve_devMode()
    {
        newResolver( true );

        final String location1 = this.resolver.resolve( "com.enonic.xp:portal-api:1.0.0" );
        assertEquals( "project/modules/portal/portal-api/target/libs/portal-api-1.0.0.jar", getRelativeLocation( location1 ) );

        final String location2 = this.resolver.resolve( "org.slf4j:slf4j-api:1.7.10" );
        assertEquals( "system/org/slf4j/slf4j-api/1.7.10/slf4j-api-1.7.10.jar", getRelativeLocation( location2 ) );

        final String location3 = this.resolver.resolve( "com.enonic.xp:other:1.0.0" );
        assertEquals( "project/modules/other/target/libs/other-1.0.0.jar", getRelativeLocation( location3 ) );

        this.props.remove( SharedConstants.DEV_PROJECT_DIR );
        newResolver( true );

        final String location4 = this.resolver.resolve( "com.enonic.xp:other:1.0.0" );
        assertEquals( "system/com/enonic/xp/other/1.0.0/other-1.0.0.jar", getRelativeLocation( location4 ) );
    }
}
