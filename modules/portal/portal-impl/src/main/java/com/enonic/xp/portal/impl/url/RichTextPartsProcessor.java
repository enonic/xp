package com.enonic.xp.portal.impl.url;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Suppliers;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.macro.Macro;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroService;
import com.enonic.xp.portal.html.HtmlDocument;
import com.enonic.xp.portal.html.HtmlElement;
import com.enonic.xp.portal.impl.ImageScaling;
import com.enonic.xp.portal.impl.html.HtmlParser;
import com.enonic.xp.portal.impl.macro.MacroDescriptorResolver;
import com.enonic.xp.portal.impl.macro.MacroParamNames;
import com.enonic.xp.portal.url.AttachmentUrlParts;
import com.enonic.xp.portal.url.AttachmentUrlPartsParams;
import com.enonic.xp.portal.url.HtmlElementPostProcessor;
import com.enonic.xp.portal.url.HtmlProcessorParams;
import com.enonic.xp.portal.url.ImageUrlParts;
import com.enonic.xp.portal.url.ImageUrlPartsParams;
import com.enonic.xp.portal.url.PageUrlParts;
import com.enonic.xp.portal.url.PageUrlPartsParams;
import com.enonic.xp.portal.url.PortalScope;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ProcessHtmlPartsParams;
import com.enonic.xp.portal.url.ProcessedHtml;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.StyleDescriptorService;
import com.enonic.xp.style.StyleDescriptors;
import com.enonic.xp.util.GenericValue;

/**
 * Processes rich text for {@link PortalUrlService#processHtmlParts}: resolves every internal link from configuration,
 * and every macro among the applications, for the site or project the HTML belongs to, and writes a placeholder for each
 * one.
 */
final class RichTextPartsProcessor
{
    private static final Logger LOG = LoggerFactory.getLogger( RichTextPartsProcessor.class );

    private final StyleDescriptorService styleDescriptorService;

    private final PortalUrlService portalUrlService;

    private final MacroService macroService;

    private final MacroDescriptorResolver macroDescriptorResolver;

    private final ContentService contentService;

    private final ProcessHtmlPartsParams params;

    private final PortalScope scope;

    private final Supplier<ImageStyles> imageStyles;

    private final List<ProcessedHtml.Link> links = new ArrayList<>();

    private final List<ProcessedHtml.Image> images = new ArrayList<>();

    private final List<ProcessedHtml.Macro> macros = new ArrayList<>();

    RichTextPartsProcessor( final StyleDescriptorService styleDescriptorService, final PortalUrlService portalUrlService,
                            final MacroService macroService, final MacroDescriptorResolver macroDescriptorResolver,
                            final ContentService contentService, final ProcessHtmlPartsParams params,
                            final PortalScope scope )
    {
        this.styleDescriptorService = styleDescriptorService;
        this.portalUrlService = portalUrlService;
        this.macroService = macroService;
        this.macroDescriptorResolver = macroDescriptorResolver;
        this.contentService = contentService;
        this.params = params;
        this.scope = scope;
        this.imageStyles = Suppliers.memoize( () -> RichTextLinks.imageStyles( styleDescriptors() ) );
    }

