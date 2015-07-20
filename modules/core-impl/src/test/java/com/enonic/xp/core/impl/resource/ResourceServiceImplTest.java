package com.enonic.xp.core.impl.resource;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.module.Module;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.Resources;
import com.enonic.xp.support.ResourceTestHelper;

import static org.junit.Assert.*;

public class ResourceServiceImplTest
{
    private static final String RESOURCE_FILE_NAME = "resource.txt";

    private static final String RESOURCE_2_FILE_NAME = "resource2.txt";

    private static final String RESOURCE_PATH = "/a/b/" + RESOURCE_FILE_NAME;

    private static final String RESOURCE_2_PATH = "/a/" + RESOURCE_2_FILE_NAME;


    private ApplicationKey applicationKey;

    private URL resourceUrl;

    private URL resource2Url;

    private Bundle bundle;

    private ResourceServiceImpl resourceService;

    @Before
    public void before()
    {
        applicationKey = ApplicationKey.from( "mymodule" );

        ResourceTestHelper resourceTestHelper = new ResourceTestHelper( this );
        resourceUrl = resourceTestHelper.getResource( RESOURCE_FILE_NAME );
        resource2Url = resourceTestHelper.getResource( RESOURCE_2_FILE_NAME );

        bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getResource( RESOURCE_PATH ) ).thenReturn( resourceUrl );
        Mockito.when( bundle.getResource( RESOURCE_2_PATH ) ).thenReturn( resource2Url );
        Mockito.when( bundle.getState() ).thenReturn( Bundle.ACTIVE );

        Module module = Mockito.mock( Module.class );
        Mockito.when( module.getKey() ).thenReturn( applicationKey );
        Mockito.when( module.getBundle() ).thenReturn( bundle );

        final ApplicationService applicationService = Mockito.mock( ApplicationService.class );
        Mockito.when( applicationService.getModule( applicationKey ) ).thenReturn( module );

        resourceService = new ResourceServiceImpl();
        resourceService.setApplicationService( applicationService );
    }

    @Test
    public void get_resource()
    {
        ResourceKey resourceKey;
        Resource resource;

        //Retrieves a resource
        resourceKey = ResourceKey.from( applicationKey, RESOURCE_PATH );
        resource = resourceService.getResource( resourceKey );
        assertNotNull( resource );
        assertEquals( resourceKey, resource.getKey() );
        assertEquals( resourceUrl, resource.getUrl() );

        //Retrieves a resource with an incorrect module key
        ApplicationKey incorrectApplicationKey = ApplicationKey.from( "othermodule" );
        resourceKey = ResourceKey.from( incorrectApplicationKey, RESOURCE_PATH );
        resource = resourceService.getResource( resourceKey );
        assertNull( resource );

        //Retrieves a resource with an incorrect resource path
        resourceKey = ResourceKey.from( applicationKey, "c/resource.txt" );
        resource = resourceService.getResource( resourceKey );
        assertNull( resource );
    }

    @Test
    public void find_resources()
    {
        Resources resources;

        Mockito.when( bundle.findEntries( "/", RESOURCE_FILE_NAME, true ) ).thenReturn(
            Collections.enumeration( Collections.singleton( resourceUrl ) ) );
        Mockito.when( bundle.findEntries( "/a", "*", true ) ).thenReturn(
            Collections.enumeration( Arrays.asList( resourceUrl, resource2Url ) ) );

        //Finds resources for a specific path
        resources = resourceService.findResources( applicationKey, "/", RESOURCE_FILE_NAME, true );
        assertEquals( 1, resources.getSize() );

        //Finds all text resources in a specific folder
        resources = resourceService.findResources( applicationKey, "/a", "*", true );
        assertEquals( 2, resources.getSize() );

        //Finds all resources in an non existing folder
        resources = resourceService.findResources( applicationKey, "/b", "*", true );
        assertEquals( 0, resources.getSize() );

        //Finds all resources for an incorrect module key
        ApplicationKey incorrectApplicationKey = ApplicationKey.from( "othermodule" );
        resources = resourceService.findResources( incorrectApplicationKey, "/", RESOURCE_FILE_NAME, true );
        assertEquals( 0, resources.getSize() );
    }
}
