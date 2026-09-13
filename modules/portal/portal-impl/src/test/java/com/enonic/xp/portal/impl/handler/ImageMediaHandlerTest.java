package com.enonic.xp.portal.impl.handler;

import java.time.Instant;
import java.util.Locale;
import java.util.Map;

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
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.HmacTestHelper;
import com.enonic.xp.portal.impl.MediaHashResolver;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.portal.impl.url.PortalUrlGeneratorServiceImpl;
import com.enonic.xp.portal.url.ImageUrlGeneratorParams;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.ImageStyleNotFoundException;
import com.enonic.xp.style.ImageStyleSettings;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.webapp.WebappService;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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


    @ParameterizedTest
    @ValueSource(strings = {"jpeg", "png", "gif", "webp", "avif"})
    void changingSignedOutputFormatIsCacheOnly( final String format )
        throws Exception
    {
        setupContent();
        when( imageService.getStyle( "app:card" ) ).thenReturn( ImageStyle.create().name( "card" ).build() );
        final String signedFormat = "png".equals( format ) ? "jpeg" : "png";
        request.setRawPath( "/site/myproject/master/_/media:image/myproject/123456:" + styledFingerprint( signedFormat ) +
            "/width-640~app:card/image-name.jpg." + format );
        for ( HttpMethod method : new HttpMethod[]{HttpMethod.GET, HttpMethod.HEAD} )
        {
            request.setMethod( method );
            when( imageService.readImage( isA( ReadImageParams.class ) ) ).thenAnswer( invocation -> {
                assertTrue( ((ReadImageParams) invocation.getArgument( 0 )).isCacheOnly() );
                return ByteSource.wrap( new byte[]{1} );
            } );
            assertEquals( HttpStatus.OK, handler.handle( request ).getStatus() );
            when( imageService.readImage( isA( ReadImageParams.class ) ) ).thenAnswer( invocation -> {
                assertTrue( ((ReadImageParams) invocation.getArgument( 0 )).isCacheOnly() );
                throw new IllegalArgumentException( "Image is not cached" );
            } );
            assertEquals( HttpStatus.BAD_REQUEST, assertThrows( WebException.class, () -> handler.handle( request ) ).getStatus() );
        }
        verify( contentService, never() ).getBinary( isA( ContentId.class ), isA( BinaryReference.class ) );
    }

    @ParameterizedTest
    @ValueSource(strings = {"jpeg", "png", "gif"})
    void legacyHashRemainsCacheOnlyWhenHashlessGenerationIsEnabled( final String format )
        throws Exception
    {
        setupContent();
        configureHashlessGeneration( true );
        final String legacy = MediaHashResolver.resolveImageHash( (Media) contentService.getById( ContentId.from( "123456" ) ) );
        for ( HttpMethod method : new HttpMethod[]{HttpMethod.GET, HttpMethod.HEAD} )
        {
            request.setMethod( method );
            for ( String scale : new String[]{"width-640", "width-320"} )
            {
                request.setRawPath( "/site/myproject/master/_/media:image/myproject/123456:" + legacy + "/" + scale + "/image-name.jpg." + format );
                when( imageService.readImage( isA( ReadImageParams.class ) ) ).thenAnswer( invocation -> {
                    assertTrue( ((ReadImageParams) invocation.getArgument( 0 )).isCacheOnly() );
                    return ByteSource.wrap( new byte[]{1} );
                } );
                assertNull( handler.handle( request ).getHeaders().get( "Cache-Control" ) );
                when( imageService.readImage( isA( ReadImageParams.class ) ) ).thenAnswer( invocation -> {
                    assertTrue( ((ReadImageParams) invocation.getArgument( 0 )).isCacheOnly() );
                    throw new IllegalArgumentException( "Image is not cached" );
                } );
                assertEquals( HttpStatus.BAD_REQUEST, assertThrows( WebException.class, () -> handler.handle( request ) ).getStatus() );
                request.setRawPath( "/site/myproject/master/_/media:image/myproject/123456/" + scale + "/image-name.jpg." + format );
                when( imageService.readImage( isA( ReadImageParams.class ) ) ).thenAnswer( invocation -> {
                    assertFalse( ((ReadImageParams) invocation.getArgument( 0 )).isCacheOnly() );
                    return ByteSource.wrap( new byte[]{1} );
                } );
                assertEquals( HttpStatus.OK, handler.handle( request ).getStatus() );
            }
        }
    }

    @Test
    void modernUnstyledFingerprintProtectsProcessingParameters()
        throws Exception
    {
        setupContent();
        final Media media = (Media) contentService.getById( ContentId.from( "123456" ) );
        final String signed = MediaHashResolver.resolveImageFingerprint( MediaHashResolver.resolveImageHash( media ),
            new ImageStyleSettings( null, null, 85, 0xffffff ), new ScaleParams( "width", new Object[]{640} ),
            "image/png", HmacTestHelper.createHmacService() );
        request.setRawPath( "/site/myproject/master/_/media:image/myproject/123456:" + signed + "/width-640/image-name.jpg" );
        when( imageService.readImage( isA( ReadImageParams.class ) ) ).thenAnswer( invocation -> {
            assertFalse( ((ReadImageParams) invocation.getArgument( 0 )).isCacheOnly() );
            return ByteSource.wrap( new byte[]{1} );
        } );
        assertEquals( HttpStatus.OK, handler.handle( request ).getStatus() );
        when( imageService.readImage( isA( ReadImageParams.class ) ) ).thenAnswer( invocation -> {
            assertTrue( ((ReadImageParams) invocation.getArgument( 0 )).isCacheOnly() );
            throw new IllegalArgumentException( "Image is not cached" );
        } );
        for ( var parameter : Map.of( "quality", "70", "background", "000000", "filter", "blur(1)" ).entrySet() )
        {
            request.getParams().put( parameter.getKey(), parameter.getValue() );
            assertEquals( HttpStatus.BAD_REQUEST, assertThrows( WebException.class, () -> handler.handle( request ) ).getStatus() );
            request.getParams().removeAll( parameter.getKey() );
        }
        request.setRawPath( "/site/myproject/master/_/media:image/myproject/123456:" + signed + "/width-320/image-name.jpg" );
        assertEquals( HttpStatus.BAD_REQUEST, assertThrows( WebException.class, () -> handler.handle( request ) ).getStatus() );
    }

    @ParameterizedTest
    @ValueSource(strings = {"jpeg", "png", "gif", "webp", "avif", "WEBP", "AVIF"})
    void generatedSignaturesAuthorizeTheSameRendition( final String format )
        throws Exception
    {
        setupContent();
        final var generator = new PortalUrlGeneratorServiceImpl(
            mock( WebappService.class ), mock( SiteService.class ), imageService,
            HmacTestHelper.createHmacService() );
        when( imageService.getStyle( "app:card" ) ).thenReturn(
            ImageStyle.create().name( "card" ).aspectRatio( "16:9" ).quality( 70 ).filter( "grayscale" ).build() );
        final Media media = (Media) contentService.getById( ContentId.from( "123456" ) );
        for ( boolean styled : new boolean[]{true, false} )
        {
            if ( !styled && ( "webp".equalsIgnoreCase( format ) || "avif".equalsIgnoreCase( format ) ) )
            {
                continue;
            }
            final var builder = ImageUrlGeneratorParams.create()
                .setMedia( () -> media ).setProjectName( () -> ProjectName.from( "myproject" ) )
                .setBranch( () -> ContentConstants.BRANCH_MASTER ).setScale( "width(640)" ).setFormat( format );
            if ( styled )
            {
                builder.setStyle( "app:card" );
            }
            else
            {
                builder.setQuality( 70 ).setFilter( "grayscale" ).setBackground( "000000" );
            }
            final var parts = generator.imageUrlParts( builder.build() );
            request.setRawPath( "/site/myproject/master/_" + parts.path() );
            request.getParams().clear();
            if ( !styled )
            {
                request.getParams().put( "quality", "70" );
                request.getParams().put( "filter", "grayscale" );
                request.getParams().put( "background", "000000" );
            }
            when( imageService.readImage( isA( ReadImageParams.class ) ) ).thenAnswer( invocation -> {
                assertFalse( ((ReadImageParams) invocation.getArgument( 0 )).isCacheOnly() );
                return ByteSource.wrap( new byte[]{1} );
            } );
            assertEquals( HttpStatus.OK, handler.handle( request ).getStatus() );
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"gif", "webp", "avif", "svg+xml"})
    void modernOriginalUrlsRemainCacheableWithoutGenerating( final String format )
        throws Exception
    {
        final Attachment attachment = Attachment.create().name( "source" ).label( "source" ).mimeType( "image/" + format )
            .sha512( "ec25d6e4126c7064f82aaab8b34693fc" ).build();
        final Media media = (Media) createContent( "123456", "path/to/image", attachment );
        when( contentService.getById( media.getId() ) ).thenReturn( media );
        when( contentService.getBinary( isA( ContentId.class ), isA( BinaryReference.class ) ) ).thenReturn( ByteSource.empty() );
        final var generator = new PortalUrlGeneratorServiceImpl(
            mock( WebappService.class ), mock( SiteService.class ), imageService,
            HmacTestHelper.createHmacService() );
        final var parts = generator.imageUrlParts( ImageUrlGeneratorParams.create()
            .setMedia( () -> media ).setProjectName( () -> ProjectName.from( "myproject" ) )
            .setBranch( () -> ContentConstants.BRANCH_MASTER ).setScale( "width(640)" ).build() );
        request.setRawPath( "/site/myproject/master/_" + parts.path() );
        assertEquals( "public, max-age=31536000, immutable", handler.handle( request ).getHeaders().get( "Cache-Control" ) );
        verifyNoInteractions( imageService );
    }

    private void configureHashlessGeneration( final boolean enabled )
    {
        final PortalConfig config = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( config.image_allowHashlessGeneration() ).thenReturn( enabled );
        handler.activate( config );
    }

    @ParameterizedTest
    @ValueSource(strings = {"jpeg", "png", "gif", "webp", "avif"})
    void hashlessGenerationIsDisabledByDefaultAndOptInNeverBypassesStyleProtection( final String format )
        throws Exception
    {
        setupContent();
        when( imageService.getStyle( "app:card" ) ).thenReturn( ImageStyle.create().name( "card" ).build() );
        final boolean modernFormat = "webp".equals( format ) || "avif".equals( format );
        // Verify the annotation default first, then both directions of a configuration update.
        for ( int configuration = 0; configuration < 3; configuration++ )
        {
            final boolean enabled = configuration == 1;
            if ( configuration > 0 )
            {
                configureHashlessGeneration( enabled );
            }
            for ( boolean styled : new boolean[]{false, true} )
            {
                if ( modernFormat && !styled )
                {
                    continue;
                }
                final boolean cacheOnly = !enabled || styled;
                for ( HttpMethod method : new HttpMethod[]{HttpMethod.GET, HttpMethod.HEAD} )
                {
                    request.setMethod( method );
                    request.setRawPath( "/site/myproject/master/_/media:image/myproject/123456/width-640" + ( styled ? "~app:card" : "" ) +
                        "/image-name.jpg." + format );
                    when( imageService.readImage( isA( ReadImageParams.class ) ) ).thenAnswer( invocation -> {
                        assertEquals( cacheOnly, ((ReadImageParams) invocation.getArgument( 0 )).isCacheOnly() );
                        return ByteSource.wrap( new byte[]{1} );
                    } );
                    final WebResponse response = handler.handle( request );
                    assertEquals( HttpStatus.OK, response.getStatus() );
                    assertNull( response.getHeaders().get( "Cache-Control" ) );
                    if ( cacheOnly )
                    {
                        when( imageService.readImage( isA( ReadImageParams.class ) ) ).thenAnswer( invocation -> {
                            assertTrue( ((ReadImageParams) invocation.getArgument( 0 )).isCacheOnly() );
                            throw new IllegalArgumentException( "Image is not cached" );
                        } );
                        assertEquals( HttpStatus.BAD_REQUEST,
                            assertThrows( WebException.class, () -> handler.handle( request ) ).getStatus() );
                    }
                }
            }
        }
        verify( contentService, never() ).getBinary( isA( ContentId.class ), isA( BinaryReference.class ) );
    }

    @Test
    void authorizedStyleSnapshotIsAvailableDuringProcessing()
        throws Exception
    {
        setupContent();
        final ImageStyle authorized = ImageStyle.create().name( "card" ).build();
        final ImageStyle updated = ImageStyle.create().name( "card" ).quality( 70 ).build();
        when( imageService.getStyle( "app:card" ) ).thenReturn( authorized, updated );
        when( imageService.readImage( isA( ReadImageParams.class ) ) ).thenAnswer( invocation -> {
            final ReadImageParams params = invocation.getArgument( 0 );
            assertFalse( params.isCacheOnly() );
            assertSame( authorized, params.getStyle() );
            return ByteSource.wrap( new byte[]{1} );
        } );
        request.setRawPath( "/site/myproject/master/_/media:image/myproject/123456:" + styledFingerprint( "png" ) +
            "/width-640~app:card/image-name.jpg" );
        assertEquals( HttpStatus.OK, handler.handle( request ).getStatus() );
        verify( imageService ).getStyle( "app:card" );
    }

    private String styledFingerprint( final String format )
    {
        final Media media = (Media) contentService.getById( ContentId.from( "123456" ) );
        return MediaHashResolver.resolveImageFingerprint( MediaHashResolver.resolveImageHash( media ),
            ImageStyleSettings.from( ImageStyle.create().name( "card" ).build() ), new ScaleParams( "width", new Object[]{640} ),
            "image/" + format.toLowerCase( Locale.ROOT ), HmacTestHelper.createHmacService() );
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
        request.setRawPath( "/site/myproject/master/_/media:image/myproject/123456:" + styledFingerprint( "png" ) +
            "/width-640~app:card/image.png" );
        final WebResponse response = handler.handle( request );
        assertEquals( HttpStatus.OK, response.getStatus() );
        assertEquals( MediaType.PNG, response.getContentType() );
        final ArgumentCaptor<ReadImageParams> params = ArgumentCaptor.forClass( ReadImageParams.class );
        verify( imageService ).readImage( params.capture() );
        assertEquals( "card", params.getValue().getStyle().getName() );
        assertFalse( params.getValue().isCacheOnly() );
        verify( contentService, never() ).getBinary( isA( ContentId.class ), isA( BinaryReference.class ) );
    }

    @ParameterizedTest
    @ValueSource(strings = {"webp", "avif", "WEBP", "AVIF", "jpeg", "png", "gif"})
    void styledCacheMissWithInvalidFingerprintCannotRegenerate( final String format )
        throws Exception
    {
        setupContent();
        when( imageService.readImage( isA( ReadImageParams.class ) ) ).thenAnswer( invocation -> {
            assertTrue( ((ReadImageParams) invocation.getArgument( 0 )).isCacheOnly() );
            throw new IllegalArgumentException( "Image is not cached" );
        } );
        when( imageService.getStyle( "app:card" ) ).thenReturn( ImageStyle.create().name( "card" ).build() );
        final String valid = styledFingerprint( format );
        final String source = MediaHashResolver.resolveImageHash( (Media) contentService.getById( ContentId.from( "123456" ) ) );
        for ( HttpMethod method : new HttpMethod[]{HttpMethod.GET, HttpMethod.HEAD} )
        {
            request.setMethod( method );
            for ( String path : new String[]{"123456/width-640~app:card", "123456:00000000000000000000000000000000/width-640~app:card",
                "123456:" + source + "/width-640~app:card", "123456:f4774dff7b6ef5d0fc1f077cbec55899/width-640~app:card",
                "123456:09e13cd582eacd64dca2cf0c8543ecb359f9b80f/width-640~app:card", "123456:" + valid + "/width-320~app:card"} )
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
        request.setRawPath( "/site/myproject/master/_/media:image/myproject/123456:" + styledFingerprint( format ) + "/width-640~app:card/image-name.jpg." + format );
        when( imageService.getStyle( "app:card" ) ).thenReturn(
            ImageStyle.create().name( "card" ).build() );
        final WebResponse response = handler.handle( request );
        assertEquals( MediaType.parse( "image/" + format.toLowerCase( Locale.ROOT ) ), response.getContentType() );
        final ArgumentCaptor<ReadImageParams> params = ArgumentCaptor.forClass( ReadImageParams.class );
        verify( imageService ).readImage( params.capture() );
        assertEquals( "card", params.getValue().getStyle().getName() );
        assertFalse( params.getValue().isCacheOnly() );
        assertEquals( "image/" + format.toLowerCase( Locale.ROOT ), params.getValue().getMimeType() );
    }

    @Test
    void styleFingerprintControlsCaching()
        throws Exception
    {
        setupContent();
        final ImageStyle style = ImageStyle.create().name( "card" ).build();
        when( imageService.getStyle( "app:card" ) ).thenReturn( style );
        request.setRawPath( "/site/myproject/master/_/media:image/myproject/123456:" + styledFingerprint( "png" ) + "/width-640~app:card/image-name.jpg" );
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
        assertEquals( "card-wide", params.getValue().getStyle().getName() );
        assertEquals( "width", params.getValue().getScaleParams().getName() );
        assertArrayEquals( new Object[]{640}, params.getValue().getScaleParams().getArguments() );
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "HEAD"})
    void unknownStyleIsNotFound( final String method )
    {
        request.setMethod( HttpMethod.valueOf( method ) );
        request.setRawPath( "/site/myproject/master/_/media:image/myproject/123456/full~app:missing/image-name.jpg" );
        when( imageService.getStyle( "app:missing" ) ).thenThrow( new ImageStyleNotFoundException( "app:missing" ) );
        assertEquals( HttpStatus.NOT_FOUND, assertThrows( WebException.class, () -> handler.handle( request ) ).getStatus() );
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
