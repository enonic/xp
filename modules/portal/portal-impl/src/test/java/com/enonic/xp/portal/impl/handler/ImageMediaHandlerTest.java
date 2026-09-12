package com.enonic.xp.portal.impl.handler;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.portal.impl.MediaHashResolver;
import com.enonic.xp.portal.impl.HmacTestHelper;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ImageMediaHandlerTest
{
    private ImageMediaHandler handler;

    private PortalRequest request;

    private ContentService contentService;

    private ImageService imageService;

    @BeforeEach
    final void setup()
    {
        this.contentService = mock( ContentService.class );
        this.imageService = mock( ImageService.class );

        this.handler = new ImageMediaHandler( this.contentService, mock( ProjectService.class ), this.imageService, HmacTestHelper.createHmacService() );
        final PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        this.handler.activate( portalConfig );

        this.request = new PortalRequest();
        this.request.setMethod( HttpMethod.GET );
        this.request.setRepositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) );
        this.request.setBranch( ContentConstants.BRANCH_MASTER );
        this.request.setBaseUri( "/site" );
        this.request.setContentPath( ContentPath.from( "/" ) );
    }

    private String styledFingerprint()
    {
        final Media media = (Media) contentService.getById( ContentId.from( "123456" ) );
        return MediaHashResolver.resolveStyledImageHash( MediaHashResolver.resolveImageHash( media ),
            ImageStyle.create().name( "card" ).build(), new ScaleParams( "width", new Object[]{640} ), HmacTestHelper.createHmacService() );
    }

    @ParameterizedTest
    @ValueSource(strings = {"webp", "avif", "svg+xml"})
    void styledModernSourceUsesConfiguredDecoder( final String sourceFormat )
        throws Exception
    {
        final Attachment attachment = Attachment.create().name( "source" ).mimeType( "image/" + sourceFormat )
            .label( "source" ).sha512( "ec25d6e4126c7064f82aaab8b34693fc" ).build();
        final Content content = createContent( "123456", "path/to/image", attachment );
        when( contentService.getById( content.getId() ) ).thenReturn( content );
        when( imageService.getStyle( "app:card" ) ).thenReturn( ImageStyle.create().name( "card" ).build() );
        when( imageService.readImage( isA( ReadImageParams.class ) ) ).thenReturn( ByteSource.wrap( new byte[]{1} ) );
        request.setRawPath( "/site/myproject/master/_/media:image/myproject/123456:" + styledFingerprint() +
            "/width-640~app:card/image.png" );
        final WebResponse response = handler.handle( request );
        assertEquals( HttpStatus.OK, response.getStatus() );
        assertEquals( MediaType.PNG, response.getContentType() );
        final ArgumentCaptor<ReadImageParams> params = ArgumentCaptor.forClass( ReadImageParams.class );
        verify( imageService ).readImage( params.capture() );
        assertEquals( "app:card", params.getValue().getStyle() );
        assertFalse( params.getValue().isCacheOnly() );
        verify( contentService, never() ).getBinary( isA( ContentId.class ), isA( BinaryReference.class ) );
    }

    @ParameterizedTest
    @ValueSource(strings = {"webp", "avif", "WEBP", "AVIF"})
    void modernCacheMissWithInvalidFingerprintCannotRegenerate( final String format )
        throws Exception
    {
        setupContent();
        when( imageService.readImage( isA( ReadImageParams.class ) ) ).thenAnswer( invocation -> {
            assertTrue( ((ReadImageParams) invocation.getArgument( 0 )).isCacheOnly() );
            throw new IllegalArgumentException( "Image is not cached" );
        } );
        when( imageService.getStyle( "app:card" ) ).thenReturn( ImageStyle.create().name( "card" ).build() );
        final String valid = styledFingerprint();
        final String source = MediaHashResolver.resolveImageHash( (Media) contentService.getById( ContentId.from( "123456" ) ) );
        for ( HttpMethod method : new HttpMethod[]{HttpMethod.GET, HttpMethod.HEAD} )
        {
            request.setMethod( method );
            for ( String path : new String[]{"123456/width-640~app:card", "123456:00000000000000000000000000000000/width-640~app:card",
                "123456:" + source + "/width-640~app:card", "123456:f4774dff7b6ef5d0fc1f077cbec55899/width-640~app:card", "123456:" + valid + "/width-320~app:card"} )
            {
                request.setRawPath( "/site/myproject/master/_/media:image/myproject/" + path + "/image-name.jpg." + format );
                assertEquals( HttpStatus.BAD_REQUEST, assertThrows( WebException.class, () -> handler.handle( request ) ).getStatus() );
            }
            when( imageService.getStyle( "app:card" ) ).thenReturn( ImageStyle.create().name( "card" ).quality( 70 ).build() );
            request.setRawPath( "/site/myproject/master/_/media:image/myproject/123456:" + valid + "/width-640~app:card/image-name.jpg." + format );
            assertEquals( HttpStatus.BAD_REQUEST, assertThrows( WebException.class, () -> handler.handle( request ) ).getStatus() );
            when( imageService.getStyle( "app:card" ) ).thenReturn( ImageStyle.create().name( "card" ).build() );
        }
        verify( contentService, never() ).getBinary( isA( ContentId.class ), isA( BinaryReference.class ) );
    }

    @ParameterizedTest
    @ValueSource(strings = {"webp", "avif", "jpeg", "png"})
    void wrongFingerprintServesCacheHitWithoutImmutableHeaders( final String format )
        throws Exception
    {
        setupContent();
        final ByteSource cached = ByteSource.wrap( new byte[]{1, 2, 3} );
        when( imageService.getStyle( "app:card" ) ).thenReturn( ImageStyle.create().name( "card" ).build() );
        when( imageService.readImage( isA( ReadImageParams.class ) ) ).thenAnswer( invocation -> {
            assertTrue( ((ReadImageParams) invocation.getArgument( 0 )).isCacheOnly() );
            return cached;
        } );
        for ( HttpMethod method : new HttpMethod[]{HttpMethod.GET, HttpMethod.HEAD} )
        {
            request.setMethod( method );
            request.setRawPath( "/site/myproject/master/_/media:image/myproject/123456:00000000000000000000000000000000/width-640~app:card/image-name.jpg." + format );
            final WebResponse response = handler.handle( request );
            assertEquals( HttpStatus.OK, response.getStatus() );
            assertEquals( cached, response.getBody() );
            assertNull( response.getHeaders().get( "Cache-Control" ) );
            if ( "webp".equals( format ) || "avif".equals( format ) )
            {
                request.setRawPath( "/site/myproject/master/_/media:image/myproject/123456/width-640~app:card/image-name.jpg." + format );
                assertEquals( HttpStatus.OK, handler.handle( request ).getStatus() );
            }
        }
        verify( contentService, never() ).getBinary( isA( ContentId.class ), isA( BinaryReference.class ) );
    }

    @ParameterizedTest
    @ValueSource(strings = {"webp", "avif", "WEBP", "AVIF", "jpeg", "png"})
    void styleAllowsRequestedOutputFormat( final String format )
        throws Exception
    {
        setupContent();
        request.setRawPath( "/site/myproject/master/_/media:image/myproject/123456:b70c37373c28d80778129544e9b504a6a0560ed0/width-640~app:card/image-name.jpg." + format );
        when( imageService.getStyle( "app:card" ) ).thenReturn(
            ImageStyle.create().name( "card" ).build() );
        final WebResponse response = handler.handle( request );
        assertEquals( MediaType.parse( "image/" + format.toLowerCase( java.util.Locale.ROOT ) ), response.getContentType() );
        final ArgumentCaptor<ReadImageParams> params = ArgumentCaptor.forClass( ReadImageParams.class );
        verify( imageService ).readImage( params.capture() );
        assertEquals( "app:card", params.getValue().getStyle() );
        assertFalse( params.getValue().isCacheOnly() );
        assertEquals( "image/" + format.toLowerCase( java.util.Locale.ROOT ), params.getValue().getMimeType() );
    }

    @Test
    void styleFingerprintControlsCaching()
        throws Exception
    {
        setupContent();
        final ImageStyle style = ImageStyle.create().name( "card" ).build();
        when( imageService.getStyle( "app:card" ) ).thenReturn( style );
        request.setRawPath( "/site/myproject/master/_/media:image/myproject/123456:b70c37373c28d80778129544e9b504a6a0560ed0/width-640~app:card/image-name.jpg" );
        assertEquals( "public, max-age=31536000, immutable", handler.handle( request ).getHeaders().get( "Cache-Control" ) );
        when( imageService.getStyle( "app:card" ) ).thenReturn(
            ImageStyle.create().name( "card" ).quality( 70 ).build() );
        assertNull( handler.handle( request ).getHeaders().get( "Cache-Control" ) );
    }

    @ParameterizedTest
    @ValueSource(strings = {"scale", "format", "quality", "filter", "background"})
    void styleRejectsEvenEmptyOverridesBeforeReadingContent( final String parameter )
    {
        request.setRawPath( "/site/myproject/master/_/media:image/myproject/123456/full~app:card/image-name.jpg" );
        request.getParams().put( parameter, "" );
        final WebException error = assertThrows( WebException.class, () -> handler.handle( request ) );
        assertEquals( HttpStatus.BAD_REQUEST, error.getStatus() );
        verifyNoInteractions( contentService, imageService );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "~app:card"})
    void styleRejectsQueryStyle( final String suffix )
    {
        request.setRawPath( "/site/myproject/master/_/media:image/myproject/123456/width-640" + suffix + "/image-name.jpg" );
        request.getParams().put( "style", "app:other" );
        assertEquals( HttpStatus.BAD_REQUEST, assertThrows( WebException.class, () -> handler.handle( request ) ).getStatus() );
        verifyNoInteractions( contentService, imageService );
    }

    @ParameterizedTest
    @ValueSource(strings = {"width-640~", "~app:card", "width-640~card", "width-640~:card", "width-640~app:card~app:other"})
    void malformedStyleSegmentIsRejectedBeforeReadingContent( final String segment )
    {
        request.setRawPath( "/site/myproject/master/_/media:image/myproject/123456/" + segment + "/image-name.jpg" );
        assertEquals( HttpStatus.BAD_REQUEST, assertThrows( WebException.class, () -> handler.handle( request ) ).getStatus() );
        verifyNoInteractions( contentService, imageService );
    }

    @ParameterizedTest
    @ValueSource(strings = {"com.example.site:card-wide", "com.example.site:card~wide"})
    void pathStylePreservesQualifiedAliasAndScale( final String alias )
        throws Exception
    {
        setupContent();
        when( imageService.getStyle( alias ) ).thenReturn( ImageStyle.create().name( "card-wide" ).build() );
        request.setRawPath( "/site/myproject/master/_/media:image/myproject/123456/width-640~" + alias + "/image-name.jpg" );
        assertEquals( HttpStatus.OK, handler.handle( request ).getStatus() );
        final ArgumentCaptor<ReadImageParams> params = ArgumentCaptor.forClass( ReadImageParams.class );
        verify( imageService ).readImage( params.capture() );
        assertEquals( alias, params.getValue().getStyle() );
        assertEquals( "width", params.getValue().getScaleParams().getName() );
        assertArrayEquals( new Object[]{640}, params.getValue().getScaleParams().getArguments() );
    }

    @Test
    void unknownStyleIsBadRequest()
    {
        request.setRawPath( "/site/myproject/master/_/media:image/myproject/123456/full~app:missing/image-name.jpg" );
        when( imageService.getStyle( "app:missing" ) ).thenThrow( new IllegalArgumentException( "Unknown style" ) );
        assertEquals( HttpStatus.BAD_REQUEST, assertThrows( WebException.class, () -> handler.handle( request ) ).getStatus() );
        verifyNoInteractions( contentService );
    }

    @ParameterizedTest
    @ValueSource(strings = {"webp", "avif", "WEBP", "AVIF"})
    void modernOutputRequiresStyle( final String format )
        throws Exception
    {
        setupContent();
        request.setRawPath( "/site/myproject/master/_/media:image/myproject/123456/full/image-name.jpg." + format );
        assertEquals( HttpStatus.BAD_REQUEST, assertThrows( WebException.class, () -> handler.handle( request ) ).getStatus() );
        verifyNoInteractions( imageService );
    }

    @Test
    void testOptions()
    {
        this.request.setBaseUri( "" );
        this.request.setRawPath( "/api/media:image/myproject:draft/123456/scale-100-100/logo.png" );
        this.request.setMethod( HttpMethod.OPTIONS );

        final WebResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,HEAD,OPTIONS", res.getHeaders().get( "Allow" ) );
    }

    @Test
    void testHandleMethodNotAllowed()
    {
        this.request.setBaseUri( "" );
        this.request.setMethod( HttpMethod.DELETE );
        this.request.setRawPath( "/api/media:image/myproject:draft/123456/scale-100-100/logo.png" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, ex.getStatus() );
        assertEquals( "Method DELETE not allowed", ex.getMessage() );
    }

    @Test
    void testHandleInvalidUrl()
    {
        this.request.setBaseUri( "" );
        this.request.setMethod( HttpMethod.DELETE );
        this.request.setRawPath( "/api/media:image/myproject:draft/123456/logo.png" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "Not a valid media url pattern", ex.getMessage() );

        this.request.setRawPath( "/api/media:image/myproject:draft/123456" );

        ex = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "Not a valid media url pattern", ex.getMessage() );
    }

    @Test
    void testMediaScopeWithWrongContext()
    {
        ContextBuilder.copyOf( ContextAccessor.current() )
            .attribute( "media.scope", "project1, project1:draft, project2" )
            .authInfo( ContentConstants.CONTENT_SU_AUTH_INFO )
            .build()
            .runWith( () -> {
                this.request.setBaseUri( "" );
                this.request.setRawPath( "/api/media:image/project/123456/scale-100-100/image-name.jpg" );

                WebException ex = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
                assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );

                this.request.setBaseUri( "/site" );
                this.request.setRawPath( "/site/project/branch/_/media:image/project/123456/scale-100-100/image-name.jpg" );

                ex = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
                assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );

                this.request.setBaseUri( "/site" );
                this.request.setRawPath( "/site/project/branch/_/media:image/project2:draft/123456/scale-100-100/image-name.jpg" );

                ex = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
                assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
            } );
    }

    @Test
    void testMediaScope()
        throws Exception
    {
        setupContent();

        ContextBuilder.copyOf( ContextAccessor.current() )
            .attribute( "media.scope", "myproject, myproject:draft, myproject2:draft" )
            .authInfo( ContentConstants.CONTENT_SU_AUTH_INFO )
            .build()
            .runWith( () -> {
                try
                {
                    WebRequest webRequest = new WebRequest();

                    webRequest.setMethod( HttpMethod.GET );
                    webRequest.setRawPath( "/api/media:image/myproject/123456/scale-100-100/image-name.jpg" );

                    WebResponse webResponse = this.handler.handle( webRequest );
                    assertEquals( HttpStatus.OK, webResponse.getStatus() );

                    webRequest.setRawPath( "/api/media:image/myproject:draft/123456/scale-100-100/image-name.jpg" );
                    webResponse = this.handler.handle( webRequest );
                    assertEquals( HttpStatus.OK, webResponse.getStatus() );

                    this.request.setRepositoryId( ProjectName.from( "myproject" ).getRepoId() );
                    this.request.setBranch( ContentConstants.BRANCH_DRAFT );

                    this.request.setBaseUri( "/admin/site/preview" );
                    this.request.setRawPath(
                        "/admin/site/preview/myproject/draft/_/media:image/myproject:draft/123456/scale-100-100/image-name.jpg" );
                    webResponse = this.handler.handle( this.request );
                    assertEquals( HttpStatus.OK, webResponse.getStatus() );

                    this.request.setRepositoryId( ProjectName.from( "myproject" ).getRepoId() );
                    this.request.setBranch( ContentConstants.BRANCH_DRAFT );

                    this.request.setBaseUri( "/admin/site/preview" );
                    this.request.setRawPath(
                        "/admin/site/preview/myproject/draft/_/media:image/myproject:draft/123456/scale-100-100/image-name.jpg" );
                    webResponse = this.handler.handle( this.request );
                    assertEquals( HttpStatus.OK, webResponse.getStatus() );

                    this.request.setRepositoryId( ProjectName.from( "myproject" ).getRepoId() );
                    this.request.setBranch( ContentConstants.BRANCH_DRAFT );

                    this.request.setBaseUri( "/admin/site/preview" );
                    this.request.setRawPath(
                        "/admin/site/preview/myproject/draft/_/media:image/myproject2:draft/123456/scale-100-100/image-name.jpg" );
                    webResponse = this.handler.handle( this.request );
                    assertEquals( HttpStatus.OK, webResponse.getStatus() );

                    this.request.setRepositoryId( ProjectName.from( "unknown" ).getRepoId() );
                    this.request.setBranch( ContentConstants.BRANCH_DRAFT );

                    this.request.setBaseUri( "/admin/site/preview" );
                    this.request.setRawPath(
                        "/admin/site/preview/myproject/draft/_/media:image/unknown:draft/123456/scale-100-100/image-name.jpg" );
                    WebException ex = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
                    assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
                }
                catch ( Exception e )
                {
                    throw new RuntimeException( e );
                }
            } );
    }

    @Test
    void svgzImage()
        throws Exception
    {
        setupContentSvgz();

        this.request.setBaseUri( "/admin/site/preview" );
        this.request.setRawPath( "/admin/site/preview/myproject/master/_/media:image/myproject/123456/full/image-name.svgz" );

        final WebResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.SVG_UTF_8.withoutParameters(), res.getContentType() );
        assertInstanceOf( ByteSource.class, res.getBody() );
        assertEquals( "gzip", res.getHeaders().get( "Content-Encoding" ) );
        assertEquals( "default-src 'none'; base-uri 'none'; form-action 'none'; style-src 'self' 'unsafe-inline'",
                      res.getHeaders().get( "Content-Security-Policy" ) );
    }

    @Test
    void testGifImage()
        throws Exception
    {
        setupContentGif();

        this.request.setBaseUri( "/site" );
        this.request.setRawPath( "/site/myproject/master/sitepath/_/media:image/myproject/123456/full/image-name.gif" );

        final WebResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.GIF, res.getContentType() );
        assertInstanceOf( ByteSource.class, res.getBody() );
        assertNull( res.getHeaders().get( "Content-Encoding" ) );
    }

    private void setupContentSvgz()
        throws Exception
    {
        final Attachment attachment = Attachment.create()
            .name( "enonic-logo.svgz" )
            .mimeType( "image/svg+xml" )
            .label( "source" )
            .sha512( "ec25d6e4126c7064f82aaab8b34693fc" )
            .build();

        final Content content = createContent( "123456", "path/to/image-name.svgz", attachment );

        when( this.contentService.getById( eq( content.getId() ) ) ).thenReturn( content );
        when( this.contentService.getByPath( eq( content.getPath() ) ) ).thenReturn( content );

        final ByteSource imageBytes = ByteSource.wrap( new byte[0] );

        when( this.contentService.getBinary( isA( ContentId.class ), isA( BinaryReference.class ) ) ).thenReturn( imageBytes );

        when( this.imageService.readImage( isA( ReadImageParams.class ) ) ).thenReturn( imageBytes );
    }

    private void setupContentGif()
        throws Exception
    {
        final Attachment attachment = Attachment.create()
            .name( "enonic-logo.svg" )
            .mimeType( "image/gif" )
            .label( "source" )
            .sha512( "ec25d6e4126c7064f82aaab8b34693fc" )
            .build();

        final Content content = createContent( "123456", "path/to/image-name.gif", attachment );

        when( this.contentService.getById( eq( content.getId() ) ) ).thenReturn( content );
        when( this.contentService.getByPath( eq( content.getPath() ) ) ).thenReturn( content );

        final ByteSource imageBytes = ByteSource.wrap( new byte[0] );

        when( this.contentService.getBinary( isA( ContentId.class ), isA( BinaryReference.class ) ) ).thenReturn( imageBytes );

        when( this.imageService.readImage( isA( ReadImageParams.class ) ) ).thenReturn( imageBytes );
    }

    private void setupContent()
        throws Exception
    {
        final Attachment attachment = Attachment.create()
            .name( "enonic-logo.png" )
            .mimeType( "image/png" )
            .label( "source" )
            .sha512( "ec25d6e4126c7064f82aaab8b34693fc" )
            .build();

        final Content content = createContent( "123456", "path/to/image-name.jpg", attachment );

        when( this.contentService.getById( eq( content.getId() ) ) ).thenReturn( content );
        when( this.contentService.getByPath( eq( content.getPath() ) ) ).thenReturn( content );

        final ByteSource imageBytes = ByteSource.wrap( new byte[0] );

        when( this.contentService.getBinary( isA( ContentId.class ), isA( BinaryReference.class ) ) ).thenReturn( imageBytes );

        when( this.imageService.readImage( isA( ReadImageParams.class ) ) ).thenReturn( imageBytes );
    }

    private Content createContent( final String id, final String contentPath, final Attachment... attachments )
    {
        final PropertyTree data = new PropertyTree();
        data.addSet( "media" ).addString( "attachment", attachments[0].getName() );

        return Media.create()
            .id( ContentId.from( id ) )
            .path( contentPath )
            .createdTime( Instant.now() )
            .type( ContentTypeName.imageMedia() )
            .permissions( AccessControlList.create()
                              .add( AccessControlEntry.create().principal( RoleKeys.EVERYONE ).allow( Permission.READ ).build() )
                              .build() )
            .owner( PrincipalKey.from( "user:myStore:me" ) )
            .displayName( "My Content" )
            .modifiedTime( Instant.now() )
            .modifier( PrincipalKey.from( "user:system:admin" ) )
            .data( data )
            .attachments( Attachments.from( attachments ) )
            .build();
    }
}
