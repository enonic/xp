package com.enonic.wem.portal.underscore;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.sun.jersey.api.client.ClientResponse;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.content.GetContentByPath;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.module.ModuleKeyResolver;
import com.enonic.wem.core.module.ModuleKeyResolverService;
import com.enonic.wem.core.module.ModuleResourcePathResolver;
import com.enonic.wem.portal.AbstractResourceTest;

import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

public class PublicResourceTest
    extends AbstractResourceTest
{
    private PublicResource resource;

    private ModuleResourcePathResolver modulePathResolver;

    private ModuleKeyResolverService moduleKeyResolverService;

    private ModuleKeyResolver moduleKeyResolver;

    private Client client;

    private Path tempDir;

    @Override
    protected Object getResourceInstance()
    {
        modulePathResolver = Mockito.mock( ModuleResourcePathResolver.class );
        moduleKeyResolverService = Mockito.mock( ModuleKeyResolverService.class );
        moduleKeyResolver = Mockito.mock( ModuleKeyResolver.class );
        client = Mockito.mock( Client.class );
        when( moduleKeyResolverService.forContent( isA( ContentPath.class ) ) ).thenReturn( moduleKeyResolver );
        resource = new PublicResource();
        resource.modulePathResolver = modulePathResolver;
        resource.client = client;
        resource.moduleKeyResolverService = moduleKeyResolverService;
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
        Content content = createContent( "content-id", "path/to/content", "content-type" );
        when( client.execute( Mockito.isA( GetContentByPath.class ) ) ).thenReturn( content );

        resource.mode = "live";
        resource.contentPath = "content";
        resource.moduleName = "demo-1.0.0";
        resource.resourceName = "css/main.css";
        final ClientResponse resp =
            resource().path( "/portal/live/path/to/content/_/public/demo-1.0.0/css/main.css" ).get( ClientResponse.class );

        assertEquals( 200, resp.getStatus() );
        assertEquals( "p {color:red;}", resp.getEntity( String.class ) );
        assertEquals( "text/css", resp.getHeaders().getFirst( "content-type" ) );
    }

    @Test
    public void getPublicResourceResolvingModuleVersion()
        throws Exception
    {
        final Path filePath = tempDir.resolve( "main.css" );
        Files.write( filePath, "p {color:red;}".getBytes( Charsets.UTF_8 ) );
        when( modulePathResolver.resolveResourcePath( isA( ModuleResourceKey.class ) ) ).thenReturn( filePath );
        Content content = createContent( "content-id", "path/to/content", "content-type" );
        when( client.execute( Mockito.isA( GetContentByPath.class ) ) ).thenReturn( content );
        when( moduleKeyResolver.resolve( ModuleName.from( "demo" ) ) ).thenReturn( ModuleKey.from( "demo-1.0.0" ) );

        resource.mode = "live";
        resource.contentPath = "content";
        resource.moduleName = "demo";
        resource.resourceName = "css/main.css";
        final ClientResponse resp =
            resource().path( "/portal/live/path/to/content/_/public/demo/css/main.css" ).get( ClientResponse.class );

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
        Content content = createContent( "content-id", "path/to/content", "content-type" );
        when( client.execute( Mockito.isA( GetContentByPath.class ) ) ).thenReturn( content );

        resource.mode = "live";
        resource.contentPath = "content";
        resource.moduleName = "demo-1.0.0";
        resource.resourceName = "css/main.css";
        final ClientResponse resp =
            resource().path( "/portal/live/path/to/content/_/public/demo-1.0.0/css/main.css" ).get( ClientResponse.class );

        assertEquals( 404, resp.getStatus() );
    }

    @Test
    public void getPublicResourceInvalidModule()
        throws Exception
    {
        final Path filePath = tempDir.resolve( "main.css" );
        when( modulePathResolver.resolveResourcePath( isA( ModuleResourceKey.class ) ) ).thenReturn( filePath );
        Content content = createContent( "content-id", "path/to/content", "content-type" );
        when( client.execute( Mockito.isA( GetContentByPath.class ) ) ).thenReturn( content );

        resource.mode = "live";
        resource.contentPath = "content";
        resource.moduleName = "demo-1-2-3";
        resource.resourceName = "css/main.css";
        final ClientResponse resp =
            resource().path( "/portal/live/path/to/content/_/public/demo-1.0.0/css/main.css" ).get( ClientResponse.class );

        assertEquals( 404, resp.getStatus() );
    }

    @Test
    public void getPublicResourceContentNotFound()
        throws Exception
    {
        final Path filePath = tempDir.resolve( "main.css" );
        when( modulePathResolver.resolveResourcePath( isA( ModuleResourceKey.class ) ) ).thenReturn( filePath );
        when( client.execute( Mockito.isA( GetContentByPath.class ) ) ).thenReturn( null );

        resource.mode = "live";
        resource.contentPath = "content";
        resource.moduleName = "demo-1-2-3";
        resource.resourceName = "css/main.css";
        final ClientResponse resp =
            resource().path( "/portal/live/path/to/content/_/public/demo-1.0.0/css/main.css" ).get( ClientResponse.class );

        assertEquals( 404, resp.getStatus() );
    }

    private Content createContent( final String id, final String name, final String contentTypeName )
    {
        return Content.newContent().
            id( ContentId.from( id ) ).
            path( ContentPath.from( name ) ).
            createdTime( DateTime.now() ).
            owner( UserKey.from( "myStore:me" ) ).
            displayName( "My Content" ).
            modifiedTime( DateTime.now() ).
            modifier( UserKey.superUser() ).
            type( ContentTypeName.from( contentTypeName ) ).
            build();
    }
}
