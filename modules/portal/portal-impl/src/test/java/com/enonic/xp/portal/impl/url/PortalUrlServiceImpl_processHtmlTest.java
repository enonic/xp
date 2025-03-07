package com.enonic.xp.portal.impl.url;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.Media;
import com.enonic.xp.portal.html.HtmlDocument;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.url.ProcessHtmlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.StyleDescriptor;
import com.enonic.xp.style.StyleDescriptors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

public class PortalUrlServiceImpl_processHtmlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void process_empty_value()
    {
        //Checks the process for a null value
        final ProcessHtmlParams params = new ProcessHtmlParams().portalRequest( this.portalRequest );
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
        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        //Process an html text containing a link to this content
        final ProcessHtmlParams params = new ProcessHtmlParams().portalRequest( this.portalRequest )
            .value( "<a href=\"content://" + content.getId() + "\">Content</a>" );

        //Checks that the page URL of the content is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals( "<a href=\"/site/myproject/draft" + content.getPath() + "\">Content</a>", processedHtml );
    }

    @Test
    public void process_single_image()
    {
        //Creates a content
        final Media media = ContentFixtures.newMedia();
        when( this.contentService.getById( media.getId() ) ).thenReturn( media );

        //Process an html text containing a link to this content
        final ProcessHtmlParams params = new ProcessHtmlParams().value( "<a href=\"image://" + media.getId() + "\">Image</a>" );

        //Checks that the page URL of the content is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals(
            "<a href=\"/site/myproject/draft/_/media:image/myproject:draft/" + media.getId() + ":b12b4c973748042e3b3a7e4798344289/" +
                "width-768" + "/" + media.getName() + "\">Image</a>", processedHtml );
    }

    @Test
    public void process_single_media()
    {
        //Creates a content with attachments
        final Attachment thumb = Attachment.create().label( "thumb" ).name( "a1.jpg" ).mimeType( "image/jpeg" ).build();
        final Attachment source = Attachment.create()
            .label( "source" )
            .name( "a2.jpg" )
            .mimeType( "image/jpeg" )
            .sha512( "bb6d2c0f3112f562ec454654b9aebe7ab47ba865" )
            .build();
        final Attachments attachments = Attachments.from( thumb, source );
        final Content content = Content.create( ContentFixtures.newContent() ).attachments( attachments ).build();
        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        Map<String, String> projection = new HashMap<>();

        //Process an html text containing an inline link to this content
        ProcessHtmlParams params = new ProcessHtmlParams().customHtmlProcessor( processorParams -> {
            processorParams.processDefault( ( element, properties ) -> {
                if ( "a".equals( element.getTagName() ) )
                {
                    element.setAttribute( "data-link-ref", "linkRef" );

                    projection.clear();
                    projection.putAll( properties );
                }
            } );
            return null;
        } ).value( "<a href=\"media://inline/" + content.getId() + "\">Media</a>" );

        //Checks that the URL of the source attachment of the content is returned
        String processedHtml = this.service.processHtml( params );
        assertEquals(
            "<a href=\"/site/myproject/draft/_/media:attachment/myproject:draft/" + content.getId() + ":bb6d2c0f3112f562ec454654b9aebe7a/" +
                source.getName() + "\" data-link-ref=\"linkRef\">Media</a>", processedHtml );

        assertEquals( content.getId().toString(), projection.get( "contentId" ) );
        assertEquals( "server", projection.get( "type" ) );
        assertEquals( "media://inline/" + content.getId(), projection.get( "uri" ) );
        assertEquals( "inline", projection.get( "mode" ) );
        assertNull( projection.get( "queryParams" ) );

        //Process an html text containing a download link to this content
        params = new ProcessHtmlParams().portalRequest( this.portalRequest )
            .value( "<a href=\"media://download/" + content.getId() + "\">Media</a>" );

        //Checks that the URL of the source attachment of the content is returned
        processedHtml = this.service.processHtml( params );
        assertEquals(
            "<a href=\"/site/myproject/draft/_/media:attachment/myproject:draft/" + content.getId() + ":bb6d2c0f3112f562ec454654b9aebe7a/" +
                source.getName() + "?download\">Media</a>", processedHtml );

        //Process an html text containing an inline link to this content in a img tag
        params = new ProcessHtmlParams().portalRequest( this.portalRequest )
            .value( "<a href=\"/some/page\"><img src=\"media://inline/" + content.getId() + "\">Media</a>" );

        //Checks that the URL of the source attachment of the content is returned
        processedHtml = this.service.processHtml( params );
        assertEquals( "<a href=\"/some/page\"><img src=\"/site/myproject/draft/_/media:attachment/myproject:draft/" + content.getId() +
                          ":bb6d2c0f3112f562ec454654b9aebe7a/" + source.getName() + "\">Media</a>", processedHtml );

    }

    @Test
    public void process_multiple_links()
    {
        //Creates a content with attachments
        final Attachment thumb = Attachment.create().label( "thumb" ).name( "a1.jpg" ).mimeType( "image/jpeg" ).build();
        final Attachment source = Attachment.create()
            .label( "source" )
            .name( "a2.jpg" )
            .mimeType( "image/jpeg" )
            .sha512( "bb6d2c0f3112f562ec454654b9aebe7ab47ba865" )
            .build();
        final Attachments attachments = Attachments.from( thumb, source );
        final Content content = Content.create( ContentFixtures.newContent() ).attachments( attachments ).build();
        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        //Process an html text containing multiple links, on multiple lines, to this content as a media and as a content
        final ProcessHtmlParams params = new ProcessHtmlParams().portalRequest( this.portalRequest )
            .value( "<p>A content link:&nbsp;<a href=\"content://" + content.getId() + "\">FirstLink</a></p>\n" +
                        "<p>A second content link:&nbsp;<a href=\"content://" + content.getId() + "\">SecondLink</a>" +
                        "&nbsp;and a download link:&nbsp;<a href=\"media://download/" + content.getId() + "\">Download</a></p>\n" +
                        "<p>An external link:&nbsp;<a href=\"http://www.enonic.com\">An external  link</a></p>\n" + "<p>&nbsp;</p>\n" +
                        "<a href=\"media://inline/" + content.getId() + "\">Inline</a>" );

        //Checks the returned value
        final String processedHtml = this.service.processHtml( params );
        assertEquals( "<p>A content link:&nbsp;<a href=\"/site/myproject/draft" + content.getPath() + "\">FirstLink</a></p>\n" +
                          "<p>A second content link:&nbsp;<a href=\"/site/myproject/draft" + content.getPath() + "\">SecondLink</a>" +
                          "&nbsp;and a download link:&nbsp;<a href=\"/site/myproject/draft/_/media:attachment/myproject:draft/" +
                          content.getId() + ":bb6d2c0f3112f562ec454654b9aebe7a/" + source.getName() + "?download\">Download</a></p>\n" +
                          "<p>An external link:&nbsp;<a href=\"http://www.enonic.com\">An external  link</a></p>\n" + "<p>&nbsp;</p>\n" +
                          "<a href=\"/site/myproject/draft/_/media:attachment/myproject:draft/" + content.getId() + ":bb6d2c0f3112f562ec454654b9aebe7a/" + source.getName() +
                          "\">Inline</a>", processedHtml );
    }

    @Test
    public void process_unknown_content()
    {
        when( contentService.getById( isA( ContentId.class ) ) ).thenAnswer( ( params ) -> {
            final ContentId contentId = params.getArgument( 0 );
            throw ContentNotFoundException.create()
                .contentId( contentId )
                .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
                .branch( ContentConstants.BRANCH_DRAFT )
                .build();
        } );

        //Process an html text containing a link to an unknown content
        final ProcessHtmlParams params =
            new ProcessHtmlParams().portalRequest( this.portalRequest ).value( "<a href=\"content://123\">Content</a>" );

        //Checks that the error 500 page is returned
        final String processedHtml = this.service.processHtml( params );
        assertThat( processedHtml ).matches( "<a href=\"/_/error/404\\?message=Not\\+Found\\.\\+\\w+?\">Content</a>" );
    }

    @Test
    public void process_unknown_media()
    {
        when( contentService.getById( isA( ContentId.class ) ) ).thenAnswer( ( params ) -> {
            final ContentId contentId = params.getArgument( 0 );
            throw ContentNotFoundException.create().contentId( contentId ).branch( ContentConstants.BRANCH_DRAFT ).build();
        } );

        //Process an html text containing a link to an unknown media
        final ProcessHtmlParams params =
            new ProcessHtmlParams().portalRequest( this.portalRequest ).value( "<a href=\"media://inline/123\">Media</a>" );

        //Checks that the error 500 page is returned
        final String processedHtml = this.service.processHtml( params );
        assertThat( processedHtml ).matches( "<a href=\"/site/myproject/draft/_/error/404\\?message=Not\\+Found\\.\\+\\w+?\">Media</a>" );
    }

    @Test
    public void process_unknown_image()
    {
        when( contentService.getById( isA( ContentId.class ) ) ).thenAnswer( ( params ) -> {
            final ContentId contentId = params.getArgument( 0 );
            throw ContentNotFoundException.create()
                .contentId( contentId )
                .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
                .branch( ContentConstants.BRANCH_DRAFT )
                .build();
        } );

        //Process an html text containing a link to an unknown media
        final ProcessHtmlParams params =
            new ProcessHtmlParams().portalRequest( this.portalRequest ).value( "<a href=\"image://123\">Image</a>" );

        //Checks that the error 404 page is returned
        final String processedHtml = this.service.processHtml( params );
        assertThat( processedHtml ).matches( "<a href=\"/site/myproject/draft/_/error/404\\?message=Not\\+Found\\.\\+\\w+?\">Image</a>" );
    }

    @Test
    public void process_absolute()
    {
        //Creates a content
        final Content content = ContentFixtures.newContent();
        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        //Process an html text containing a link to this content
        final ProcessHtmlParams params = new ProcessHtmlParams().type( UrlTypeConstants.ABSOLUTE )
            .portalRequest( this.portalRequest )
            .value( "<a href=\"content://" + content.getId() + "\">Content</a>" );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        //Checks that the page URL of the content is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals( "<a href=\"http://localhost/site/myproject/draft" + content.getPath() + "\">Content</a>", processedHtml );
    }

    @Test
    public void process_image_with_scale()
    {
        //Creates a content
        final Media media = ContentFixtures.newMedia();
        when( this.contentService.getById( media.getId() ) ).thenReturn( media );

        //Process an html text containing a link to this content
        final ProcessHtmlParams params = new ProcessHtmlParams().portalRequest( this.portalRequest )
            .value( "<a href=\"image://" + media.getId() + "?scale=21:9\">Image</a>" );

        //Checks that the page URL of the content is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals(
            "<a href=\"/site/myproject/draft/_/media:image/myproject:draft/" + media.getId() + ":b12b4c973748042e3b3a7e4798344289/" +
                "block-768-324" + "/" + media.getName() + "\">Image</a>", processedHtml );
    }

    @Test
    public void process_html_with_macros()
        throws IOException
    {
        assertProcessHtml( "html-with-macros-input.txt", "html-with-macros-output.txt" );
        assertProcessHtml( "html-with-unclosed-macro-input.txt", "html-with-unclosed-macro-output.txt" );
    }

    @Test
    public void process_image_with_styles()
    {
        //Creates a content
        final Media media = ContentFixtures.newMedia();
        when( this.contentService.getById( media.getId() ) ).thenReturn( media );

        final ImageStyle imageStyle = ImageStyle.create().name( "mystyle" ).aspectRatio( "2:1" ).filter( "myfilter" ).build();
        final StyleDescriptor styleDescriptor =
            StyleDescriptor.create().application( ApplicationKey.from( "myapp" ) ).addStyleElement( imageStyle ).build();
        when( styleDescriptorService.getByApplications( Mockito.any() ) ).thenReturn( StyleDescriptors.from( styleDescriptor ) );

        final Map<String, String> imageProjection = new HashMap<>();

        //Process an html text containing a style
        final String link1 = "<img src=\"image://" + media.getId() + "?style=mystyle\">";
        final String link2 = "<a href=\"image://" + media.getId() + "?style=missingstyle\">Image</a>";
        final ProcessHtmlParams params1 =
            new ProcessHtmlParams().portalRequest( this.portalRequest ).customHtmlProcessor( processorParams -> {
                processorParams.processDefault( ( element, properties ) -> {
                    if ( "img".equals( element.getTagName() ) )
                    {
                        element.setAttribute( "data-image-ref", "imageRef" );
                        imageProjection.clear();
                        imageProjection.putAll( properties );
                    }
                } );
                return processorParams.getDocument().getInnerHtml();
            } ).value( link1 );
        final ProcessHtmlParams params2 = new ProcessHtmlParams().portalRequest( this.portalRequest ).value( link2 );
        final String processedLink1 = this.service.processHtml( params1 );
        final String processedLink2 = this.service.processHtml( params2 );

        //Checks that the page URL of the content is returned
        final String expectedResult1 =
            "<img src=\"/site/myproject/draft/_/media:image/myproject:draft/" + media.getId() + ":b12b4c973748042e3b3a7e4798344289/" +
                "block-768-384" + "/" + media.getName() + "?filter=myfilter\" data-image-ref=\"imageRef\">";
        final String expectedResult2 = "<a href=\"/site/myproject/draft/_/media:image/myproject:draft/" + media.getId() +
            ":b12b4c973748042e3b3a7e4798344289/width-768/" + media.getName() + "\">Image</a>";
        assertEquals( expectedResult1, processedLink1 );
        assertEquals( expectedResult2, processedLink2 );

        assertEquals( media.getId().toString(), imageProjection.get( "contentId" ) );
        assertEquals( "server", imageProjection.get( "type" ) );
        assertNull( imageProjection.get( "mode" ) );
        assertEquals( "?style=mystyle", imageProjection.get( "queryParams" ) );
        assertEquals( "mystyle", imageProjection.get( "style:name" ) );
        assertEquals( "2:1", imageProjection.get( "style:aspectRatio" ) );
        assertEquals( "myfilter", imageProjection.get( "style:filter" ) );
    }

    @Test
    public void processHtml_image_imageWidths()
    {
        //Creates a content
        final Media media = ContentFixtures.newMedia();
        when( this.contentService.getById( media.getId() ) ).thenReturn( media );

        //Process an html text containing a link to this content
        final ProcessHtmlParams params = new ProcessHtmlParams().portalRequest( this.portalRequest )
            .value( "<figure class=\"editor-align-justify\"><img alt=\"Alt text\" src=\"image://" + media.getId() +
                        "\"/><figcaption>Caption text</figcaption></figure>" )
            .imageWidths( List.of( 660, 1024 ) )
            .imageSizes( " " );

        //Checks that the page URL of the content is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals(
            "<figure class=\"editor-align-justify\">" + "<img alt=\"Alt text\" src=\"/site/myproject/draft/_/media:image/myproject:draft/" +
                media.getId() + ":b12b4c973748042e3b3a7e4798344289/width-768/mycontent\" " +
                "srcset=\"/site/myproject/draft/_/media:image/myproject:draft/" + media.getId() +
                ":b12b4c973748042e3b3a7e4798344289/width-660/mycontent 660w," + "/site/myproject/draft/_/media:image/myproject:draft/" +
                media.getId() +
                ":b12b4c973748042e3b3a7e4798344289/width-1024/mycontent 1024w\"><figcaption>Caption text</figcaption></figure>",
            processedHtml );
    }

    @Test
    public void processHtml_image_imageWidths_with_imageSizes()
    {
        //Creates a content
        final Media media = ContentFixtures.newMedia();
        when( this.contentService.getById( media.getId() ) ).thenReturn( media );

        //Process an html text containing a link to this content
        final ProcessHtmlParams params = new ProcessHtmlParams().portalRequest( this.portalRequest )
            .value( "<figure class=\"editor-align-justify\"><img alt=\"Alt text\" src=\"image://" + media.getId() +
                        "\"/><figcaption>Caption text</figcaption></figure>" )
            .imageWidths( List.of( 660, 1024 ) )
            .imageSizes( "(max-width: 960px) 660px" );

        //Checks that the page URL of the content is returned
        final String processedHtml = this.service.processHtml( params );
        assertEquals(
            "<figure class=\"editor-align-justify\">" + "<img alt=\"Alt text\" src=\"/site/myproject/draft/_/media:image/myproject:draft/" +
                media.getId() + ":b12b4c973748042e3b3a7e4798344289/width-768/mycontent\" " +
                "srcset=\"/site/myproject/draft/_/media:image/myproject:draft/" + media.getId() +
                ":b12b4c973748042e3b3a7e4798344289/width-660/mycontent 660w," + "/site/myproject/draft/_/media:image/myproject:draft/" +
                media.getId() +
                ":b12b4c973748042e3b3a7e4798344289/width-1024/mycontent 1024w\" sizes=\"(max-width: 960px) 660px\"><figcaption>Caption text</figcaption></figure>",
            processedHtml );
    }

    @Test
    public void processHtml_content_queryParams()
    {
        final Attachment source = Attachment.create().label( "source" ).name( "source.jpg" ).mimeType( "image/jpeg" ).build();

        final Content content = Content.create( ContentFixtures.newContent() ).build();

        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        // test without query params
        ProcessHtmlParams params = new ProcessHtmlParams().portalRequest( this.portalRequest )
            .value( "<p><a href=\"content://" + content.getId() + "\">Text</a></p>\n" );

        String processedHtml = this.service.processHtml( params );
        assertEquals( "<p><a href=\"/site/myproject/draft" + content.getPath() + "\">Text</a></p>\n", processedHtml );

        String queryParam = "query=k1%3Dv1%26k2%3Dv2"; // k1=v1&k2=v2
        String fragmentParam = "fragment=some-fragment";

        // test with query and fragment
        params = new ProcessHtmlParams().portalRequest( this.portalRequest )
            .value( "<p><a href=\"content://" + content.getId() + "?" + String.join( "&", List.of( queryParam, fragmentParam ) ) +
                        "\">Text</a></p>\n" );

        processedHtml = this.service.processHtml( params );
        assertEquals( "<p><a href=\"/site/myproject/draft" + content.getPath() + "?k1=v1&amp;k2=v2#some-fragment\">Text</a></p>\n",
                      processedHtml );

        // test only with query
        params = new ProcessHtmlParams().portalRequest( this.portalRequest )
            .value( "<p><a href=\"content://" + content.getId() + "?" + queryParam + "\">Text</a></p>\n" );

        processedHtml = this.service.processHtml( params );
        assertEquals( "<p><a href=\"/site/myproject/draft" + content.getPath() + "?k1=v1&amp;k2=v2\">Text</a></p>\n", processedHtml );

        // test only with fragment
        params = new ProcessHtmlParams().portalRequest( this.portalRequest )
            .value( "<p><a href=\"content://" + content.getId() + "?" + fragmentParam + "\">Text</a></p>\n" );

        processedHtml = this.service.processHtml( params );
        assertEquals( "<p><a href=\"/site/myproject/draft" + content.getPath() + "#some-fragment\">Text</a></p>\n", processedHtml );

        // test with unsupported symbols in query and fragment
        queryParam = "query=k%3Dh%C3%A5ndkl%C3%A6r"; // query=k=håndklær
        fragmentParam = "fragment=h%C3%A5ndkl%C3%A6r"; // fragment=håndklær

        // test with query and fragment
        params = new ProcessHtmlParams().portalRequest( this.portalRequest )
            .value( "<p><a href=\"content://" + content.getId() + "?" + String.join( "&", List.of( queryParam, fragmentParam ) ) +
                        "\">Text</a></p>\n" );

        processedHtml = this.service.processHtml( params );
        assertEquals( "<p><a href=\"/site/myproject/draft" + content.getPath() + "\">Text</a></p>\n", processedHtml );

        // try to pass håndklær, where `å` and `æ` are encoded twice
        // query=encodeFn('k=h' + encodeFn('å') + 'ndkl' + encodeFn('æ') + 'r'), where å = %25C3%25A5  æ = %25C3%25A6
        queryParam = "query=k%3Dh%25C3%25A5ndkl%25C3%25A6r";

        // test with query and fragment
        params = new ProcessHtmlParams().portalRequest( this.portalRequest )
            .value( "<p><a href=\"content://" + content.getId() + "?" + queryParam + "\">Text</a></p>\n" );

        processedHtml = this.service.processHtml( params );
        assertEquals( "<p><a href=\"/site/myproject/draft" + content.getPath() + "?k=h%C3%A5ndkl%C3%A6r\">Text</a></p>\n", processedHtml );
    }

    @Test
    public void testProcessHtmlWithCustomProcessor()
    {
        final Attachment source = Attachment.create().label( "source" ).name( "source.jpg" ).mimeType( "image/jpeg" ).build();

        final Content content = Content.create( ContentFixtures.newContent() ).build();

        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        ProcessHtmlParams params = new ProcessHtmlParams().portalRequest( this.portalRequest )
            .value( "<a href=\"content://" + content.getId() + "\">Text</a>" )
            .customHtmlProcessor( processorParams -> {
                processorParams.processDefault();
                return processorParams.getDocument().getInnerHtml();
            } );

        String result = this.service.processHtml( params );

        assertEquals( "<a href=\"/site/myproject/draft" + content.getPath() + "\">Text</a>", result );

        // #######################

        final Map<String, String> linkProjection = new HashMap<>();

        params = new ProcessHtmlParams().portalRequest( this.portalRequest )
            .value( "<a href=\"content://" + content.getId() + "\">Text</a>" )
            .customHtmlProcessor( processorParams -> {
                processorParams.processDefault( ( element, properties ) -> {
                    if ( "a".equals( element.getTagName() ) )
                    {
                        element.setAttribute( "data-link-ref", "linkRef" );

                        linkProjection.clear();
                        linkProjection.putAll( properties );
                    }
                } );

                return processorParams.getDocument().getInnerHtml();
            } );

        result = this.service.processHtml( params );

        assertEquals( "<a href=\"/site/myproject/draft" + content.getPath() + "\" data-link-ref=\"linkRef\">Text</a>", result );
        assertEquals( content.getId().toString(), linkProjection.get( "contentId" ) );
        assertEquals( "server", linkProjection.get( "type" ) );
        assertEquals( "content://" + content.getId(), linkProjection.get( "uri" ) );
        assertNull( linkProjection.get( "mode" ) );
        assertNull( linkProjection.get( "queryParams" ) );

        // #######################

        params = new ProcessHtmlParams().portalRequest( this.portalRequest )
            .value( "<a href=\"content://" + content.getId() + "\">Text</a>" )
            .customHtmlProcessor( processorParams -> {
                final HtmlDocument document = processorParams.getDocument();
                document.select( "a" ).forEach( processorParams::processElementDefault );
                return document.getInnerHtml();
            } );

        result = this.service.processHtml( params );

        assertEquals( "<a href=\"/site/myproject/draft" + content.getPath() + "\">Text</a>", result );

        // #######################

        params = new ProcessHtmlParams().portalRequest( this.portalRequest )
            .value( "<a href=\"content://" + content.getId() + "\">Text</a>\n[correct_macro/]" )
            .customHtmlProcessor( processorParams -> {
                final HtmlDocument document = processorParams.getDocument();
                document.select( "a" )
                    .forEach( element -> processorParams.processElementDefault( element,
                                                                                ( el, properties ) -> element.setAttribute( "data-link-ref",
                                                                                                                            "linkRef" ) ) );

                return document.getInnerHtml();
            } );

        result = this.service.processHtml( params );

        assertEquals( "<a href=\"/site/myproject/draft" + content.getPath() +
                          "\" data-link-ref=\"linkRef\">Text</a>\n<!--#MACRO _name=\"correct_macro\" _document=\"__macroDocument10\" _body=\"\"-->",
                      result );
    }

    @Test
    public void testCustomStyleDescriptorCallback()
    {
        final Media media = ContentFixtures.newMedia();
        when( this.contentService.getById( media.getId() ) ).thenReturn( media );

        final ImageStyle imageStyle = ImageStyle.create().name( "mystyle" ).aspectRatio( "2:1" ).filter( "myfilter" ).build();
        final StyleDescriptor styleDescriptor1 =
            StyleDescriptor.create().application( ApplicationKey.from( "myapp1" ) ).addStyleElement( imageStyle ).build();
        final StyleDescriptor styleDescriptor2 =
            StyleDescriptor.create().application( ApplicationKey.from( "myapp2" ) ).addStyleElement( imageStyle ).build();

        final Map<String, String> imageProjection = new HashMap<>();

        //Process a html text containing a style
        final String link1 = "<img src=\"image://" + media.getId() + "?style=mystyle\">";

        final ProcessHtmlParams params = new ProcessHtmlParams().portalRequest( this.portalRequest )
            .customStyleDescriptorsCallback( () -> StyleDescriptors.from( styleDescriptor1, styleDescriptor2 ) )
            .customHtmlProcessor( processorParams -> {
                processorParams.processDefault( ( element, properties ) -> {
                    if ( "img".equals( element.getTagName() ) )
                    {
                        element.setAttribute( "data-image-ref", "imageRef" );
                        imageProjection.clear();
                        imageProjection.putAll( properties );
                    }
                } );
                return processorParams.getDocument().getInnerHtml();
            } )
            .value( link1 );

        final String processedLink = this.service.processHtml( params );

        final String expectedResult =
            "<img src=\"/site/myproject/draft/_/media:image/myproject:draft/" + media.getId() + ":b12b4c973748042e3b3a7e4798344289/" +
                "block-768-384" + "/" + media.getName() + "?filter=myfilter\" data-image-ref=\"imageRef\">";

        assertEquals( expectedResult, processedLink );
        assertEquals( media.getId().toString(), imageProjection.get( "contentId" ) );
        assertEquals( "server", imageProjection.get( "type" ) );
        assertNull( imageProjection.get( "mode" ) );
        assertEquals( "?style=mystyle", imageProjection.get( "queryParams" ) );
        assertEquals( "mystyle", imageProjection.get( "style:name" ) );
        assertEquals( "2:1", imageProjection.get( "style:aspectRatio" ) );
        assertEquals( "myfilter", imageProjection.get( "style:filter" ) );
    }

    private void assertProcessHtml( String inputName, String expectedOutputName )
        throws IOException
    {
        final String input;
        final String expected;
        //Reads the input and output files
        try (InputStream is = this.getClass().getResourceAsStream( inputName ))
        {
            input = new String( is.readAllBytes(), StandardCharsets.UTF_8 );
        }
        try (InputStream is = this.getClass().getResourceAsStream( expectedOutputName ))
        {
            expected = new String( is.readAllBytes(), StandardCharsets.UTF_8 );
        }

        //Processes the input file
        final ProcessHtmlParams processHtmlParams = new ProcessHtmlParams().value( input );
        final String processedHtml = this.service.processHtml( processHtmlParams );

        //Checks that the processed text is equal to the expected output
        assertEquals( expected, processedHtml );
    }
}
