package com.enonic.xp.portal.impl.url;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Suppliers;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.macro.MacroService;
import com.enonic.xp.portal.html.HtmlDocument;
import com.enonic.xp.portal.html.HtmlElement;
import com.enonic.xp.portal.impl.ImageScaling;
import com.enonic.xp.portal.impl.html.HtmlParser;
import com.enonic.xp.portal.url.AttachmentUrlParts;
import com.enonic.xp.portal.url.AttachmentUrlPartsParams;
import com.enonic.xp.portal.url.HtmlElementPostProcessor;
import com.enonic.xp.portal.url.HtmlProcessorParams;
import com.enonic.xp.portal.url.ImageUrlParts;
import com.enonic.xp.portal.url.ImageUrlPartsParams;
import com.enonic.xp.portal.url.PageUrlParts;
import com.enonic.xp.portal.url.PageUrlPartsParams;
import com.enonic.xp.portal.url.PortalUrlGeneratorService;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ProcessHtmlPartsParams;
import com.enonic.xp.portal.url.ProcessedHtml;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.StyleDescriptorService;
import com.enonic.xp.style.StyleDescriptors;

/**
 * Processes rich text for {@link PortalUrlService#processHtmlParts}: resolves every internal link from configuration,
 * for the site or project the HTML belongs to, and writes a placeholder for each one.
 */
final class RichTextPartsProcessor
{
    private static final Logger LOG = LoggerFactory.getLogger( RichTextPartsProcessor.class );

    private final StyleDescriptorService styleDescriptorService;

    private final PortalUrlService portalUrlService;

    private final PortalUrlGeneratorService portalUrlGeneratorService;

    private final MacroService macroService;

    private final ContentService contentService;

    private final ProcessHtmlPartsParams params;

    private final BaseUrlMetadata metadata;

    private final Supplier<Map<String, ImageStyle>> imageStyles;

    private final List<ProcessedHtml.Link> links = new ArrayList<>();

    private final List<ProcessedHtml.Image> images = new ArrayList<>();

    RichTextPartsProcessor( final StyleDescriptorService styleDescriptorService, final PortalUrlService portalUrlService,
                            final PortalUrlGeneratorService portalUrlGeneratorService, final MacroService macroService,
                            final ContentService contentService, final ProcessHtmlPartsParams params, final BaseUrlMetadata metadata )
    {
        this.styleDescriptorService = styleDescriptorService;
        this.portalUrlService = portalUrlService;
        this.portalUrlGeneratorService = portalUrlGeneratorService;
        this.macroService = macroService;
        this.contentService = contentService;
        this.params = params;
        this.metadata = metadata;
        this.imageStyles = Suppliers.memoize( () -> RichTextLinks.imageStyles( styleDescriptors() ) );
    }

    ProcessedHtml process()
    {
        final String baseUrl = baseUrl();

        if ( params.getValue() == null )
        {
            return new ProcessedHtml( "", baseUrl, List.of(), List.of() );
        }

        final HtmlDocument document = HtmlParser.parse( params.getValue() );

        final String processed;
        if ( params.getCustomHtmlProcessor() == null )
        {
            processDocument( document, null );
            processed = document.getInnerHtml();
        }
        else
        {
            processed = params.getCustomHtmlProcessor()
                .apply( HtmlProcessorParams.create()
                            .htmlDocument( document )
                            .defaultProcessor( postProcessor -> processDocument( document, postProcessor ) )
                            .defaultElementProcessor( this::processElement )
                            .build() );
        }

        final String html = params.isProcessMacros() ? new HtmlMacroProcessor( macroService ).process( processed ) : processed;

        return new ProcessedHtml( html, baseUrl, links, images );
    }

    private String baseUrl()
    {
        final String configured = metadata.baseUrl();
        return configured == null || configured.isEmpty() ? null : UrlGenerator.removeTrailingSlash( configured );
    }

    private void processDocument( final HtmlDocument document, final HtmlElementPostProcessor postProcessor )
    {
        document.select( "[href],[src]" ).forEach( element -> processElement( element, postProcessor ) );
        RichTextLinks.removeEmptyCaptions( document );
    }

    private void processElement( final HtmlElement element, final HtmlElementPostProcessor postProcessor )
    {
        final RichTextLinks.Link link = RichTextLinks.find( element );

        if ( link == null )
        {
            return;
        }

        final String ref = UUID.randomUUID().toString();

        final Map<String, String> properties = new HashMap<>();
        properties.put( "contentId", link.id() );
        properties.put( "queryParams", link.urlParamsString() );
        properties.put( "ref", ref );

        // a link that does not resolve is left as written, and its element keeps no ref
        try
        {
            processLink( element, link, ref, properties );
        }
        catch ( RuntimeException e )
        {
            LOG.debug( "Link [{}] left as written", link.uri(), e );
            return;
        }

        if ( postProcessor != null )
        {
            postProcessor.process( element, properties );
        }
    }

