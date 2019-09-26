package com.enonic.xp.internal.blobstore.file;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import com.enonic.xp.internal.blobstore.file.config.FileBlobStoreConfig;

import static org.junit.jupiter.api.Assertions.*;

public class FileBlobStoreProviderTest
{
    @TempDir
    public Path temporaryFolder;

    private FileBlobStoreProvider provider;

    private FileBlobStoreConfig config;

    @BeforeEach
    public void setup()
    {
        this.provider = new FileBlobStoreProvider();
        this.config = Mockito.mock( FileBlobStoreConfig.class );
        Mockito.when( this.config.baseDir() ).thenReturn( this.temporaryFolder.toFile() );
        this.provider.setConfig( this.config );
    }

    @Test
    public void testAccessors()
    {
        assertEquals( "file", this.provider.name() );
        assertSame( this.config, this.provider.config() );
    }

    @Test
    public void get_valid()
    {
        Mockito.when( this.config.isValid() ).thenReturn( true );
        assertNotNull( this.provider.get() );
    }

    @Test
    public void get_notValid()
    {
        Mockito.when( this.config.isValid() ).thenReturn( false );
        assertNull( this.provider.get() );
    }
}
