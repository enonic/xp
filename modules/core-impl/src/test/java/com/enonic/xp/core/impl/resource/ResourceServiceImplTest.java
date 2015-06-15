package com.enonic.xp.core.impl.resource;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;

import com.google.common.collect.Lists;

import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.Resources;
import com.enonic.xp.support.ResourceTestHelper;

import static org.junit.Assert.*;

public class ResourceServiceImplTest
{

    private static final String RESOURCE_FILE_NAME = "resource.txt";

    private static final String RESOURCE_2_FILE_NAME = "resource2.txt";

    private static final String RESOURCE_PATH = "/a/b/resource.txt";

    private static final String RESOURCE_2_PATH = "/a/resource2.txt";

    private static final Collection<String> RESOURCE_PATHS = Lists.newArrayList( RESOURCE_PATH, RESOURCE_2_PATH );


    private ModuleKey moduleKey;

    private URL resourceUrl;

    private Bundle bundle;

    private ResourceServiceImpl resourceService;

    @Before
    public void before()
    {
        moduleKey = ModuleKey.from( "mymodule" );

        ResourceTestHelper resourceTestHelper = new ResourceTestHelper( this );
        resourceUrl = resourceTestHelper.getResource( RESOURCE_FILE_NAME );
        final URL resource2Url = resourceTestHelper.getResource( RESOURCE_2_FILE_NAME );

        bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getResource( RESOURCE_PATH ) ).thenReturn( resourceUrl );
        Mockito.when( bundle.getResource( RESOURCE_2_PATH ) ).thenReturn( resource2Url );
        Mockito.when( bundle.getState() ).thenReturn( Bundle.ACTIVE );

        Module module = Mockito.mock( Module.class );
        Mockito.when( module.getKey() ).thenReturn( moduleKey );
        Mockito.when( module.getBundle() ).thenReturn( bundle );

        final ModuleService moduleService = Mockito.mock( ModuleService.class );
        Mockito.when( moduleService.getModule( moduleKey ) ).thenReturn( module );

        resourceService = new ResourceServiceImpl();
        resourceService.setModuleService( moduleService );
    }

    @Test
    public void get_resource()
    {
        ResourceKey resourceKey;
        Resource resource;

        //Retrieves a resource
        resourceKey = ResourceKey.from( moduleKey, RESOURCE_PATH );
        resource = resourceService.getResource( resourceKey );
        assertNotNull( resource );
        assertEquals( resourceKey, resource.getKey() );
        assertEquals( resourceUrl, resource.getUrl() );

        //Retrieves a resource with an incorrect module key
        ModuleKey incorrectModuleKey = ModuleKey.from( "othermodule" );
        resourceKey = ResourceKey.from( incorrectModuleKey, RESOURCE_PATH );
        resource = resourceService.getResource( resourceKey );
        assertNull( resource );

        //Retrieves a resource with an incorrect resource path
        resourceKey = ResourceKey.from( moduleKey, "c/resource.txt" );
        resource = resourceService.getResource( resourceKey );
        assertNull( resource );
    }

    @Test
    public void find_resources()
    {
        Resources resources;

        //Finds resources for a specific path
        Mockito.when( bundle.getEntryPaths( "/" ) ).thenReturn( Collections.enumeration( RESOURCE_PATHS ) );
        resources = resourceService.findResources( moduleKey, RESOURCE_PATH );
        assertEquals( 1, resources.getSize() );
        assertEquals( ResourceKey.from( moduleKey, RESOURCE_PATH ), resources.first().getKey() );
        assertEquals( resourceUrl, resources.first().getUrl() );

        //Finds all text resources in a specific folder
        Mockito.when( bundle.getEntryPaths( "/" ) ).thenReturn( Collections.enumeration( RESOURCE_PATHS ) );
        resources = resourceService.findResources( moduleKey, "/a/.*\\.txt" );
        assertEquals( 2, resources.getSize() );

        //Finds all resources in an non existing folder
        Mockito.when( bundle.getEntryPaths( "/" ) ).thenReturn( Collections.enumeration( RESOURCE_PATHS ) );
        resources = resourceService.findResources( moduleKey, "/b/.*" );
        assertEquals( 0, resources.getSize() );

        //Finds all resources for an incorrect module key
        Mockito.when( bundle.getEntryPaths( "/" ) ).thenReturn( Collections.enumeration( RESOURCE_PATHS ) );
        ModuleKey incorrectModuleKey = ModuleKey.from( "othermodule" );
        resources = resourceService.findResources( incorrectModuleKey, RESOURCE_PATH );
        assertEquals( 0, resources.getSize() );
    }
}
