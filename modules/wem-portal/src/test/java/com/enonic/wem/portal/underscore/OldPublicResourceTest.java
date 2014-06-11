package com.enonic.wem.portal.underscore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.core.DefaultResourceConfig;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeyResolver;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.module.ModuleKeyResolverService;
import com.enonic.wem.core.module.ModuleResourcePathResolver;
import com.enonic.wem.portal.OldAbstractResourceTest;

import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

@Ignore
public class OldPublicResourceTest
    extends OldAbstractResourceTest
{
    private OldPublicResource resource;

    private ModuleResourcePathResolver modulePathResolver;

    private ContentService contentService;

    private Path tempDir;

    @Override
    protected void configure( final DefaultResourceConfig config )
    {
        modulePathResolver = Mockito.mock( ModuleResourcePathResolver.class );
        final ModuleKeyResolverService moduleKeyResolverService = Mockito.mock( ModuleKeyResolverService.class );
        final ModuleKeyResolver moduleKeyResolver = ModuleKeyResolver.from( ModuleKey.from( "demo-1.0.0" ) );
        contentService = Mockito.mock( ContentService.class );
        when( moduleKeyResolverService.forContent( isA( ContentPath.class ) ) ).thenReturn( moduleKeyResolver );
        resource = new OldPublicResource();
        resource.modulePathResolver = modulePathResolver;
        resource.moduleKeyResolverService = moduleKeyResolverService;

        config.getSingletons().add( resource );
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

        final ContentPath contentPath = ContentPath.from( "path/to/content" );
        final Content content = createContent( "content-id", contentPath, "content-type" );
        when( contentService.getByPath( contentPath, ContentConstants.DEFAULT_CONTEXT ) ).thenReturn( content );

        // resource.mode = "live";
        // resource.contentPath = "content";
        // resource.moduleName = "demo-1.0.0";
        // resource.resourceName = "css/main.css";
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

        final ContentPath contentPath = ContentPath.from( "path/to/content" );
        final Content content = createContent( "content-id", contentPath, "content-type" );
        when( contentService.getByPath( contentPath, ContentConstants.DEFAULT_CONTEXT ) ).thenReturn( content );

        // resource.mode = "live";
        // resource.contentPath = "content";
        // resource.moduleName = "demo";
        // resource.resourceName = "css/main.css";
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

        final ContentPath contentPath = ContentPath.from( "path/to/content" );
        final Content content = createContent( "content-id", contentPath, "content-type" );
        when( contentService.getByPath( contentPath, ContentConstants.DEFAULT_CONTEXT ) ).thenReturn( content );

        // resource.mode = "live";
        // resource.contentPath = "content";
        // resource.moduleName = "demo-1.0.0";
        // resource.resourceName = "css/main.css";
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

        final ContentPath contentPath = ContentPath.from( "path/to/content" );
        final Content content = createContent( "content-id", contentPath, "content-type" );
        when( contentService.getByPath( contentPath, ContentConstants.DEFAULT_CONTEXT ) ).thenReturn( content );

        // resource.mode = "live";
        // resource.contentPath = "content";
        // resource.moduleName = "demo-1-2-3";
        // resource.resourceName = "css/main.css";
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
        when( contentService.getByPath( ContentPath.from( "content" ), ContentConstants.DEFAULT_CONTEXT ) ).thenReturn( null );

        // resource.mode = "live";
        // resource.contentPath = "content";
        // resource.moduleName = "demo-1-2-3";
        // resource.resourceName = "css/main.css";
        final ClientResponse resp =
            resource().path( "/portal/live/path/to/content/_/public/demo-1.0.0/css/main.css" ).get( ClientResponse.class );

        assertEquals( 404, resp.getStatus() );
    }

    private Content createContent( final String id, final ContentPath contentPath, final String contentTypeName )
    {
        return Content.newContent().
            id( ContentId.from( id ) ).
            path( contentPath ).
            createdTime( Instant.now() ).
            owner( UserKey.from( "myStore:me" ) ).
            displayName( "My Content" ).
            modifiedTime( Instant.now() ).
            modifier( UserKey.superUser() ).
            type( ContentTypeName.from( contentTypeName ) ).
            build();
    }
}
