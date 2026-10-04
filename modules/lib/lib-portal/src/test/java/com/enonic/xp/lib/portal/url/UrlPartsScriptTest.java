package com.enonic.xp.lib.portal.url;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.portal.url.AttachmentUrlParts;
import com.enonic.xp.portal.url.AttachmentUrlPartsParams;
import com.enonic.xp.portal.url.ImageUrlParts;
import com.enonic.xp.portal.url.ImageUrlPartsParams;
import com.enonic.xp.portal.url.PageUrlParts;
import com.enonic.xp.portal.url.PageUrlPartsParams;
import com.enonic.xp.portal.url.PortalScope;
import com.enonic.xp.portal.url.PortalScopeParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ProcessHtmlPartsParams;
import com.enonic.xp.portal.url.ProcessedHtml;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UrlPartsScriptTest
    extends ScriptTestSupport
{
    private static final String MACRO_PLACEHOLDER = "<editor-macro data-macro-name=\"youtube\" data-macro-ref=\"macroref\"></editor-macro>";

    private PortalUrlService portalUrlService;

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();

        this.portalUrlService = Mockito.mock( PortalUrlService.class );

        when( portalUrlService.portalScope( any( PortalScopeParams.class ) ) ).thenAnswer( invocation -> {
            final PortalScopeParams params = invocation.getArgument( 0 );
            final SiteConfigs siteConfigs = ContentPath.from( "/configured" ).equals( params.getContentPath() ) ? portalConfig(
                "https://www.example.com" ) : SiteConfigs.empty();
            return new PortalScope( params.getProjectName(), params.getBranch(), params.getContentPath(), siteConfigs );
        } );

        when( portalUrlService.pageUrlParts( any( PageUrlPartsParams.class ) ) ).thenAnswer( invocation -> {
            final PageUrlPartsParams params = invocation.getArgument( 0 );
            final String query = params.getQueryParams()
                .entrySet()
                .stream()
                .flatMap( entry -> entry.getValue().stream().map( value -> entry.getKey() + "=" + value ) )
                .collect( Collectors.joining( "&" ) );
            return new PageUrlParts( null, "/posts/first-post", query.isEmpty() ? "" : "?" + query );
        } );

        when( portalUrlService.processHtmlParts( any( ProcessHtmlPartsParams.class ) ) ).thenReturn(
            new ProcessedHtml( "<a href=\"/posts/first-post\" data-link-ref=\"ref\">Post</a>" + MACRO_PLACEHOLDER, null, List.of(
                new ProcessedHtml.ContentLink( "ref", "content://123456", "123456", new PageUrlParts( null, "/posts/first-post", "" ),
                                               "" ) ), List.of(),
                               List.of( new ProcessedHtml.Macro( "macroref", MacroKey.from( "com.example.myapp:youtube" ),
                                                                   Map.of( "videoId", List.of( "abc" ) ), "" ) ) ) );

        when( portalUrlService.imageUrlParts( any( ImageUrlPartsParams.class ) ) ).thenAnswer( invocation -> {
            final ImageUrlPartsParams params = invocation.getArgument( 0 );
            final String id = params.getId();
            final String context = context( params.getProjectName().get(), params.getBranch().get() );
            return new ImageUrlParts( "/media:image/" + context + "/" + id + ":hash/block-1024-768/photo.jpg", "?quality=85", context, id,
                                      "hash", "block-1024-768", "photo.jpg" );
        } );

        when( portalUrlService.attachmentUrlParts( any( AttachmentUrlPartsParams.class ) ) ).thenAnswer( invocation -> {
            final AttachmentUrlPartsParams params = invocation.getArgument( 0 );
            final String id = "/my-site/documents/report".equals( params.getPath() ) ? "reportid" : params.getId();
            final String context = context( params.getProjectName().get(), params.getBranch().get() );
            return new AttachmentUrlParts( "/media:attachment/" + context + "/" + id + ":hash/report.pdf",
                                           params.isDownload() ? "?download" : "", context, id, "hash", "report.pdf" );
        } );

        addService( PortalUrlService.class, this.portalUrlService );
    }

    private static SiteConfigs portalConfig( final String baseUrl )
    {
        final PropertyTree config = new PropertyTree();
        config.setString( "baseUrl", baseUrl );
        return SiteConfigs.from( SiteConfig.create().application( ApplicationKey.from( "portal" ) ).config( config ).build() );
    }

    private static String context( final ProjectName projectName, final Branch branch )
    {
        return projectName + ( "master".equals( branch.toString() ) ? "" : ":" + branch );
    }

    @Test
    void testExample_pageUrlParts()
    {
        runScript( "/lib/xp/examples/portal/pageUrlParts.js" );

        final ArgumentCaptor<PageUrlPartsParams> captor = ArgumentCaptor.forClass( PageUrlPartsParams.class );
        verify( portalUrlService ).pageUrlParts( captor.capture() );

        final PageUrlPartsParams params = captor.getValue();
        assertEquals( "/my-site/posts/first-post", params.getPath() );
        assertScope( params.getScope() );
        assertTrue( params.getQueryParams().get( "a" ).contains( "1" ) );
    }

    @Test
    void testExample_portalScope()
    {
        runScript( "/lib/xp/examples/portal/portalScope.js" );

        final ArgumentCaptor<PortalScopeParams> scope = ArgumentCaptor.forClass( PortalScopeParams.class );
        verify( portalUrlService ).portalScope( scope.capture() );
        assertEquals( ContentPath.from( "/my-site" ), scope.getValue().getContentPath() );
        assertEquals( ProjectName.from( "myproject" ), scope.getValue().getProjectName() );
        assertEquals( Branch.from( "master" ), scope.getValue().getBranch() );

        final ArgumentCaptor<PageUrlPartsParams> captor = ArgumentCaptor.forClass( PageUrlPartsParams.class );
        verify( portalUrlService ).pageUrlParts( captor.capture() );
        assertScope( captor.getValue().getScope() );
    }

    @Test
    void testPortalScopeBaseUrl()
    {
        runFunction( "/test/portal-scope-test.js", "baseUrl" );

        // the script reads the Base URL as a string, and hands the scope on as it was resolved
        final ArgumentCaptor<PageUrlPartsParams> captor = ArgumentCaptor.forClass( PageUrlPartsParams.class );
        verify( portalUrlService ).pageUrlParts( captor.capture() );
        assertEquals( "https://www.example.com", captor.getValue().getScope().baseUrl() );
        assertEquals( ContentPath.from( "/configured" ), captor.getValue().getScope().path() );
    }

    @Test
    void testPortalScopeWithoutBaseUrl()
    {
        runFunction( "/test/portal-scope-test.js", "noBaseUrl" );
    }

    @Test
    void testScopeNotResolvedByPortalScope()
    {
        runFunction( "/test/portal-scope-test.js", "scopeNotResolved" );
        verify( portalUrlService, never() ).pageUrlParts( any() );
    }

    /**
     * The scope resolved by the script comes back as is.
     */
    private static void assertScope( final PortalScope scope )
    {
        assertEquals( ProjectName.from( "myproject" ), scope.projectName() );
        assertEquals( Branch.from( "master" ), scope.branch() );
        assertEquals( ContentPath.from( "/my-site" ), scope.path() );
    }

    @Test
    void testExample_processHtmlParts()
    {
        runScript( "/lib/xp/examples/portal/processHtmlParts.js" );

        final ArgumentCaptor<ProcessHtmlPartsParams> captor = ArgumentCaptor.forClass( ProcessHtmlPartsParams.class );
        verify( portalUrlService ).processHtmlParts( captor.capture() );

        final ProcessHtmlPartsParams params = captor.getValue();
        assertEquals( "<a href=\"content://123456\">Post</a>[youtube videoid=\"abc\"/]", params.getValue() );
        assertScope( params.getScope() );
    }

    @Test
    void testExample_imageUrlParts()
    {
        runScript( "/lib/xp/examples/portal/imageUrlParts.js" );

        final ArgumentCaptor<ImageUrlPartsParams> captor = ArgumentCaptor.forClass( ImageUrlPartsParams.class );
        verify( portalUrlService ).imageUrlParts( captor.capture() );

        final ImageUrlPartsParams params = captor.getValue();
        assertEquals( "block(1024,768)", params.getScale() );
        assertEquals( 85, params.getQuality() );
        assertEquals( "11cc4e09-0d9d-4a4d-9a4b-1a3a8c2b6f3e", params.getId() );
        assertNull( params.getMedia() );
    }

    @Test
    void testExample_attachmentUrlParts()
    {
        runScript( "/lib/xp/examples/portal/attachmentUrlParts.js" );

        final ArgumentCaptor<AttachmentUrlPartsParams> captor = ArgumentCaptor.forClass( AttachmentUrlPartsParams.class );
        verify( portalUrlService ).attachmentUrlParts( captor.capture() );

        assertTrue( captor.getValue().isDownload() );
        assertEquals( "/my-site/documents/report", captor.getValue().getPath() );
        assertNull( captor.getValue().getContentSupplier() );
    }
}
