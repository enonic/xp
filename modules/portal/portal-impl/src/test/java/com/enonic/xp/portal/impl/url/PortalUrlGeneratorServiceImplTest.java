package com.enonic.xp.portal.impl.url;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.Media;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.url.ApiUrlGeneratorParams;
import com.enonic.xp.portal.url.AttachmentUrlGeneratorParams;
import com.enonic.xp.portal.url.ImageUrlGeneratorParams;
import com.enonic.xp.portal.url.AttachmentUrlParts;
import com.enonic.xp.portal.url.AttachmentUrlPartsParams;
import com.enonic.xp.portal.url.ImageUrlParts;
import com.enonic.xp.portal.url.ImageUrlPartsParams;
import com.enonic.xp.portal.url.PortalUrlGeneratorService;
import com.enonic.xp.portal.url.UrlGeneratorParams;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.webapp.WebappService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PortalUrlGeneratorServiceImplTest
{
    private PortalUrlGeneratorService service;

    private ContentService contentService;

    @BeforeEach
    void setUp()
    {
        this.contentService = mock( ContentService.class );
        this.service = new PortalUrlGeneratorServiceImpl( mock( WebappService.class ), mock( SiteService.class ), this.contentService );
    }

    @AfterEach
    void tearDown()
    {
        PortalRequestAccessor.remove();
    }

    @Test
    void imageUrl_basic()
    {
        final ImageUrlGeneratorParams params = ImageUrlGeneratorParams.create()
            .setBaseUrl( "baseUrl" )
            .setMedia( () -> mockMedia( "123456", "mycontent.png" ) )
            .setProjectName( () -> ProjectName.from( "myproject" ) )
            .setBranch( () -> Branch.from( "draft" ) )
            .setScale( "max(300)" )
            .build();

        final String url = this.service.imageUrl( params );

        assertEquals( "baseUrl/_/media:image/myproject:draft/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png", url );
    }

    @Test
    void imageUrl_masterBranch()
    {
        final ImageUrlGeneratorParams params = ImageUrlGeneratorParams.create()
            .setBaseUrl( "baseUrl" )
            .setMedia( () -> mockMedia( "123456", "mycontent.png" ) )
            .setProjectName( () -> ProjectName.from( "myproject" ) )
            .setBranch( () -> Branch.from( "master" ) )
            .setScale( "max(300)" )
            .build();

        final String url = this.service.imageUrl( params );

        assertEquals( "baseUrl/_/media:image/myproject/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png", url );
    }

    @Test
    void imageUrl_withQualityBackgroundFilter()
    {
        final ImageUrlGeneratorParams params = ImageUrlGeneratorParams.create()
            .setBaseUrl( "baseUrl" )
            .setMedia( () -> mockMedia( "123456", "mycontent.png" ) )
            .setProjectName( () -> ProjectName.from( "myproject" ) )
            .setBranch( () -> Branch.from( "draft" ) )
            .setScale( "max(300)" )
            .setQuality( 85 )
            .setBackground( "0x000000" )
            .setFilter( "blur(3)" )
            .build();

        final String url = this.service.imageUrl( params );

        assertEquals(
            "baseUrl/_/media:image/myproject:draft/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png?quality=85&background=0x000000&filter=blur%283%29",
            url );
    }

    @Test
    void imageUrl_withFormat()
    {
        final ImageUrlGeneratorParams params = ImageUrlGeneratorParams.create()
            .setBaseUrl( "baseUrl" )
            .setMedia( () -> mockMedia( "123456", "mycontent.png" ) )
            .setProjectName( () -> ProjectName.from( "myproject" ) )
            .setBranch( () -> Branch.from( "draft" ) )
            .setScale( "max(300)" )
            .setFormat( "webp" )
            .build();

        final String url = this.service.imageUrl( params );

        assertEquals( "baseUrl/_/media:image/myproject:draft/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png.webp", url );
    }

    @Test
    void imageUrl_withExtraQueryParams()
    {
        final ImageUrlGeneratorParams params = ImageUrlGeneratorParams.create()
            .setBaseUrl( "baseUrl" )
            .setMedia( () -> mockMedia( "123456", "mycontent.png" ) )
            .setProjectName( () -> ProjectName.from( "myproject" ) )
            .setBranch( () -> Branch.from( "draft" ) )
            .setScale( "max(300)" )
            .setQueryParam( "ts", "123" )
            .build();

        final String url = this.service.imageUrl( params );

        assertEquals( "baseUrl/_/media:image/myproject:draft/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png?ts=123", url );
    }

    @Test
    void attachmentUrl_basic()
    {
        final AttachmentUrlGeneratorParams params = AttachmentUrlGeneratorParams.create()
            .setBaseUrl( "baseUrl" )
            .setContent( () -> mockMedia( "123456", "mycontent.png" ) )
            .setProjectName( () -> ProjectName.from( "myproject" ) )
            .setBranch( () -> Branch.from( "draft" ) )
            .build();

        final String url = this.service.attachmentUrl( params );

        assertEquals( "baseUrl/_/media:attachment/myproject:draft/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png", url );
    }

    @Test
    void attachmentUrl_masterBranch()
    {
        final AttachmentUrlGeneratorParams params = AttachmentUrlGeneratorParams.create()
            .setBaseUrl( "baseUrl" )
            .setContent( () -> mockMedia( "123456", "mycontent.png" ) )
            .setProjectName( () -> ProjectName.from( "myproject" ) )
            .setBranch( () -> Branch.from( "master" ) )
            .build();

        final String url = this.service.attachmentUrl( params );

        assertEquals( "baseUrl/_/media:attachment/myproject/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png", url );
    }

    @Test
    void attachmentUrl_withDownload()
    {
        final AttachmentUrlGeneratorParams params = AttachmentUrlGeneratorParams.create()
            .setBaseUrl( "baseUrl" )
            .setContent( () -> mockMedia( "123456", "mycontent.png" ) )
            .setProjectName( () -> ProjectName.from( "myproject" ) )
            .setBranch( () -> Branch.from( "draft" ) )
            .setDownload( true )
            .setQueryParams( Map.of( "æ", List.of( "a", "e" ) ) )
            .build();

        final String url = this.service.attachmentUrl( params );

        assertEquals(
            "baseUrl/_/media:attachment/myproject:draft/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png?%C3%A6=a&%C3%A6=e&download",
            url );
    }

    @Test
    void attachmentUrl_byName()
    {
        final AttachmentUrlGeneratorParams params = AttachmentUrlGeneratorParams.create()
            .setBaseUrl( "baseUrl" )
            .setContent( () -> mockMedia( "123456", "mycontent.png" ) )
            .setProjectName( () -> ProjectName.from( "myproject" ) )
            .setBranch( () -> Branch.from( "draft" ) )
            .setName( "mycontent.png" )
            .build();

        final String url = this.service.attachmentUrl( params );

        assertEquals( "baseUrl/_/media:attachment/myproject:draft/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png", url );
    }

    @Test
    void attachmentUrl_byLabel()
    {
        final AttachmentUrlGeneratorParams params = AttachmentUrlGeneratorParams.create()
            .setBaseUrl( "baseUrl" )
            .setContent( () -> mockMediaWithLabel( "123456", "mycontent.png", "myLabel" ) )
            .setProjectName( () -> ProjectName.from( "myproject" ) )
            .setBranch( () -> Branch.from( "draft" ) )
            .setLabel( "myLabel" )
            .build();

        final String url = this.service.attachmentUrl( params );

        assertEquals( "baseUrl/_/media:attachment/myproject:draft/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png", url );
    }

    @Test
    void attachmentUrl_unknownLabel()
    {
        final AttachmentUrlGeneratorParams params = AttachmentUrlGeneratorParams.create()
            .setBaseUrl( "baseUrl" )
            .setContent( () -> mockMediaWithLabel( "123456", "mycontent.png", "myLabel" ) )
            .setProjectName( () -> ProjectName.from( "myproject" ) )
            .setBranch( () -> Branch.from( "draft" ) )
            .setLabel( "unknownLabel" )
            .build();

        final String url = this.service.attachmentUrl( params );

        assertThat( url ).startsWith( "/_/error/500?message=Something+went+wrong." );
    }

    @Test
    void attachmentUrl_unknownName()
    {
        final AttachmentUrlGeneratorParams params = AttachmentUrlGeneratorParams.create()
            .setBaseUrl( "baseUrl" )
            .setContent( () -> mockMedia( "123456", "mycontent.png" ) )
            .setProjectName( () -> ProjectName.from( "myproject" ) )
            .setBranch( () -> Branch.from( "draft" ) )
            .setName( "unknownName" )
            .build();

        final String url = this.service.attachmentUrl( params );

        assertThat( url ).startsWith( "/_/error/500?message=Something+went+wrong." );
    }

    @Test
    void apiUrl_basic()
    {
        PortalRequestAccessor.set( null );

        final ApiUrlGeneratorParams params = ApiUrlGeneratorParams.create()
            .setDescriptorKey( DescriptorKey.from( "com.enonic.app.myapp:myapi" ) )
            .setPath( () -> "some/path" )
            .setBaseUrl( "baseUrl" )
            .build();

        final String url = this.service.apiUrl( params );

        assertEquals( "baseUrl/_/com.enonic.app.myapp:myapi/some/path", url );
    }

    @Test
    void apiUrl_withQueryParams()
    {
        PortalRequestAccessor.set( null );

        final ApiUrlGeneratorParams params = ApiUrlGeneratorParams.create()
            .setDescriptorKey( DescriptorKey.from( "com.enonic.app.myapp:myapi" ) )
            .setBaseUrl( "baseUrl" )
            .setQueryParam( "k1", "v1" )
            .setQueryParam( "k2", "v2" )
            .build();

        final String url = this.service.apiUrl( params );

        assertEquals( "baseUrl/_/com.enonic.app.myapp:myapi?k1=v1&k2=v2", url );
    }

    @Test
    void apiUrl_withBaseUrl()
    {
        PortalRequestAccessor.set( null );

        final ApiUrlGeneratorParams params = ApiUrlGeneratorParams.create()
            .setDescriptorKey( DescriptorKey.from( "com.enonic.app.myapp:myapi" ) )
            .setBaseUrl( "https://example.com" )
            .build();

        final String url = this.service.apiUrl( params );

        assertEquals( "https://example.com/_/com.enonic.app.myapp:myapi", url );
    }

    @Test
    void apiUrl_noRequestContext()
    {
        PortalRequestAccessor.set( null );

        final ApiUrlGeneratorParams params = ApiUrlGeneratorParams.create()
            .setDescriptorKey( DescriptorKey.from( "com.enonic.app.myapp:myapi" ) )
            .setPath( () -> "path" )
            .build();

        final String url = ContextBuilder.create().build().callWith( () -> this.service.apiUrl( params ) );

        assertEquals( "/api/com.enonic.app.myapp:myapi/path", url );
    }

    @Test
    void generateUrl_basic()
    {
        final UrlGeneratorParams params = UrlGeneratorParams.create()
            .setBaseUrl( () -> "https://example.com" )
            .setPath( () -> "/my/path" )
            .setQueryString( () -> "?a=1&b=2" )
            .build();

        final String url = this.service.generateUrl( params );

        assertEquals( "https://example.com/my/path?a=1&b=2", url );
    }

    @Test
    void generateUrl_withNullSuppliers()
    {
        final UrlGeneratorParams params = UrlGeneratorParams.create().build();

        final String url = this.service.generateUrl( params );

        assertEquals( "", url );
    }

    @Test
    void imageUrlParts_basic()
    {
        final ImageUrlPartsParams params = ImageUrlPartsParams.create()
            .setMedia( () -> mockMedia( "123456", "mycontent.png" ) )
            .setProjectName( () -> ProjectName.from( "myproject" ) )
            .setBranch( () -> Branch.from( "draft" ) )
            .setScale( "max(300)" )
            .setQuality( 85 )
            .build();

        final ImageUrlParts parts = this.service.imageUrlParts( params );

        assertEquals( "/media:image/myproject:draft/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
                      parts.path() );
        assertEquals( "?quality=85", parts.queryString() );
        assertEquals( "myproject:draft", parts.context() );
        assertEquals( "123456", parts.id() );
        assertEquals( "0a350f43700951cdcca1574f448a7e22", parts.fingerprint() );
        assertEquals( "max-300", parts.scale() );
        assertEquals( "mycontent.png", parts.name() );
    }

    @Test
    void imageUrlParts_matchImageUrl()
    {
        final ImageUrlParts parts = this.service.imageUrlParts( ImageUrlPartsParams.create()
                                                                    .setMedia( () -> mockMedia( "123456", "my content.png" ) )
                                                                    .setProjectName( () -> ProjectName.from( "myproject" ) )
                                                                    .setBranch( () -> Branch.from( "master" ) )
                                                                    .setScale( "block(800,200)" )
                                                                    .setFilter( "blur(3)" )
                                                                    .setFormat( "webp" )
                                                                    .setQueryParam( "a", "1" )
                                                                    .build() );
        final String url = this.service.imageUrl( ImageUrlGeneratorParams.create()
                                                      .setMedia( () -> mockMedia( "123456", "my content.png" ) )
                                                      .setProjectName( () -> ProjectName.from( "myproject" ) )
                                                      .setBranch( () -> Branch.from( "master" ) )
                                                      .setScale( "block(800,200)" )
                                                      .setFilter( "blur(3)" )
                                                      .setFormat( "webp" )
                                                      .setQueryParam( "a", "1" )
                                                      .setBaseUrl( "https://media.example.com" )
                                                      .build() );

        // the invariant for building URLs from parts, with the media APIs served under the mount's "_" segment
        assertEquals( url, "https://media.example.com/_" + parts.path() + parts.queryString() );
    }

    @Test
    void attachmentUrlParts_basic()
    {
        final AttachmentUrlPartsParams params = AttachmentUrlPartsParams.create()
            .setContent( () -> mockMedia( "123456", "mycontent.png" ) )
            .setProjectName( () -> ProjectName.from( "myproject" ) )
            .setBranch( () -> Branch.from( "master" ) )
            .setDownload( true )
            .build();

        final AttachmentUrlParts parts = this.service.attachmentUrlParts( params );

        assertEquals( "/media:attachment/myproject/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png", parts.path() );
        assertEquals( "?download", parts.queryString() );
        assertEquals( "myproject", parts.context() );
        assertEquals( "123456", parts.id() );
        assertEquals( "ec25d6e4126c7064f82aaab8b34693fc", parts.fingerprint() );
        assertEquals( "mycontent.png", parts.name() );
    }

    @Test
    void imageUrlParts_byIdInTheContextProjectAndBranch()
    {
        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( ContentId.from( "123456" ) ) ).thenAnswer( invocation -> {
            assertEquals( "com.enonic.cms.myproject", ContextAccessor.current().getRepositoryId().toString() );
            assertEquals( "draft", ContextAccessor.current().getBranch().toString() );
            return media;
        } );

        final ImageUrlParts parts = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .branch( Branch.from( "draft" ) )
            .build()
            .callWith( () -> this.service.imageUrlParts( ImageUrlPartsParams.create().setId( "123456" ).setScale( "max(300)" ).build() ) );

        assertEquals( "/media:image/myproject:draft/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png", parts.path() );
    }

    @Test
    void imageUrlParts_ofAnotherMediaFails()
    {
        final Media document = mockMedia( "123456", "report.pdf" );
        when( document.getType() ).thenReturn( ContentTypeName.documentMedia() );

        final ImageUrlPartsParams params = ImageUrlPartsParams.create()
            .setMedia( () -> document )
            .setProjectName( () -> ProjectName.from( "myproject" ) )
            .setBranch( () -> Branch.from( "master" ) )
            .setScale( "max(300)" )
            .build();

        assertThrows( IllegalArgumentException.class, () -> this.service.imageUrlParts( params ) );
    }

    @Test
    void imageUrlParts_ofImageServedAsStored()
    {
        final Attachment animation =
            Attachment.create().name( "logo.gif" ).mimeType( "image/gif" ).sha512( "ec25d6e4126c7064f82aaab8b34693fc" ).label( "source" ).build();
        final Media media = mockMedia( "123456", "logo.gif" );
        when( media.getAttachments() ).thenReturn( Attachments.from( animation ) );

        final ImageUrlPartsParams params = ImageUrlPartsParams.create()
            .setMedia( () -> media )
            .setProjectName( () -> ProjectName.from( "myproject" ) )
            .setBranch( () -> Branch.from( "master" ) )
            .setScale( "block(800,200)" )
            .setFilter( "blur(3)" )
            .setQuality( 85 )
            .setBackground( "ff0000" )
            .setFormat( "webp" )
            .setQueryParam( "a", "1" )
            .build();

        // served as stored: one URL, whatever the processing asked for
        final ImageUrlParts parts = this.service.imageUrlParts( params );
        assertEquals( "/media:image/myproject/123456:0a350f43700951cdcca1574f448a7e22/full/logo.gif", parts.path() );
        assertEquals( "full", parts.scale() );
        assertEquals( "?a=1", parts.queryString() );

        final String url = this.service.imageUrl( ImageUrlGeneratorParams.create()
                                                      .setMedia( () -> media )
                                                      .setProjectName( () -> ProjectName.from( "myproject" ) )
                                                      .setBranch( () -> Branch.from( "master" ) )
                                                      .setScale( "block(800,200)" )
                                                      .setFilter( "blur(3)" )
                                                      .setQuality( 85 )
                                                      .setFormat( "webp" )
                                                      .setQueryParam( "a", "1" )
                                                      .setBaseUrl( "https://media.example.com" )
                                                      .build() );
        assertEquals( "https://media.example.com/_" + parts.path() + parts.queryString(), url );
    }

    @Test
    void imageUrlParts_requireMediaOrKey()
    {
        assertThrows( IllegalArgumentException.class, () -> ImageUrlPartsParams.create().setScale( "max(300)" ).build() );
        assertThrows( IllegalArgumentException.class, () -> ImageUrlPartsParams.create()
            .setMedia( () -> mockMedia( "123456", "mycontent.png" ) )
            .setId( "123456" )
            .setScale( "max(300)" )
            .build() );
    }

    @Test
    void attachmentUrlParts_byPath()
    {
        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getByPath( ContentPath.from( "/a/mycontent.png" ) ) ).thenReturn( media );

        final AttachmentUrlParts parts = this.service.attachmentUrlParts( AttachmentUrlPartsParams.create()
                                                                              .setPath( "/a/mycontent.png" )
                                                                              .setProjectName( () -> ProjectName.from( "myproject" ) )
                                                                              .setBranch( () -> Branch.from( "master" ) )
                                                                              .build() );

        assertEquals( "/media:attachment/myproject/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png", parts.path() );
    }

    @Test
    void attachmentUrlParts_ofMissingContentFails()
    {
        when( contentService.getById( ContentId.from( "missing" ) ) ).thenReturn( null );

        final AttachmentUrlPartsParams params = AttachmentUrlPartsParams.create()
            .setId( "missing" )
            .setProjectName( () -> ProjectName.from( "myproject" ) )
            .setBranch( () -> Branch.from( "master" ) )
            .build();

        assertThrows( ContentNotFoundException.class, () -> this.service.attachmentUrlParts( params ) );
    }

    @Test
    void attachmentUrlParts_matchAttachmentUrl()
    {
        final AttachmentUrlParts parts = this.service.attachmentUrlParts( AttachmentUrlPartsParams.create()
                                                                              .setContent( () -> mockMedia( "123456", "mycontent.png" ) )
                                                                              .setProjectName( () -> ProjectName.from( "myproject" ) )
                                                                              .setBranch( () -> Branch.from( "draft" ) )
                                                                              .setDownload( true )
                                                                              .setQueryParam( "a", "1" )
                                                                              .build() );
        final String url = this.service.attachmentUrl( AttachmentUrlGeneratorParams.create()
                                                           .setContent( () -> mockMedia( "123456", "mycontent.png" ) )
                                                           .setProjectName( () -> ProjectName.from( "myproject" ) )
                                                           .setBranch( () -> Branch.from( "draft" ) )
                                                           .setDownload( true )
                                                           .setQueryParam( "a", "1" )
                                                           .setBaseUrl( "https://media.example.com" )
                                                           .build() );

        assertEquals( url, "https://media.example.com/_" + parts.path() + parts.queryString() );
    }

    private Media mockMedia( final String id, final String name )
    {
        final Attachment attachment =
            Attachment.create().name( name ).mimeType( "image/png" ).sha512( "ec25d6e4126c7064f82aaab8b34693fc" ).label( "source" ).build();

        final Media media = mock( Media.class );

        final ContentId contentId = ContentId.from( id );

        when( media.getId() ).thenReturn( contentId );
        when( media.getPath() ).thenReturn( ContentPath.from( "/" + id ) );
        when( media.getName() ).thenReturn( ContentName.from( name ) );
        when( media.getType() ).thenReturn( ContentTypeName.imageMedia() );
        when( media.getData() ).thenReturn( new PropertyTree() );
        when( media.getAttachments() ).thenReturn( Attachments.from( attachment ) );

        return media;
    }

    private Media mockMediaWithLabel( final String id, final String name, final String label )
    {
        final Attachment attachment =
            Attachment.create().name( name ).mimeType( "image/png" ).sha512( "ec25d6e4126c7064f82aaab8b34693fc" ).label( label ).build();

        final Media media = mock( Media.class );

        final ContentId contentId = ContentId.from( id );

        when( media.getId() ).thenReturn( contentId );
        when( media.getPath() ).thenReturn( ContentPath.from( "/" + id ) );
        when( media.getName() ).thenReturn( ContentName.from( name ) );
        when( media.getType() ).thenReturn( ContentTypeName.imageMedia() );
        when( media.getData() ).thenReturn( new PropertyTree() );
        when( media.getAttachments() ).thenReturn( Attachments.from( attachment ) );

        return media;
    }
}