    private void processLink( final HtmlElement element, final RichTextLinks.Link link, final String ref,
                              final Map<String, String> properties )
    {
        switch ( link.type() )
        {
            case RichTextLinks.CONTENT_TYPE ->
            {
                final String href = contentLink( ref, link );
                element.setAttribute( ProcessedHtml.LINK_REF_ATTRIBUTE, ref );
                element.setAttribute( link.attribute(), href );
                properties.put( "uri", link.uri() );
            }
            case RichTextLinks.MEDIA_TYPE ->
            {
                final String href = attachmentLink( ref, link );
                element.setAttribute( ProcessedHtml.LINK_REF_ATTRIBUTE, ref );
                element.setAttribute( link.attribute(), href );
                properties.put( "uri", link.uri() );
                properties.put( "mode", link.mode() );
            }
            case RichTextLinks.IMAGE_TYPE ->
            {
                final ImageStyle style = image( element, ref, link );
                if ( style != null )
                {
                    properties.put( "style:name", style.getName() );
                    properties.put( "style:aspectRatio", style.getAspectRatio() );
                    properties.put( "style:filter", style.getFilter() );
                }
            }
            default -> throw new IllegalStateException( "Unknown type " + link.type() );
        }
    }

    private String contentLink( final String ref, final RichTextLinks.Link link )
    {
        final PageUrlParts parts =
            portalUrlService.pageUrlParts( PageUrlPartsParams.create().setId( link.id() ).setBase( params.getBase() ).build() );

        final Map<String, String> urlParams = link.urlParams();
        final String query = RichTextLinks.validQueryOrFragment( urlParams.get( "query" ) );
        final String fragment = RichTextLinks.validQueryOrFragment( urlParams.get( "fragment" ) );
        final String queryString = query == null ? "" : "?" + query;

        links.add( new ProcessedHtml.ContentLink( ref, link.uri(), link.id(),
                                                  new PageUrlParts( parts.baseUrl(), parts.path(), queryString ), fragment ) );

        // the level itself has an empty relative path: its root is linked instead of the document
        final String path = parts.path().isEmpty() ? "/" : parts.path();
        return path + queryString + ( fragment == null ? "" : "#" + fragment );
    }

    private String attachmentLink( final String ref, final RichTextLinks.Link link )
    {
        final boolean download = RichTextLinks.DOWNLOAD_MODE.equals( link.mode() );
        final AttachmentUrlParts parts = portalUrlGeneratorService.attachmentUrlParts( AttachmentUrlPartsParams.create()
                                                                                          .setId( link.id() )
                                                                                          .setProjectName( metadata::projectName )
                                                                                          .setBranch( metadata::branch )
                                                                                          .setDownload( download )
                                                                                          .build() );

        links.add( new ProcessedHtml.AttachmentLink( ref, link.uri(), link.id(), parts, download ) );

        return parts.path() + parts.queryString();
    }

    private ImageStyle image( final HtmlElement element, final String ref, final RichTextLinks.Link link )
    {
        final String id = link.id();
        final Map<String, String> urlParams = link.urlParams();
        final String styleName = urlParams.get( "style" );
        final ImageStyle style = styleName == null ? null : imageStyles.get().get( styleName );
        final String scaleFromQueryString = urlParams.get( "scale" );

        // looked up once for the src and every srcset width
        final Supplier<Media> media =
            Suppliers.memoize( () -> MediaLookup.media( contentService, metadata.projectName(), metadata.branch(), id ) );

        final ImageUrlParts src = imageParts( media, style, DefaultImageLinkProcessor.scale( style, scaleFromQueryString, null ) );

        final boolean responsive = "img".equals( element.getTagName() ) && ImageScaling.isScalable( media.get() );
        final List<ProcessedHtml.Source> srcset = new ArrayList<>();
        if ( responsive && params.getImageWidths() != null )
        {
            for ( final Integer width : params.getImageWidths() )
            {
                final String scale = DefaultImageLinkProcessor.scale( style, scaleFromQueryString, width );
                srcset.add( new ProcessedHtml.Source( width, imageParts( media, style, scale ) ) );
            }
        }

        element.setAttribute( ProcessedHtml.IMAGE_REF_ATTRIBUTE, ref );
        element.setAttribute( link.attribute(), src.path() + src.queryString() );
        if ( responsive && params.getImageWidths() != null )
        {
            element.setAttribute( "srcset", srcset.stream()
                .map( source -> source.url().path() + source.url().queryString() + " " + source.width() + "w" )
                .collect( Collectors.joining( "," ) ) );
        }
        if ( responsive && params.getImageSizes() != null && !params.getImageSizes().isBlank() )
        {
            element.setAttribute( "sizes", params.getImageSizes() );
        }

        images.add( new ProcessedHtml.Image( ref, id, style, src, srcset ) );

        return style;
    }

    private ImageUrlParts imageParts( final Supplier<Media> media, final ImageStyle style, final String scale )
    {
        return portalUrlGeneratorService.imageUrlParts( ImageUrlPartsParams.create()
                                                            .setMedia( media )
                                                            .setProjectName( metadata::projectName )
                                                            .setBranch( metadata::branch )
                                                            .setScale( scale )
                                                            .setFilter( style == null ? null : style.getFilter() )
                                                            .build() );
    }

    /**
     * @return the style descriptors the callback gives, or else those of the system application and of the applications
     * of the site or project the HTML belongs to
     */
    private StyleDescriptors styleDescriptors()
    {
        return params.getCustomStyleDescriptorsCallback() != null
            ? params.getCustomStyleDescriptorsCallback().get()
            : RichTextLinks.styleDescriptors( styleDescriptorService, metadata.siteConfigs() );
    }
}