    ProcessedHtml process()
    {
        final String baseUrl = scope.baseUrl();

        if ( params.getValue() == null )
        {
            return new ProcessedHtml( "", baseUrl, List.of(), List.of(), List.of() );
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

        final String safeHtml = HtmlMacroProcessor.withoutInstructions( processed );
        final String html = params.isProcessMacros() ? macroService.evaluateMacros( safeHtml, this::processMacro ) : safeHtml;

        return new ProcessedHtml( html, baseUrl, links, images, macros );
    }

    private String processMacro( final Macro macro )
    {
        final MacroDescriptor descriptor = macroDescriptorResolver.resolve( scope.applications(), macro.getName() );
        if ( descriptor == null )
        {
            return macro.toString();
        }

        final String ref = UUID.randomUUID().toString();
        final String body = Objects.requireNonNullElse( macro.getBody(), "" );
        macros.add( new ProcessedHtml.Macro( ref, descriptor.getKey(), macroConfig( macro, descriptor ), body ) );

        return "<" + ProcessedHtml.MACRO_ELEMENT + " " + ProcessedHtml.MACRO_NAME_ATTRIBUTE + "=\"" + descriptor.getName() + "\" " +
            ProcessedHtml.MACRO_REF_ATTRIBUTE + "=\"" + ref + "\">" + body + "</" +
            ProcessedHtml.MACRO_ELEMENT + ">";
    }

    private static GenericValue macroConfig( final Macro macro, final MacroDescriptor descriptor )
    {
        final MacroParamNames paramNames = new MacroParamNames( descriptor );
        final Map<String, List<String>> macroParams = new LinkedHashMap<>();
        macro.getParameters().forEach( ( name, value ) -> {
            if ( value != null )
            {
                macroParams.computeIfAbsent( paramNames.of( name ), key -> new ArrayList<>() ).add( value );
            }
        } );

        final GenericValue.ObjectBuilder config = GenericValue.newObject();
        macroParams.forEach( ( name, values ) -> {
            if ( paramNames.isMultiple( name ) )
            {
                config.put( name, values.stream().map( GenericValue::stringValue ).collect( GenericValue.listCollector() ) );
            }
            else
            {
                config.put( name, values.getFirst() );
            }
        } );
        return config.build();
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

        try
        {
            processLink( element, link, ref, properties );
        }
        catch ( RuntimeException e )
        {
            unresolved( element, link, ref, e );
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
                final ProcessedHtml.Style style = image( element, ref, link );
                if ( style != null )
                {
                    properties.put( "style:application", style.application().toString() );
                    properties.put( "style:name", style.name() );
                    properties.put( "style:aspectRatio", style.aspectRatio() );
                    properties.put( "style:filter", style.filter() );
                }
            }
            default -> throw new IllegalStateException( "Unknown type " + link.type() );
        }
    }

    /**
     * Writes a link that does not resolve as a URL answered with 404, with a ref to an entry without parts.
     */
    private void unresolved( final HtmlElement element, final RichTextLinks.Link link, final String ref, final RuntimeException e )
    {
        if ( !RichTextLinks.CONTENT_TYPE.equals( link.type() ) )
        {
            LOG.warn( "Link [{}] does not resolve", link.uri(), e );
        }

        switch ( link.type() )
        {
            case RichTextLinks.CONTENT_TYPE ->
            {
                element.setAttribute( ProcessedHtml.LINK_REF_ATTRIBUTE, ref );
                element.setAttribute( link.attribute(), UrlGenerator.notFoundUrl( e ) );
                links.add( new ProcessedHtml.ContentLink( ref, link.uri(), link.id(), null, fragment( link ) ) );
            }
            case RichTextLinks.MEDIA_TYPE ->
            {
                final boolean download = RichTextLinks.DOWNLOAD_MODE.equals( link.mode() );
                element.setAttribute( ProcessedHtml.LINK_REF_ATTRIBUTE, ref );
                element.setAttribute( link.attribute(),
                                      MediaPathParts.unresolved( link.id(), null, null )
                                          .path( PortalUrlGeneratorServiceImpl.MEDIA_ATTACHMENT_API_DESCRIPTOR_KEY ) +
                                          ( download ? "?download" : "" ) );
                links.add( new ProcessedHtml.AttachmentLink( ref, link.uri(), link.id(), null, download ) );
            }
            case RichTextLinks.IMAGE_TYPE ->
            {
                element.setAttribute( ProcessedHtml.IMAGE_REF_ATTRIBUTE, ref );
                element.setAttribute( link.attribute(), MediaPathParts.unresolved( link.id(), unresolvedScale( link ), null )
                    .path( PortalUrlGeneratorServiceImpl.MEDIA_IMAGE_API_DESCRIPTOR_KEY ) );
                images.add( new ProcessedHtml.Image( ref, link.id(), null, null, List.of() ) );
            }
            default -> throw new IllegalStateException( "Unknown type " + link.type() );
        }
    }

    private String unresolvedScale( final RichTextLinks.Link link )
    {
        try
        {
            final String styleName = link.decodedParam( "style" );
            final ImageStyles.Resolved resolved = styleName == null ? null : imageStyles.get().get( styleName );
            final ImageStyle style = resolved == null ? null : resolved.style();
            return ImageMediaPathSupplier.resolveScale(
                DefaultImageLinkProcessor.scale( style, link.decodedParam( "scale" ), params.getImageSrcWidth() ) );
        }
        catch ( RuntimeException e )
        {
            return ImageMediaPathSupplier.FULL_SCALE;
        }
    }

