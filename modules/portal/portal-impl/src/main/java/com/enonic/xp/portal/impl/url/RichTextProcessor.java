package com.enonic.xp.portal.impl.url;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.macro.MacroService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.html.HtmlDocument;
import com.enonic.xp.portal.html.HtmlElement;
import com.enonic.xp.portal.impl.html.HtmlParser;
import com.enonic.xp.portal.url.ApiUrlGeneratorParams;
import com.enonic.xp.portal.url.AttachmentUrlParams;
import com.enonic.xp.portal.url.HtmlElementPostProcessor;
import com.enonic.xp.portal.url.HtmlProcessorParams;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.PortalUrlGeneratorService;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ProcessHtmlParams;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.StyleDescriptorService;
import com.enonic.xp.style.StyleDescriptors;

public class RichTextProcessor
{
    private static final DescriptorKey MEDIA_IMAGE_API_DESCRIPTOR_KEY = DescriptorKey.from( ApplicationKey.from( "media" ), "image" );

    private static final String SCALE_PARAM = "scale";

    private static final String STYLE_PARAM = "style";

    private final StyleDescriptorService styleDescriptorService;

    private final PortalUrlService portalUrlService;

    private final PortalUrlGeneratorService portalUrlGeneratorService;

    private final ContentService contentService;

    private final MacroService macroService;

    private Supplier<ImageStyles> imageStylesSupplier;

    private Supplier<String> imageBaseUrlSupplier;

    public RichTextProcessor( final StyleDescriptorService styleDescriptorService, final PortalUrlService portalUrlService,
                              final PortalUrlGeneratorService portalUrlGeneratorService, final MacroService macroService,
                              final ContentService contentService )
    {
        this.styleDescriptorService = styleDescriptorService;
        this.portalUrlService = portalUrlService;
        this.portalUrlGeneratorService = portalUrlGeneratorService;
        this.macroService = macroService;
        this.contentService = contentService;
    }

    private void defaultElementProcessing( HtmlElement element, ProcessHtmlParams params, HtmlElementPostProcessor postProcessor )
    {
        final RichTextLinks.Link link = RichTextLinks.find( element );

        if ( link == null )
        {
            return;
        }

        switch ( link.type() )
        {
            case RichTextLinks.CONTENT_TYPE -> defaultLinkProcessingForContent( element, params, link, postProcessor );
            case RichTextLinks.IMAGE_TYPE -> defaultImageProcessing( element, params, link, postProcessor );
            case RichTextLinks.MEDIA_TYPE -> defaultAttachmentProcessing( element, params, link, postProcessor );
            default -> throw new IllegalStateException( "Unknown type " + link.type() );
        }
    }

    private void defaultProcessing( HtmlDocument document, ProcessHtmlParams params, HtmlElementPostProcessor postProcessor )
    {
        document.select( "[href],[src]" ).forEach( element -> defaultElementProcessing( element, params, postProcessor ) );
        RichTextLinks.removeEmptyCaptions( document );
    }

    public String process( final ProcessHtmlParams params )
    {
        if ( params.getValue() == null || params.getValue().isEmpty() )
        {
            return "";
        }

        this.imageStylesSupplier = Suppliers.memoize( () -> {
            final StyleDescriptors styleDescriptors = params.getCustomStyleDescriptorsCallback() != null
                ? params.getCustomStyleDescriptorsCallback().get()
                : getStyleDescriptors();
            return RichTextLinks.imageStyles( styleDescriptors );
        } );

        this.imageBaseUrlSupplier = Suppliers.memoize( () -> {
            final ApiUrlGeneratorParams apiParams = ApiUrlGeneratorParams.create()
                .setUrlType( params.getType() )
                .setBaseUrl( params.getBaseUrl() )
                .setDescriptorKey( MEDIA_IMAGE_API_DESCRIPTOR_KEY )
                .build();
            return portalUrlGeneratorService.apiUrl( apiParams );
        } );

        final HtmlDocument document = HtmlParser.parse( params.getValue() );
        if ( params.getCustomHtmlProcessor() == null )
        {
            defaultProcessing( document, params, null );
            if ( !params.isProcessMacros() )
            {
                return document.getInnerHtml();
            }
        }
        else
        {
            final String html = params.getCustomHtmlProcessor()
                .apply( HtmlProcessorParams.create()
                            .htmlDocument( document )
                            .defaultProcessor( postProcessor -> defaultProcessing( document, params, postProcessor ) )
                            .defaultElementProcessor(
                                ( htmlElement, postProcessor ) -> defaultElementProcessing( htmlElement, params, postProcessor ) )
                            .build() );
            if ( !params.isProcessMacros() )
            {
                return html;
            }
        }
        return new HtmlMacroProcessor( macroService ).process( document.getInnerHtml() );
    }

