package com.enonic.xp.repo.impl.dump.reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import org.junit.jupiter.api.io.TempDir;

class BaseDumpReaderTest
{

    @TempDir
    protected Path temporaryFolder;

    protected Path dumpFolder;

    protected void hideTheFileWindowsWay( final Path hiddenFolder )
        throws IOException
    {
        Files.setAttribute( hiddenFolder, "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS );
    }

    protected boolean isWindows()
    {
        return System.getProperty( "os.name" ).toLowerCase().startsWith( "windows" );
    }

    protected void createMetaDataFile( final Path parent )
        throws IOException
    {
        final String content = "{\"xpVersion\":\"X.Y.Z.SNAPSHOT\",\"timestamp\":\"1970-01-01T00:00:00.000Z\",\"modelVersion\":\"1.0.0\"}";
        Files.writeString( parent.resolve( "dump.json" ), content );
    }

    protected Path createFolder( final Path parent, final String name )
        throws IOException
    {
        return Files.createDirectory( parent.resolve( name ) );
    }

    protected Path createFile( final Path parent, final String name )
        throws IOException
    {
        return Files.createFile( parent.resolve( name ) );
    }

}
