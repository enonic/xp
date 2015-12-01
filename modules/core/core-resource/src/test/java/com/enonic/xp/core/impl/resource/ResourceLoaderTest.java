package com.enonic.xp.core.impl.resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;

import static org.junit.Assert.*;

public abstract class ResourceLoaderTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    protected ApplicationKey appKey;

    protected Application app;

    protected Bundle bundle;

    protected ResourceLoader loader;

    @Before
    public final void setup()
    {
        this.appKey = ApplicationKey.from( "myapplication" );

        this.bundle = Mockito.mock( Bundle.class );

        this.app = Mockito.mock( Application.class );
        Mockito.when( this.app.getKey() ).thenReturn( this.appKey );
        Mockito.when( this.app.getBundle() ).thenReturn( this.bundle );
        Mockito.when( this.app.isStarted() ).thenReturn( true );

        initialize();
    }

    protected abstract void initialize();

    @Test
    public void getResource_notFound()
    {
        final Resource resource = this.loader.getResource( this.app, ResourceKey.from( this.appKey, "/a/b/resource.txt" ) );
        assertNotNull( resource );
        assertFalse( resource.exists() );
    }

    @Test
    public void findFolders_notFound()
    {
        final ResourceKeys keys = this.loader.findFolders( this.app, "/a" );
        assertNotNull( keys );
        assertTrue( keys.isEmpty() );
    }

    @Test
    public void findFiles_notFound()
    {
        final ResourceKeys keys = this.loader.findFiles( this.app, "/a", "xml", true );
        assertNotNull( keys );
        assertTrue( keys.isEmpty() );
    }
}