    private void defaultLinkProcessingForContent( HtmlElement element, ProcessHtmlParams params, RichTextLinks.Link link,
                                                  HtmlElementPostProcessor postProcessor )
    {
        final String id = link.id();

        final PageUrlParams pageUrlParams = new PageUrlParams().type( params.getType() ).id( id );

        final String pageUrl = addQueryParamsIfPresent( portalUrlService.pageUrl( pageUrlParams ), link.urlParams() );

        element.setAttribute( link.attribute(), pageUrl );

        if ( postProcessor != null )
        {
            Map<String, String> properties = new HashMap<>();

            properties.put( "type", params.getType() );
            properties.put( "contentId", id );
            properties.put( "uri", link.uri() );
            properties.put( "queryParams", link.urlParamsString() );

            postProcessor.process( element, properties );
        }
    }

    private void defaultImageProcessing( HtmlElement element, ProcessHtmlParams params, RichTextLinks.Link link,
                                         HtmlElementPostProcessor callback )
    {
        final String id = link.id();
        final Map<String, String> urlParams = link.urlParams();

        final String styleName = urlParams.get( STYLE_PARAM );
        final ImageStyles.Resolved resolvedStyle = styleName != null ? imageStylesSupplier.get().get( styleName ) : null;
        final ImageStyle imageStyle = resolvedStyle != null ? resolvedStyle.style() : null;

        final String scaleFromQueryParams = urlParams.get( SCALE_PARAM );

        final DefaultImageLinkProcessor imageLinkProcessor = new DefaultImageLinkProcessor();

        imageLinkProcessor.contentService = contentService;
        imageLinkProcessor.portalUrlGeneratorService = portalUrlGeneratorService;
        imageLinkProcessor.baseUrlSupplier = imageBaseUrlSupplier;
        imageLinkProcessor.params = params;
        imageLinkProcessor.element = element;
        imageLinkProcessor.imageStyle = imageStyle;
        imageLinkProcessor.id = id;
        imageLinkProcessor.scaleFromQueryString = scaleFromQueryParams;
        imageLinkProcessor.process();

        if ( callback != null )
        {
            Map<String, String> properties = new HashMap<>();

            properties.put( "type", params.getType() );
            properties.put( "contentId", id );
            properties.put( "queryParams", link.urlParamsString() );
            if ( imageStyle != null )
            {
                properties.put( "style:name", imageStyle.getName() );
                properties.put( "style:aspectRatio", imageStyle.getAspectRatio() );
                properties.put( "style:filter", imageStyle.getFilter() );
            }

            callback.process( element, properties );
        }
    }

    private void defaultAttachmentProcessing( HtmlElement element, ProcessHtmlParams params, RichTextLinks.Link link,
                                              HtmlElementPostProcessor callback )
    {
        final AttachmentUrlParams attachmentUrlParams = new AttachmentUrlParams().baseUrl( params.getBaseUrl() )
            .type( params.getType() )
            .id( link.id() )
            .download( RichTextLinks.DOWNLOAD_MODE.equals( link.mode() ) );

        final String attachmentUrl = portalUrlService.attachmentUrl( attachmentUrlParams );

        element.setAttribute( link.attribute(), attachmentUrl );

        if ( callback != null )
        {
            Map<String, String> properties = new HashMap<>();

            properties.put( "type", params.getType() );
            properties.put( "contentId", link.id() );
            properties.put( "uri", link.uri() );
            properties.put( "mode", link.mode() );
            properties.put( "queryParams", link.urlParamsString() );

            callback.process( element, properties );
        }
    }

    private StyleDescriptors getStyleDescriptors()
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();
        final SiteConfigs siteConfigs = portalRequest != null && portalRequest.getSite() != null
            ? SiteConfigsDataSerializer.fromData( portalRequest.getSite().getData().getRoot() )
            : SiteConfigs.empty();
        return RichTextLinks.styleDescriptors( styleDescriptorService, siteConfigs );
    }

    private static String addQueryParamsIfPresent( final String url, final Map<String, String> urlParams )
    {
        final String query = RichTextLinks.validQueryOrFragment( urlParams.get( "query" ) );
        final String fragment = RichTextLinks.validQueryOrFragment( urlParams.get( "fragment" ) );
        return url + ( query == null ? "" : "?" + query ) + ( fragment == null ? "" : "#" + fragment );
    }
}
