package com.enonic.xp.launcher.impl.env;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static com.enonic.xp.launcher.impl.SharedConstants.XP_HOME_DIR;
import static com.enonic.xp.launcher.impl.SharedConstants.XP_INSTALL_DIR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class EnvironmentResolverTest
{
    @TempDir
    public Path temporaryFolder;

    @Test
    public void testInstallDir()
        throws Exception
    {
        final Environment env1 = resolve();
        assertNull( env1.getInstallDir() );
        assertNull( env1.getHomeDir() );

        final File dir = Files.createDirectory(this.temporaryFolder.resolve( "dir" ) ).toFile();

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

        final File dir = Files.createDirectory(this.temporaryFolder.resolve( "dir" ) ).toFile();

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