    /**
     * @return the fragment of the link prefixed with {@code #}, or empty when it has none
     */
    private static String fragment( final RichTextLinks.Link link )
    {
        try
        {
            final String fragment = RichTextLinks.validQueryOrFragment( link.urlParams().get( "fragment" ) );
            return fragment == null ? "" : "#" + fragment;
        }
        catch ( RuntimeException e )
        {
            return "";
        }
    }

    private String contentLink( final String ref, final RichTextLinks.Link link )
    {
        final PageUrlParts parts =
            portalUrlService.pageUrlParts( PageUrlPartsParams.create().setId( link.id() ).setScope( scope ).build() );

        final Map<String, String> urlParams = link.urlParams();
        final String query = RichTextLinks.validQueryOrFragment( urlParams.get( "query" ) );
        final String fragment = fragment( link );
        final String queryString = query == null ? "" : "?" + query;

        links.add( new ProcessedHtml.ContentLink( ref, link.uri(), link.id(),
                                                  new PageUrlParts( parts.baseUrl(), parts.path(), queryString ), fragment ) );

        // the level itself has an empty relative path: its root is linked instead of the document
        final String path = parts.path().isEmpty() ? "/" : parts.path();
        return path + queryString + fragment;
    }

    private String attachmentLink( final String ref, final RichTextLinks.Link link )
    {
        final boolean download = RichTextLinks.DOWNLOAD_MODE.equals( link.mode() );
        final AttachmentUrlParts parts = portalUrlService.attachmentUrlParts(
            AttachmentUrlPartsParams.create()
                .setId( link.id() )
                .setProjectName( scope::projectName )
                .setBranch( scope::branch )
                .setDownload( download )
                .build() );

        links.add( new ProcessedHtml.AttachmentLink( ref, link.uri(), link.id(), parts, download ) );

        return parts.path() + parts.queryString();
    }

    /**
     * @return the style applied, with its application, or {@code null} for none
     */
    private ProcessedHtml.Style image( final HtmlElement element, final String ref, final RichTextLinks.Link link )
    {
        final String id = link.id();
        final String styleName = link.decodedParam( "style" );
        final ImageStyles.Resolved resolved = styleName == null ? null : imageStyles.get().get( styleName );
        final ImageStyle style = resolved == null ? null : resolved.style();
        final String scaleFromQueryString = link.decodedParam( "scale" );

        // looked up once for the src and every srcset width
        final Supplier<Media> media =
            Suppliers.memoize( () -> MediaLookup.media( contentService, scope.projectName(), scope.branch(), id ) );

        final ImageUrlParts src =
            imageParts( media, style, DefaultImageLinkProcessor.scale( style, scaleFromQueryString, params.getImageSrcWidth() ) );

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
        // sizes goes with the width descriptors of a srcset, so both are written, or neither
        if ( !srcset.isEmpty() )
        {
            element.setAttribute( "srcset", srcset.stream()
                .map( source -> source.url().path() + source.url().queryString() + " " + source.width() + "w" )
                .collect( Collectors.joining( "," ) ) );

            if ( params.getImageSizes() != null && !params.getImageSizes().isBlank() )
            {
                element.setAttribute( "sizes", params.getImageSizes() );
            }
        }

        final ProcessedHtml.Style processedStyle = resolved == null
            ? null
            : new ProcessedHtml.Style( resolved.application(), style.getName(), style.getAspectRatio(), style.getFilter() );

        images.add( new ProcessedHtml.Image( ref, id, processedStyle, src, srcset ) );

        return processedStyle;
    }

    private ImageUrlParts imageParts( final Supplier<Media> media, final ImageStyle style, final String scale )
    {
        return portalUrlService.imageUrlParts( ImageUrlPartsParams.create()
                                                   .setMedia( media )
                                                   .setProjectName( scope::projectName )
                                                   .setBranch( scope::branch )
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
            : RichTextLinks.styleDescriptors( styleDescriptorService, scope.applications() );
    }
}
