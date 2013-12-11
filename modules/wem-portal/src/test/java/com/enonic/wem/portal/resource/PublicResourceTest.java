package com.enonic.wem.portal.resource;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.sun.jersey.api.client.ClientResponse;

import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.core.module.ModuleResourcePathResolver;
import com.enonic.wem.portal.AbstractResourceTest;

import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

public class PublicResourceTest
    extends AbstractResourceTest
{
    private ModuleResourcePathResolver modulePathResolver;

    private PublicResource resource;

    private Path tempDir;

    @Override
    protected Object getResourceInstance()
    {
        resource = new PublicResource();
        modulePathResolver = Mockito.mock( ModuleResourcePathResolver.class );
        resource.modulePathResolver = modulePathResolver;
        return resource;
    }

    @Before
    public void setup()
        throws IOException
    {
        tempDir = Files.createTempDirectory( "modules" );
        mockCurrentContextHttpRequest();
    }

    @After
    public void tearDown()
        throws IOException
    {
        FileUtils.deleteDirectory( tempDir.toFile() );
    }

    @Test
    public void getPublicResourceFound()
        throws Exception
    {
        final Path filePath = tempDir.resolve( "main.css" );
        Files.write( filePath, "p {color:red;}".getBytes( Charsets.UTF_8 ) );
        when( modulePathResolver.resolveResourcePath( isA( ModuleResourceKey.class ) ) ).thenReturn( filePath );

        resource.mode = "live";
        resource.contentPath = "content";
        resource.moduleName = "demo-1.0.0";
        resource.resourceName = "css/main.css";
        final ClientResponse resp = resource().path( "/portal/live/content/_/public/demo-1.0.0/css/main.css" ).get( ClientResponse.class );

        assertEquals( 200, resp.getStatus() );
        assertEquals( "p {color:red;}", resp.getEntity( String.class ) );
        assertEquals( "text/css", resp.getHeaders().getFirst( "content-type" ) );
    }

    @Test
    public void getPublicResourceNotFound()
        throws Exception
    {
        final Path filePath = tempDir.resolve( "main.css" );
        when( modulePathResolver.resolveResourcePath( isA( ModuleResourceKey.class ) ) ).thenReturn( filePath );

        resource.mode = "live";
        resource.contentPath = "content";
        resource.moduleName = "demo-1.0.0";
        resource.resourceName = "css/main.css";
        final ClientResponse resp = resource().path( "/portal/live/content/_/public/demo-1.0.0/css/main.css" ).get( ClientResponse.class );

        assertEquals( 404, resp.getStatus() );
    }

    @Test
    public void getPublicResourceInvalidModule()
        throws Exception
    {
        final Path filePath = tempDir.resolve( "main.css" );
        when( modulePathResolver.resolveResourcePath( isA( ModuleResourceKey.class ) ) ).thenReturn( filePath );

        resource.mode = "live";
        resource.contentPath = "content";
        resource.moduleName = "demo-1-2-3";
        resource.resourceName = "css/main.css";
        final ClientResponse resp = resource().path( "/portal/live/content/_/public/demo-1.0.0/css/main.css" ).get( ClientResponse.class );

        assertEquals( 404, resp.getStatus() );
    }
}
