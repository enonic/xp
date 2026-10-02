package com.enonic.xp.portal.impl.url;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.portal.url.ProcessHtmlPartsParams;
import com.enonic.xp.portal.url.ProcessedHtml;
import com.enonic.xp.portal.url.UrlBaseParams;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.context.ContextAccessorSupport;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.impl.macro.MacroServiceImpl;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.html.HtmlDocument;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.portal.impl.RedirectChecksumService;
import com.enonic.xp.portal.url.PortalUrlGeneratorService;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ProcessHtmlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.StyleDescriptor;
import com.enonic.xp.style.StyleDescriptorService;
import com.enonic.xp.style.StyleDescriptors;
import com.enonic.xp.webapp.WebappService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PortalUrlServiceImpl_processHtmlPartsTest
{
    private ContentService contentService;

    private PortalUrlService service;

    protected StyleDescriptorService styleDescriptorService;

    private PortalRequest portalRequest;

    private HttpServletRequest req;

    PortalUrlGeneratorService portalUrlGeneratorService;

    @BeforeEach
    void setUp()
    {
        this.contentService = mock( ContentService.class );
        this.styleDescriptorService = mock( StyleDescriptorService.class );

        this.styleDescriptorService = mock( StyleDescriptorService.class );
        when( this.styleDescriptorService.getByApplications( any() ) ).thenReturn( StyleDescriptors.empty() );

        portalUrlGeneratorService = new PortalUrlGeneratorServiceImpl( mock( WebappService.class ), mock( SiteService.class ), this.contentService );

        this.service =
            new PortalUrlServiceImpl( this.contentService, mock( ResourceService.class ), new MacroServiceImpl(), styleDescriptorService,
                                      mock( RedirectChecksumService.class ), mock( ProjectService.class ), portalUrlGeneratorService );

        req = mock( HttpServletRequest.class );

        this.portalRequest = new PortalRequest();
        this.portalRequest.setMode( RenderMode.LIVE );
        this.portalRequest.setBranch( Branch.from( "draft" ) );
        this.portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) );
        this.portalRequest.setBaseUri( "/site" );
        this.portalRequest.setRawPath( "/site/myproject/draft/context/path" );
        this.portalRequest.setRawRequest( req );

        PortalRequestAccessor.set( portalRequest );

        ContextAccessorSupport.getInstance().set( ContextBuilder.create().build() );
    }

    @AfterEach
    void destroy()
    {
        PortalRequestAccessor.remove();
        ContextAccessorSupport.getInstance().remove();
    }

    /**
     * Processes in the context project and branch, with the project as the base.
     */
    private ProcessedHtml process( final ProcessHtmlPartsParams.Builder params )
    {
        return inContext( () -> service.processHtmlParts( params.build() ) );
    }

    /**
     * Processes in the context project and branch, for the base the path names.
     */
    private ProcessedHtml process( final ProcessHtmlPartsParams.Builder params, final String basePath )
    {
        return inContext( () -> service.processHtmlParts(
            params.base( service.urlBase( UrlBaseParams.create().setContentPath( ContentPath.from( basePath ) ).build() ) ).build() ) );
    }

    private static <T> T inContext( final Callable<T> callable )
    {
        return ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( callable );
    }

    private Content contentInNestedSites( final String parentBaseUrl )
    {
        final Content content = Content.create( ContentFixtures.newContent() ).build();
        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        // the content sits in the nested site /a/b, itself inside the site /a
        final Site nestedSite = mockSiteWithBaseUrl( ContentPath.from( "/a/b" ), "https://nested.example.com" );
        mockSiteWithBaseUrl( ContentPath.from( "/a" ), parentBaseUrl );
        when( this.contentService.getNearestSite( content.getId() ) ).thenReturn( nestedSite );

        return content;
    }

    @Test
    void testEmptyValue()
    {
        mockSiteWithBaseUrl( ContentPath.from( "/a" ), "https://parent.example.com" );

        final ProcessedHtml result = process( ProcessHtmlPartsParams.create(), "/a" );

        assertEquals( "", result.html() );
        assertEquals( "https://parent.example.com", result.baseUrl() );
        assertThat( result.links() ).isEmpty();
        assertThat( result.images() ).isEmpty();
    }

    @Test
    void testContentLinkWithConfiguredBaseUrl()
    {
        final Content content = contentInNestedSites( "https://parent.example.com/" );

        final ProcessedHtml result = process( ProcessHtmlPartsParams.create()
                                                  .value( String.format( "<a href=\"content://%s\">Content</a>", content.getId() ) ), "/a" );

        // the link is the content path relative to /a; the Base URL of /a comes with the result
        assertEquals( "https://parent.example.com", result.baseUrl() );

        final ProcessedHtml.ContentLink link = (ProcessedHtml.ContentLink) result.links().get( 0 );
        assertEquals( content.getId().toString(), link.contentId() );
        assertEquals( "content://" + content.getId(), link.uri() );
        assertEquals( "https://parent.example.com", link.page().baseUrl() );
        assertEquals( "/b/mycontent", link.page().path() );
        assertEquals( "", link.page().queryString() );

        assertEquals( "<a href=\"/b/mycontent\" data-link-ref=\"" + link.ref() + "\">Content</a>", result.html() );
    }

    @Test
    void testBaseIsResolvedOnceForEveryLink()
    {
        final Content content = contentInNestedSites( "https://parent.example.com" );
        final String link = String.format( "<a href=\"content://%s\">Content</a>", content.getId() );

        final ProcessedHtml result = process( ProcessHtmlPartsParams.create().value( link + link + link ), "/a" );

        assertEquals( 3, result.links().size() );
        verify( this.contentService, times( 1 ) ).getByPath( ContentPath.from( "/a" ) );
    }

    @Test
    void testLinksThatDoNotResolveHaveEntriesWithoutParts()
    {
        final Content content = contentInNestedSites( null );
        final String html = String.format( "<a href=\"content://missing?fragment=top\">Gone</a>" + "<a href=\"media://download/missing\">Gone</a>" +
                                               "<img src=\"image://missing\">" + "<a href=\"content://%s\">Content</a>", content.getId() );

        final ProcessedHtml result = process( ProcessHtmlPartsParams.create().value( html ), "/a" );

        assertEquals( 3, result.links().size() );
        final ProcessedHtml.ContentLink gone = (ProcessedHtml.ContentLink) result.links().get( 0 );
        assertNull( gone.page() );
        assertEquals( "top", gone.fragment() );
        final ProcessedHtml.AttachmentLink goneMedia = (ProcessedHtml.AttachmentLink) result.links().get( 1 );
        assertNull( goneMedia.attachment() );
        assertThat( goneMedia.download() ).isTrue();
        assertNull( result.images().get( 0 ).src() );
        assertThat( result.images().get( 0 ).srcset() ).isEmpty();

        // the links are left as written, with a ref to their entries; the rest of the text is processed
        assertThat( result.html() ).matches(
            "<a href=\"/_/error/404\\?message=Not\\+Found\\.\\+\\w+\" data-link-ref=\"" + gone.ref() + "\">Gone</a>" +
                "<a href=\"/media:attachment/_error/missing/missing\\?download\" data-link-ref=\"" + goneMedia.ref() +
                "\">Gone</a><img src=\"/media:image/_error/missing/width-768/missing\" data-image-ref=\"" + result.images().get( 0 ).ref() +
                "\">.*" );
        assertThat( result.html() ).contains( "<a href=\"/b/mycontent\" data-link-ref=\"" + result.links().get( 2 ).ref() + "\">" );
    }

    @Test
    void testContentLinkOutsideTheBaseHasNoParts()
    {
        final Content content = contentInNestedSites( null );
        mockSiteWithBaseUrl( ContentPath.from( "/c" ), null );

        final String html = String.format( "<a href=\"content://%s\">Content</a>", content.getId() );
        final ProcessedHtml result = process( ProcessHtmlPartsParams.create().value( html ), "/c" );

        final ProcessedHtml.ContentLink link = (ProcessedHtml.ContentLink) result.links().get( 0 );
        assertNull( link.page() );
        assertThat( result.html() ).matches(
            "<a href=\"/_/error/404\\?message=Not\\+Found\\.\\+\\w+\" data-link-ref=\"" + link.ref() + "\">Content</a>" );
    }

    @Test
    void testContentLinkWithoutBaseUrl()
    {
        // setUp binds a site request for myproject/draft: the parts are resolved from configuration regardless
        final Content content = contentInNestedSites( null );

        final ProcessedHtml result = process( ProcessHtmlPartsParams.create()
                                                  .value( String.format( "<a href=\"content://%s\">Content</a>", content.getId() ) ), "/a" );

        assertNull( result.baseUrl() );
        assertNull( ( (ProcessedHtml.ContentLink) result.links().get( 0 ) ).page().baseUrl() );
        assertThat( result.html() ).startsWith( "<a href=\"/b/mycontent\"" );
    }

    @Test
    void testContentLinkWithQueryAndFragment()
    {
        final Content content = contentInNestedSites( null );

        final ProcessedHtml result = process( ProcessHtmlPartsParams.create()
                                                  .value( String.format(
                                                      "<a href=\"content://%s?query=a%%3D1&amp;fragment=top\">Content</a>",
                                                      content.getId() ) ), "/a" );

        final ProcessedHtml.ContentLink link = (ProcessedHtml.ContentLink) result.links().get( 0 );
        assertEquals( "?a=1", link.page().queryString() );
        assertEquals( "top", link.fragment() );
        assertThat( result.html() ).startsWith( "<a href=\"/b/mycontent?a=1#top\"" );
    }

    @Test
    void testWithoutMacroProcessing()
    {
        final ProcessedHtml result = process( ProcessHtmlPartsParams.create()
                                                  .value( "<p>[correct_macro/]</p>" )
                                                  .processMacros( false ) );

        assertEquals( "<p>[correct_macro/]</p>", result.html() );
    }

    @Test
    void testMacrosAreProcessedInTheCustomProcessorResult()
    {
        final ProcessedHtml result = process( ProcessHtmlPartsParams.create()
                                                  .value( "<p>Text</p>" )
                                                  .customHtmlProcessor( processor -> {
                                                      processor.processDefault();
                                                      return processor.getDocument().getInnerHtml() + "[correct_macro/]";
                                                  } ) );

        assertEquals( "<p>Text</p><!--#MACRO _name=\"correct_macro\" _document=\"__macroDocument1\" _body=\"\"-->", result.html() );
    }

    @Test
    void testEmptyCaptionsAreRemoved()
    {
        final ProcessedHtml result = process( ProcessHtmlPartsParams.create()
                                                  .value( "<figure><img src=\"src\"/><figcaption></figcaption></figure>" +
                                                              "<figure><img src=\"src\"/><figcaption>Caption text</figcaption></figure>" ) );

        assertEquals( "<figure><img src=\"src\"></figure><figure><img src=\"src\"><figcaption>Caption text</figcaption></figure>",
                      result.html() );
    }

    @Test
    void testContentLinkToTheSelectedLevel()
    {
        final Site site = Site.create( ContentFixtures.newSite() ).parentPath( ContentPath.ROOT ).name( "a" ).build();
        when( this.contentService.getById( site.getId() ) ).thenReturn( site );
        when( this.contentService.getByPath( ContentPath.from( "/a" ) ) ).thenReturn( site );

        final ProcessedHtml result = process( ProcessHtmlPartsParams.create()
                                                  .value( String.format( "<a href=\"content://%s\">Site</a>", site.getId() ) ), "/a" );

        // the relative path of the level itself is empty: its root is linked instead of the document
        assertEquals( "", ( (ProcessedHtml.ContentLink) result.links().get( 0 ) ).page().path() );
        assertThat( result.html() ).startsWith( "<a href=\"/\"" );
    }

    @Test
    void testMediaLinks()
    {
        final Media media = ContentFixtures.newMedia();
        when( this.contentService.getById( media.getId() ) ).thenReturn( media );

        final ProcessedHtml result = process( ProcessHtmlPartsParams.create()
                                                  .value( "<img src=\"image://" + media.getId() + "\"/><a href=\"media://download/" +
                                                              media.getId() + "\">Download</a>" )
                                                  .imageWidths( List.of( 660 ) )
                                                  .imageSizes( " " ) );

        // the bare media API paths, with the context's project and branch
        final String image = "/media:image/context-project:context-branch/" + media.getId() + ":0a350f43700951cdcca1574f448a7e22";

        final ProcessedHtml.Image img = result.images().get( 0 );
        assertEquals( media.getId().toString(), img.contentId() );
        assertEquals( image + "/width-768/mycontent", img.src().path() );
        assertEquals( 660, img.srcset().get( 0 ).width() );
        assertEquals( image + "/width-660/mycontent", img.srcset().get( 0 ).url().path() );

        final ProcessedHtml.AttachmentLink attachment = (ProcessedHtml.AttachmentLink) result.links().get( 0 );
        assertThat( attachment.download() ).isTrue();
        assertEquals( "?download", attachment.attachment().queryString() );

        assertThat( result.html() ).startsWith(
            "<img src=\"" + image + "/width-768/mycontent\" data-image-ref=\"" + img.ref() + "\" srcset=\"" + image +
                "/width-660/mycontent 660w\">" );
        assertThat( result.html() ).contains(
            "<a href=\"" + attachment.attachment().path() + "?download\" data-link-ref=\"" + attachment.ref() + "\">Download</a>" );
        assertThat( result.html() ).startsWith( "<img src=\"/media:image/" ).doesNotContain( "/_/" ).doesNotContain( "/site/" );
    }

    @Test
    void testImageSizesWithoutImageWidths()
    {
        final Media media = ContentFixtures.newMedia();
        when( this.contentService.getById( media.getId() ) ).thenReturn( media );

        // sizes goes with a srcset: without widths, or with none, neither is written
        for ( final List<Integer> imageWidths : Arrays.asList( null, List.<Integer>of() ) )
        {
            final ProcessedHtml result = process( ProcessHtmlPartsParams.create()
                                                      .value( "<img src=\"image://" + media.getId() + "\"/>" )
                                                      .imageWidths( imageWidths )
                                                      .imageSizes( "(max-width: 960px) 660px" ) );

            assertThat( result.images().get( 0 ).srcset() ).isEmpty();
            assertThat( result.html() ).doesNotContain( "srcset" ).doesNotContain( "sizes" );
        }
    }

    @Test
    void testImageServedAsStoredHasNoSrcset()
    {
        final Media media = unscaledMedia();
        when( this.contentService.getById( media.getId() ) ).thenReturn( media );

        final ProcessedHtml result = process( ProcessHtmlPartsParams.create()
                                                  .value( "<img src=\"image://" + media.getId() + "\"/>" )
                                                  .imageWidths( List.of( 660 ) )
                                                  .imageSizes( "(max-width: 960px) 660px" ) );

        assertThat( result.images().get( 0 ).srcset() ).isEmpty();
        assertThat( result.html() ).contains( "/full/mycontent\"" ).doesNotContain( "srcset" ).doesNotContain( "sizes" );
        assertEquals( "full", result.images().get( 0 ).src().scale() );
    }

    @Test
    void testMediaLinksIgnoreDefaultMediaBaseUrl()
    {
        final PortalConfig config = mock( PortalConfig.class );
        when( config.media_defaultBaseUrl() ).thenReturn( "https://cdn.example.com/api/" );
        when( config.legacy_mediaApiAutoMount_enabled() ).thenReturn( true );
        ( (PortalUrlGeneratorServiceImpl) this.portalUrlGeneratorService ).activate( config );

        final Media media = ContentFixtures.newMedia();
        when( this.contentService.getById( media.getId() ) ).thenReturn( media );

        final ProcessedHtml result = process( ProcessHtmlPartsParams.create()
                                                  .value( "<img src=\"image://" + media.getId() + "\"/>" ) );

        // media.defaultBaseUrl stands for vhost configuration: the parts are built without it
        assertThat( result.html() ).startsWith( "<img src=\"/media:image/context-project:context-branch/" ).doesNotContain( "cdn.example.com" );
    }

    private Site mockSiteWithBaseUrl( final ContentPath path, final String baseUrl )
    {
        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( path );

        final SiteConfigs.Builder siteConfigsBuilder = SiteConfigs.create();
        if ( baseUrl != null )
        {
            final PropertyTree config = new PropertyTree();
            config.addString( "baseUrl", baseUrl );
            siteConfigsBuilder.add( SiteConfig.create().application( ApplicationKey.from( "portal" ) ).config( config ).build() );
        }

        final PropertyTree data = new PropertyTree();
        when( site.getData() ).thenReturn( data );
        SiteConfigsDataSerializer.toData( siteConfigsBuilder.build(), data.getRoot() );

        when( this.contentService.getByPath( path ) ).thenReturn( site );

        return site;
    }

    private static Media unscaledMedia()
    {
        final Media media = ContentFixtures.newMedia();
        final Attachment animation =
            Attachment.create().name( "logo.gif" ).mimeType( "image/gif" ).label( "source" ).sha512( "ec25d6e4126c7064f82aaab8b34693fc" ).build();
        return Media.create( media ).attachments( Attachments.from( animation ) ).build();
    }
}
