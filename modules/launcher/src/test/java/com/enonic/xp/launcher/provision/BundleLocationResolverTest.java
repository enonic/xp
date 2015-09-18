package com.enonic.xp.launcher.provision;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.enonic.xp.launcher.SharedConstants;
import com.enonic.xp.launcher.config.ConfigProperties;

import static org.junit.Assert.*;

public class BundleLocationResolverTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File systemDir;

    private File projectDir;

    private ConfigProperties props;

    private BundleLocationResolver resolver;

    @Before
    public void setup()
        throws Exception
    {
        this.systemDir = this.folder.newFolder( "system" );
        this.projectDir = this.folder.newFolder( "project" );

        this.props = new ConfigProperties();
        this.props.put( SharedConstants.DEV_PROJECT_DIR, this.projectDir.getAbsolutePath() );
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
    }

    @Test
    public void testResolve_devMode()
    {
        newResolver( true );

        final String location1 = this.resolver.resolve( "com.enonic.xp:portal-api:1.0.0" );
        assertEquals( "project/modules/portal/portal-api/target/libs/portal-api-1.0.0.jar", getRelativeLocation( location1 ) );

        final String location2 = this.resolver.resolve( "org.slf4j:slf4j-api:1.7.10" );
        assertEquals( "system/org/slf4j/slf4j-api/1.7.10/slf4j-api-1.7.10.jar", getRelativeLocation( location2 ) );
    }
}
