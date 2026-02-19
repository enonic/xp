package com.enonic.xp.portal.impl.url;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.Media;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.url.ApiUrlGeneratorParams;
import com.enonic.xp.portal.url.AttachmentUrlGeneratorParams;
import com.enonic.xp.portal.url.ImageUrlGeneratorParams;
import com.enonic.xp.portal.url.PortalUrlGeneratorService;
import com.enonic.xp.portal.url.UrlGeneratorParams;
import com.enonic.xp.project.ProjectName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PortalUrlGeneratorServiceImplTest
{
    private PortalUrlGeneratorService service;

    @BeforeEach
    void setUp()
    {
        this.service = new PortalUrlGeneratorServiceImpl();
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

        assertEquals( "baseUrl/_/media:image/myproject:draft/123456:b12b4c973748042e3b3a7e4798344289/max-300/mycontent.png", url );
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

        assertEquals( "baseUrl/_/media:image/myproject/123456:b12b4c973748042e3b3a7e4798344289/max-300/mycontent.png", url );
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
            "baseUrl/_/media:image/myproject:draft/123456:b12b4c973748042e3b3a7e4798344289/max-300/mycontent.png?quality=85&background=0x000000&filter=blur%283%29",
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

        assertEquals( "baseUrl/_/media:image/myproject:draft/123456:b12b4c973748042e3b3a7e4798344289/max-300/mycontent.png.webp", url );
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

        assertEquals( "baseUrl/_/media:image/myproject:draft/123456:b12b4c973748042e3b3a7e4798344289/max-300/mycontent.png?ts=123", url );
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

    private Media mockMedia( final String id, final String name )
    {
        final Attachment attachment =
            Attachment.create().name( name ).mimeType( "image/png" ).sha512( "ec25d6e4126c7064f82aaab8b34693fc" ).label( "source" ).build();

        final Media media = mock( Media.class );

        final ContentId contentId = ContentId.from( id );

        when( media.getId() ).thenReturn( contentId );
        when( media.getPath() ).thenReturn( ContentPath.from( "/" + id ) );
        when( media.getName() ).thenReturn( ContentName.from( name ) );
        when( media.getMediaAttachment() ).thenReturn( attachment );
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
        when( media.getMediaAttachment() ).thenReturn( attachment );
        when( media.getAttachments() ).thenReturn( Attachments.from( attachment ) );

        return media;
    }
}
