package com.enonic.xp.portal.impl.url;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.url.ProcessHtmlParams;

import static org.junit.Assert.*;

public class PortalUrlServiceImpl_processHtmlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void process_empty_value()
    {
        //Checks the process for a null value
        final ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest );
        String processedHtml = this.service.processHtml( params );
        assertEquals( "", processedHtml );

        //Checks the process for an empty string value
        params.value( "" );
        processedHtml = this.service.processHtml( params );
        assertEquals( "", processedHtml );
    }

    @Test
    public void process_single_content()
    {
        //Creates a content
        final Content content = ContentFixtures.newContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        //Process an html text containing a link to this content
        final ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"content://" + content.getId() + "\">Content</a>" );

        //Checks that the page URL of the content is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals( "<a href=\"/portal/draft" + content.getPath() + "\">Content</a>", processedHtml );
    }

    @Test
    public void process_single_image()
    {
        //Creates a content
        final Content content = ContentFixtures.newContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        //Process an html text containing a link to this content
        final ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"image://" + content.getId() + "\">Image</a>" );

        //Checks that the page URL of the content is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals( "<a href=\"/portal/draft/context/path/_/image/" + content.getId() + "/" + "width-768" + "/" + content.getName() +
                          ".jpeg\">Image</a>", processedHtml );
    }

    @Test
    public void process_single_media()
    {
        //Creates a content with attachments
        final Attachment thumb = Attachment.
            create().
            label( "thumb" ).
            name( "a1.jpg" ).
            mimeType( "image/jpg" ).
            build();
        final Attachment source = Attachment.
            create().
            label( "source" ).
            name( "a2.jpg" ).
            mimeType( "image/jpg" ).
            build();
        final Attachments attachments = Attachments.from( thumb, source );
        final Content content = Content.
            create( ContentFixtures.newContent() ).
            attachments( attachments ).
            build();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        //Process an html text containing an inline link to this content
        ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"media://inline/" + content.getId() + "\">Media</a>" );

        //Checks that the URL of the source attachment of the content is returned
        String processedHtml = this.service.processHtml( params );
        assertEquals(
            "<a href=\"/portal/draft/context/path/_/attachment/inline/" + content.getId() + "/" + source.getName() + "\">Media</a>",
            processedHtml );

        //Process an html text containing a download link to this content
        params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"media://download/" + content.getId() + "\">Media</a>" );

        //Checks that the URL of the source attachment of the content is returned
        processedHtml = this.service.processHtml( params );
        assertEquals(
            "<a href=\"/portal/draft/context/path/_/attachment/download/" + content.getId() + "/" + source.getName() + "\">Media</a>",
            processedHtml );

        //Process an html text containing an inline link to this content in a img tag
        params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<img src=\"media://inline/" + content.getId() + "\">Media</a>" );

        //Checks that the URL of the source attachment of the content is returned
        processedHtml = this.service.processHtml( params );
        assertEquals(
            "<img src=\"/portal/draft/context/path/_/attachment/inline/" + content.getId() + "/" + source.getName() + "\">Media</a>",
            processedHtml );

    }

    @Test
    public void process_multiple_links()
    {
        //Creates a content with attachments
        final Attachment thumb = Attachment.
            create().
            label( "thumb" ).
            name( "a1.jpg" ).
            mimeType( "image/jpg" ).
            build();
        final Attachment source = Attachment.
            create().
            label( "source" ).
            name( "a2.jpg" ).
            mimeType( "image/jpg" ).
            build();
        final Attachments attachments = Attachments.from( thumb, source );
        final Content content = Content.
            create( ContentFixtures.newContent() ).
            attachments( attachments ).
            build();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        //Process an html text containing multiple links, on multiple lines, to this content as a media and as a content
        final ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<p>A content link:&nbsp;<a href=\"content://" + content.getId() + "\">FirstLink</a></p>\n" +
                       "<p>A second content link:&nbsp;<a href=\"content://" + content.getId() + "\">SecondLink</a>" +
                       "&nbsp;and a download link:&nbsp;<a href=\"media://download/" + content.getId() + "\">Download</a></p>\n" +
                       "<p>An external link:&nbsp;<a href=\"http://www.enonic.com\">An external  link</a></p>\n" +
                       "<p>&nbsp;</p>\n" +
                       "<a href=\"media://inline/" + content.getId() + "\">Inline</a>" );

        //Checks the returned value
        final String processedHtml = this.service.processHtml( params );
        assertEquals( "<p>A content link:&nbsp;<a href=\"/portal/draft" + content.getPath() + "\">FirstLink</a></p>\n" +
                          "<p>A second content link:&nbsp;<a href=\"/portal/draft" + content.getPath() + "\">SecondLink</a>" +
                          "&nbsp;and a download link:&nbsp;<a href=\"/portal/draft/context/path/_/attachment/download/" +
                          content.getId() + "/" + source.getName() + "\">Download</a></p>\n" +
                          "<p>An external link:&nbsp;<a href=\"http://www.enonic.com\">An external  link</a></p>\n" +
                          "<p>&nbsp;</p>\n" +
                          "<a href=\"/portal/draft/context/path/_/attachment/inline/" +
                          content.getId() + "/" + source.getName() + "\">Inline</a>", processedHtml );
    }

    @Test
    public void process_unknown_content()
    {

        //Process an html text containing a link to an unknown content
        final ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"content://123\">Content</a>" );

        //Checks that the error 500 page is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals( "<a href=\"/portal/draft/context/path/_/error/500\">Content</a>", processedHtml );
    }

    @Test
    public void process_unknown_media()
    {

        //Process an html text containing a link to an unknown media
        final ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"media://inline/123\">Media</a>" );

        //Checks that the error 500 page is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals( "<a href=\"/portal/draft/context/path/_/error/500\">Media</a>", processedHtml );
    }

    @Test
    public void process_unknown_image()
    {

        //Process an html text containing a link to an unknown media
        final ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"image://123\">Image</a>" );

        //Checks that the error 500 page is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals( "<a href=\"/portal/draft/context/path/_/error/500\">Image</a>", processedHtml );
    }

}
