package com.enonic.xp.core.impl.resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;

import static org.junit.Assert.*;

public class ResourceServiceImplTest
{
    private ApplicationKey appKey;

    private ResourceServiceImpl resourceService;

    private Application app;

    private ResourceLoader mockLoader;

    private Resource mockResource;

    @Before
    public void before()
    {
        this.appKey = ApplicationKey.from( "myapp" );

        this.app = Mockito.mock( Application.class );
        Mockito.when( this.app.getKey() ).thenReturn( this.appKey );
        Mockito.when( this.app.isStarted() ).thenReturn( true );

        final ApplicationService applicationService = Mockito.mock( ApplicationService.class );
        Mockito.when( applicationService.getApplication( this.appKey ) ).thenReturn( this.app );

        this.resourceService = new ResourceServiceImpl();
        this.resourceService.setApplicationService( applicationService );
    }

    @Test
    public void testLoader()
    {
        assertNotNull( this.resourceService.resourceLoader );
    }

    private void mockLoader()
    {
        this.mockLoader = Mockito.mock( ResourceLoader.class );
        this.mockResource = Mockito.mock( Resource.class );

        Mockito.when( this.mockLoader.getResource( Mockito.any(), Mockito.any() ) ).thenReturn( this.mockResource );
        Mockito.when( this.mockLoader.findFolders( Mockito.any(), Mockito.any() ) ).thenReturn( ResourceKeys.from( "myapp:/a" ) );

        this.resourceService.resourceLoader = this.mockLoader;
    }

    @Test
    public void getResource()
    {
        mockLoader();

        final Resource resource = this.resourceService.getResource( ResourceKey.from( this.appKey, "/a/b.txt" ) );
        assertSame( this.mockResource, resource );
    }

    @Test
    public void getResource_notActive()
    {
        mockLoader();
        Mockito.when( this.app.isStarted() ).thenReturn( false );

        final Resource resource = this.resourceService.getResource( ResourceKey.from( this.appKey, "/a/b.txt" ) );
        assertNotSame( this.mockResource, resource );
        assertFalse( resource.exists() );
    }

    @Test
    public void findFolders()
    {
        mockLoader();

        final ResourceKeys keys = this.resourceService.findFolders( this.appKey, "/" );
        assertNotNull( keys );
        assertEquals( 1, keys.getSize() );
    }

    @Test
    public void findFolders_notActive()
    {
        mockLoader();
        Mockito.when( this.app.isStarted() ).thenReturn( false );

        final ResourceKeys keys = this.resourceService.findFolders( this.appKey, "/" );
        assertNotNull( keys );
        assertEquals( 0, keys.getSize() );
    }
}
