package com.enonic.xp.internal.blobstore.file;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import com.enonic.xp.internal.blobstore.file.config.FileBlobStoreConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class FileBlobStoreProviderTest
{
    @TempDir
    public Path temporaryFolder;

    private FileBlobStoreProvider provider;

    private FileBlobStoreConfig config;

    @BeforeEach
    void setup()
    {
        this.provider = new FileBlobStoreProvider();
        this.config = Mockito.mock( FileBlobStoreConfig.class );
        Mockito.when( this.config.baseDir() ).thenReturn( this.temporaryFolder );
        this.provider.setConfig( this.config );
    }

    @Test
    void testAccessors()
    {
        assertEquals( "file", this.provider.name() );
        assertSame( this.config, this.provider.config() );
    }

    @Test
    void get_valid()
    {
        Mockito.when( this.config.isValid() ).thenReturn( true );
        assertNotNull( this.provider.get() );
    }

    @Test
    void get_notValid()
    {
        Mockito.when( this.config.isValid() ).thenReturn( false );
        assertNull( this.provider.get() );
    }
}
