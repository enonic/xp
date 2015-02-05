package com.enonic.xp.launcher.env;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.enonic.xp.launcher.SharedConstants;

import static org.junit.Assert.*;

public class EnvironmentResolverTest
    implements SharedConstants
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testInstallDir()
        throws Exception
    {
        final Environment env1 = resolve();
        assertNull( env1.getInstallDir() );
        assertNull( env1.getHomeDir() );

        final File dir = this.temporaryFolder.newFolder();

        final Environment env2 = resolve( XP_INSTALL_DIR, dir.getAbsolutePath() );
        assertEquals( dir, env2.getInstallDir() );
        assertEquals( new File( dir, "home" ), env2.getHomeDir() );
    }

    @Test
    public void testHomeDir()
        throws Exception
    {
        final Environment env1 = resolve();
        assertNull( env1.getHomeDir() );

        final File dir = this.temporaryFolder.newFolder();

        final Environment env2 = resolve( XP_HOME_DIR, dir.getAbsolutePath() );
        assertEquals( dir, env2.getHomeDir() );
    }

    private Environment resolve( final String... values )
    {
        final SystemProperties props = new SystemProperties();
        for ( int i = 0; i < values.length; i += 2 )
        {
            props.put( values[i], values[i + 1] );
        }

        return new EnvironmentResolver( props ).resolve();
    }
}
