package com.enonic.xp.internal.blobstore.file;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.enonic.xp.internal.blobstore.file.config.FileBlobStoreConfig;

import static org.junit.Assert.*;

public class FileBlobStoreProviderTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private FileBlobStoreProvider provider;

    private FileBlobStoreConfig config;

    @Before
    public void setup()
    {
        this.provider = new FileBlobStoreProvider();
        this.config = Mockito.mock( FileBlobStoreConfig.class );
        Mockito.when( this.config.baseDir() ).thenReturn( this.temporaryFolder.getRoot() );
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
