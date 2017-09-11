package com.enonic.xp.portal.impl.url;

import java.io.IOException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

import com.google.common.base.Charsets;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.Media;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.url.ProcessHtmlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.web.servlet.ServletRequestHolder;

import static org.junit.Assert.*;

public class PortalUrlServiceImpl_processHtmlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Before
    public void before()
    {

    }

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
        final Media media = ContentFixtures.newMedia();
        Mockito.when( this.contentService.getById( media.getId() ) ).thenReturn( media );
        Mockito.when( this.contentService.getBinaryKey( media.getId(), media.getMediaAttachment().getBinaryReference() ) ).thenReturn(
            "binaryHash" );

        //Process an html text containing a link to this content
        final ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"image://" + media.getId() + "\">Image</a>" );

        //Checks that the page URL of the content is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals(
            "<a href=\"/portal/draft/context/path/_/image/" + media.getId() + ":8cf45815bba82c9711c673c9bb7304039a790026/" + "full" +
                "/" + media.getName() +
                "\">Image</a>", processedHtml );
    }

    @Test
    public void process_image_with_keepsize()
    {
        //Creates a content
        final Media media = ContentFixtures.newMedia();
        Mockito.when( this.contentService.getById( media.getId() ) ).thenReturn( media );
        Mockito.when( this.contentService.getBinaryKey( media.getId(), media.getMediaAttachment().getBinaryReference() ) ).thenReturn(
            "binaryHash" );

        //Process an html text containing a link to this content
        final ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"image://" + media.getId() + "?keepsize=true\">Image</a>" );

        //Checks that the page URL of the content is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals(
            "<a href=\"/portal/draft/context/path/_/image/" + media.getId() + ":8cf45815bba82c9711c673c9bb7304039a790026/" + "width-768" +
                "/" + media.getName() + "\">Image</a>", processedHtml );
    }

    @Test
    public void process_image_with_custom_size()
    {
        //Creates a content
        final Media media = ContentFixtures.newMedia();
        Mockito.when( this.contentService.getById( media.getId() ) ).thenReturn( media );
        Mockito.when( this.contentService.getBinaryKey( media.getId(), media.getMediaAttachment().getBinaryReference() ) ).thenReturn(
            "binaryHash" );

        //Process an html text containing a link to this content
        final ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"image://" + media.getId() + "?size=120\">Image</a>" );

        //Checks that the page URL of the content is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals(
            "<a href=\"/portal/draft/context/path/_/image/" + media.getId() + ":992a0004e50e58383fb909fea2b588dc714a7115/" + "width-120" +
                "/" + media.getName() + "\">Image</a>", processedHtml );
    }

    @Test
    public void process_image_with_custom_size_scaling()
    {
        //Creates a content
        final Media media = ContentFixtures.newMedia();
        Mockito.when( this.contentService.getById( media.getId() ) ).thenReturn( media );
        Mockito.when( this.contentService.getBinaryKey( media.getId(), media.getMediaAttachment().getBinaryReference() ) ).thenReturn(
            "binaryHash" );

        //Process an html text containing a link to this content
        final ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"image://" + media.getId() + "?size=120&scale=3:4\">Image</a>" );

        //Checks that the page URL of the content is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals(
            "<a href=\"/portal/draft/context/path/_/image/" + media.getId() + ":992a0004e50e58383fb909fea2b588dc714a7115/" + "block-120-160" +
                "/" + media.getName() + "\">Image</a>", processedHtml );
    }

    @Test
    public void process_image_with_keepsize_overrides_size()
    {
        //Creates a content
        final Media media = ContentFixtures.newMedia();
        Mockito.when( this.contentService.getById( media.getId() ) ).thenReturn( media );
        Mockito.when( this.contentService.getBinaryKey( media.getId(), media.getMediaAttachment().getBinaryReference() ) ).thenReturn(
            "binaryHash" );

        //Process an html text containing a link to this content
        final ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"image://" + media.getId() + "?keepSize=true&size=120&scale=3:4\">Image</a>" );

        //Checks that the page URL of the content is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals(
            "<a href=\"/portal/draft/context/path/_/image/" + media.getId() + ":992a0004e50e58383fb909fea2b588dc714a7115/" + "block-300-400" +
                "/" + media.getName() + "\">Image</a>", processedHtml );
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
        Mockito.when( this.contentService.getBinaryKey( content.getId(), source.getBinaryReference() ) ).thenReturn( "binaryHash2" );

        //Process an html text containing an inline link to this content
        ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"media://inline/" + content.getId() + "\">Media</a>" );

        //Checks that the URL of the source attachment of the content is returned
        String processedHtml = this.service.processHtml( params );
        assertEquals( "<a href=\"/portal/draft/context/path/_/attachment/inline/" + content.getId() + ":binaryHash2/" + source.getName() +
                          "\">Media</a>", processedHtml );

        //Process an html text containing a download link to this content
        params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"media://download/" + content.getId() + "\">Media</a>" );

        //Checks that the URL of the source attachment of the content is returned
        processedHtml = this.service.processHtml( params );
        assertEquals( "<a href=\"/portal/draft/context/path/_/attachment/download/" + content.getId() + ":binaryHash2/" + source.getName() +
                          "\">Media</a>", processedHtml );

        //Process an html text containing an inline link to this content in a img tag
        params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"/some/page\"><img src=\"media://inline/" + content.getId() + "\">Media</a>" );

        //Checks that the URL of the source attachment of the content is returned
        processedHtml = this.service.processHtml( params );
        assertEquals(
            "<a href=\"/some/page\"><img src=\"/portal/draft/context/path/_/attachment/inline/" + content.getId() + ":binaryHash2/" +
                source.getName() +
                "\">Media</a>", processedHtml );

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
        Mockito.when( this.contentService.getBinaryKey( content.getId(), source.getBinaryReference() ) ).thenReturn( "binaryHash2" );

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
                          content.getId() + ":binaryHash2/" + source.getName() + "\">Download</a></p>\n" +
                          "<p>An external link:&nbsp;<a href=\"http://www.enonic.com\">An external  link</a></p>\n" +
                          "<p>&nbsp;</p>\n" +
                          "<a href=\"/portal/draft/context/path/_/attachment/inline/" +
                          content.getId() + ":binaryHash2/" + source.getName() + "\">Inline</a>", processedHtml );
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

    @Test
    public void process_absolute()
    {
        //Creates a content
        final Content content = ContentFixtures.newContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        //Process an html text containing a link to this content
        final ProcessHtmlParams params = new ProcessHtmlParams().
            type( UrlTypeConstants.ABSOLUTE ).
            portalRequest( this.portalRequest ).
            value( "<a href=\"content://" + content.getId() + "\">Content</a>" );

        MockHttpServletRequest req = new MockHttpServletRequest();
        ServletRequestHolder.setRequest( req );

        //Checks that the page URL of the content is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals( "<a href=\"http://localhost/portal/draft" + content.getPath() + "\">Content</a>", processedHtml );
    }

    @Test
    public void process_image_with_scale()
    {
        //Creates a content
        final Media media = ContentFixtures.newMedia();
        Mockito.when( this.contentService.getById( media.getId() ) ).thenReturn( media );
        Mockito.when( this.contentService.getBinaryKey( media.getId(), media.getMediaAttachment().getBinaryReference() ) ).thenReturn(
            "binaryHash" );

        //Process an html text containing a link to this content
        final ProcessHtmlParams params = new ProcessHtmlParams().
            portalRequest( this.portalRequest ).
            value( "<a href=\"image://" + media.getId() + "?scale=21:9&amp;keepSize=true\">Image</a>" );

        //Checks that the page URL of the content is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals( "<a href=\"/portal/draft/context/path/_/image/" + media.getId() + ":8cf45815bba82c9711c673c9bb7304039a790026/" +
                          "block-300-126" +
                          "/" + media.getName() +
                          "\">Image</a>", processedHtml );
    }

    @Test
    public void process_html_with_macros()
        throws IOException
    {
        assertProcessHtml( "html-with-macros-input.txt", "html-with-macros-output.txt" );
        assertProcessHtml( "html-with-unclosed-macro-input.txt", "html-with-unclosed-macro-output.txt" );
    }

    private void assertProcessHtml( String inputName, String expectedOutputName )
        throws IOException
    {
        //Reads the input and output files
        final URL inputUrl = this.getClass().getResource( inputName );
        final CharSource inputCharSource = Resources.asCharSource( inputUrl, Charsets.UTF_8 );
        final URL expectedOutputUrl = this.getClass().getResource( expectedOutputName );
        final CharSource expectedOutputCharSource = Resources.asCharSource( expectedOutputUrl, Charsets.UTF_8 );

        //Processes the input file
        final ProcessHtmlParams processHtmlParams = new ProcessHtmlParams().
            value( inputCharSource.read() );
        final String processedHtml = this.service.processHtml( processHtmlParams );

        //Checks that the processed text is equal to the expected output
        assertEquals( expectedOutputCharSource.read(), processedHtml );
    }
}
