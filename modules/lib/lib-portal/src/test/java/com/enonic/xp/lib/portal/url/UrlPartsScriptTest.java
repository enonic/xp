package com.enonic.xp.lib.portal.url;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.portal.url.AttachmentUrlParts;
import com.enonic.xp.portal.url.AttachmentUrlPartsParams;
import com.enonic.xp.portal.url.ImageUrlParts;
import com.enonic.xp.portal.url.ImageUrlPartsParams;
import com.enonic.xp.portal.url.PageUrlPartsParams;
import com.enonic.xp.portal.url.PageUrlParts;
import com.enonic.xp.portal.url.PortalUrlGeneratorService;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ProcessHtmlPartsParams;
import com.enonic.xp.portal.url.ProcessedHtml;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UrlPartsScriptTest
    extends ScriptTestSupport
{
    private PortalUrlService portalUrlService;

    private PortalUrlGeneratorService portalUrlGeneratorService;

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();

        this.portalUrlService = Mockito.mock( PortalUrlService.class );
        this.portalUrlGeneratorService = Mockito.mock( PortalUrlGeneratorService.class );

        when( portalUrlService.pageUrlParts( any( PageUrlPartsParams.class ) ) ).thenReturn(
            new PageUrlParts( null, "/posts/first-post", "?a=1" ) );

        when( portalUrlService.processHtmlParts( any( ProcessHtmlPartsParams.class ) ) ).thenReturn(
            new ProcessedHtml( "<a href=\"/posts/first-post\" data-link-ref=\"ref\">Post</a>", null, List.of(
                new ProcessedHtml.ContentLink( "ref", "content://123456", "123456", new PageUrlParts( null, "/posts/first-post", "" ),
                                               null ) ), List.of() ) );

        when( portalUrlGeneratorService.imageUrlParts( any( ImageUrlPartsParams.class ) ) ).thenAnswer( invocation -> {
            final ImageUrlPartsParams params = invocation.getArgument( 0 );
            final String id = params.getId();
            final String context = context( params.getProjectName().get(), params.getBranch().get() );
            return new ImageUrlParts( "/media:image/" + context + "/" + id + ":hash/block-1024-768/photo.jpg", "?quality=85", context, id,
                                      "hash", "block-1024-768", "photo.jpg" );
        } );

        when( portalUrlGeneratorService.attachmentUrlParts( any( AttachmentUrlPartsParams.class ) ) ).thenAnswer( invocation -> {
            final AttachmentUrlPartsParams params = invocation.getArgument( 0 );
            final String id = "/my-site/documents/report".equals( params.getPath() ) ? "reportid" : params.getId();
            final String context = context( params.getProjectName().get(), params.getBranch().get() );
            return new AttachmentUrlParts( "/media:attachment/" + context + "/" + id + ":hash/report.pdf",
                                           params.isDownload() ? "?download" : "", context, id, "hash", "report.pdf" );
        } );

        addService( PortalUrlService.class, this.portalUrlService );
        addService( PortalUrlGeneratorService.class, this.portalUrlGeneratorService );
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
        assertEquals( "/my-site", params.getBase().getPath() );
        assertEquals( "myproject", params.getBase().getProjectName() );
        assertEquals( "master", params.getBase().getBranch() );
        assertTrue( params.getQueryParams().get( "a" ).contains( "1" ) );
    }

    @Test
    void testExample_processHtmlParts()
    {
        runScript( "/lib/xp/examples/portal/processHtmlParts.js" );

        final ArgumentCaptor<ProcessHtmlPartsParams> captor = ArgumentCaptor.forClass( ProcessHtmlPartsParams.class );
        verify( portalUrlService ).processHtmlParts( captor.capture() );

        final ProcessHtmlPartsParams params = captor.getValue();
        assertEquals( "<a href=\"content://123456\">Post</a>", params.getValue() );
        assertEquals( "/my-site", params.getBase().getPath() );
        assertEquals( "myproject", params.getBase().getProjectName() );
    }

    @Test
    void testExample_imageUrlParts()
    {
        runScript( "/lib/xp/examples/portal/imageUrlParts.js" );

        final ArgumentCaptor<ImageUrlPartsParams> captor = ArgumentCaptor.forClass( ImageUrlPartsParams.class );
        verify( portalUrlGeneratorService ).imageUrlParts( captor.capture() );

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
        verify( portalUrlGeneratorService ).attachmentUrlParts( captor.capture() );

        assertTrue( captor.getValue().isDownload() );
        assertEquals( "/my-site/documents/report", captor.getValue().getPath() );
        assertNull( captor.getValue().getContentSupplier() );
    }
}
