package com.enonic.xp.portal.impl.url;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.macro.MacroService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.html.HtmlDocument;
import com.enonic.xp.portal.html.HtmlElement;
import com.enonic.xp.portal.impl.html.HtmlParser;
import com.enonic.xp.portal.url.ApiUrlGeneratorParams;
import com.enonic.xp.portal.url.AttachmentUrlGeneratorParams;
import com.enonic.xp.portal.url.AttachmentUrlParams;
import com.enonic.xp.portal.url.AttachmentUrlParts;
import com.enonic.xp.portal.url.HtmlElementPostProcessor;
import com.enonic.xp.portal.url.HtmlProcessorParams;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.PageUrlParts;
import com.enonic.xp.portal.url.PortalUrlGeneratorService;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ProcessHtmlParams;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.site.SiteConfigsDataSerializer;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.StyleDescriptorService;
import com.enonic.xp.style.StyleDescriptors;

import static com.google.common.base.Strings.nullToEmpty;

public class RichTextProcessor
{
    private static final ApplicationKey SYSTEM_APPLICATION_KEY = ApplicationKey.from( "com.enonic.xp.app.system" );

    private static final DescriptorKey MEDIA_IMAGE_API_DESCRIPTOR_KEY = DescriptorKey.from( ApplicationKey.from( "media" ), "image" );

    private static final int[] QUERY_OR_FRAGMENT_ALLOWED_CHARACTERS =
        "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ?/:@-._~!$&'()*+,;=%".chars().sorted().toArray();

    /*
     * Pattern Constants
     */

    private static final int TYPE_INDEX = 1;

    private static final int MODE_INDEX = TYPE_INDEX + 1;

    private static final int ID_INDEX = MODE_INDEX + 1;

    private static final int PARAMS_INDEX = ID_INDEX + 1;

    private static final int NB_GROUPS = ID_INDEX;

    private static final String CONTENT_TYPE = "content";

    private static final String MEDIA_TYPE = "media";

    private static final String IMAGE_TYPE = "image";

    private static final String DOWNLOAD_MODE = "download";

    private static final String INLINE_MODE = "inline";

    /*
     * Parameters Keys
     */

    private static final String SCALE_PARAM = "scale";

    private static final String STYLE_PARAM = "style";

    private static final Pattern PATTERN = Pattern.compile(
        "(" + CONTENT_TYPE + "|" + MEDIA_TYPE + "|" + IMAGE_TYPE + ")://(?:(" + DOWNLOAD_MODE + "|" + INLINE_MODE +
            ")/)?([0-9a-z-/]+)(\\?[^\"]+)?" );

    private final StyleDescriptorService styleDescriptorService;

    private final PortalUrlService portalUrlService;

    private final PortalUrlGeneratorService portalUrlGeneratorService;

    private final ContentService contentService;

    private final MacroService macroService;

    private final String defaultMediaBaseUrl;

    private Supplier<Map<String, ImageStyle>> imageStylesSupplier;

    private Supplier<String> imageBaseUrlSupplier;

    public RichTextProcessor( final StyleDescriptorService styleDescriptorService, final PortalUrlService portalUrlService,
                              final PortalUrlGeneratorService portalUrlGeneratorService, final MacroService macroService,
                              final ContentService contentService, final String defaultMediaBaseUrl )
    {
        this.styleDescriptorService = styleDescriptorService;
        this.portalUrlService = portalUrlService;
        this.portalUrlGeneratorService = portalUrlGeneratorService;
        this.macroService = macroService;
        this.contentService = contentService;
        this.defaultMediaBaseUrl = defaultMediaBaseUrl;
    }

    private void defaultElementProcessing( HtmlElement element, ProcessHtmlParams params, HtmlElementPostProcessor postProcessor )
    {
        final Matcher contentMatcher = PATTERN.matcher( getLinkValue( element ) );

        if ( contentMatcher.find() && contentMatcher.groupCount() >= NB_GROUPS )
        {
            final String type = contentMatcher.group( TYPE_INDEX );
            final String mode = contentMatcher.group( MODE_INDEX );
            final String id = contentMatcher.group( ID_INDEX );
            final String urlParamsString = contentMatcher.groupCount() == PARAMS_INDEX ? contentMatcher.group( PARAMS_INDEX ) : null;

            switch ( type )
            {
                case CONTENT_TYPE:
                {
                    defaultLinkProcessingForContent( element, params, id, urlParamsString, postProcessor );
                    break;
                }
                case IMAGE_TYPE:
                {
                    defaultImageProcessing( element, params, id, urlParamsString, postProcessor );
                    break;
                }
                case MEDIA_TYPE:
                {
                    defaultAttachmentProcessing( element, params, id, mode, urlParamsString, postProcessor );
                    break;
                }
                default:
                {
                    throw new IllegalStateException( "Unknown type " + type );
                }
            }
        }
    }

    private void defaultProcessing( HtmlDocument document, ProcessHtmlParams params, HtmlElementPostProcessor postProcessor )
    {
        document.select( "[href],[src]" ).forEach( element -> defaultElementProcessing( element, params, postProcessor ) );
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
            return getImageStyleMap( styleDescriptors );
        } );

        this.imageBaseUrlSupplier = Suppliers.memoize( () -> {
            if ( params.getPageBase() != null )
            {
                // configuration alone: the given image base, the default media base, or the bare
                // media API path when neither is set - never a site mount or the request
                return mediaApiRoot( params.getImageBaseUrl(), MEDIA_IMAGE_API_DESCRIPTOR_KEY );
            }

            final String imageBaseUrl =
                PortalUrlGeneratorServiceImpl.resolveMediaBaseUrl( params.getImageBaseUrl(), params.getBaseUrl() );
            if ( imageBaseUrl != null )
            {
                // the image base points directly at the API root: no "_" endpoint segment is added
                final StringBuilder url = new StringBuilder( imageBaseUrl );
                UrlBuilderHelper.appendPart( url, MEDIA_IMAGE_API_DESCRIPTOR_KEY.toString() );
                return url.toString();
            }
            final ApiUrlGeneratorParams apiParams = ApiUrlGeneratorParams.create()
                .setUrlType( params.getType() )
                .setDescriptorKey( MEDIA_IMAGE_API_DESCRIPTOR_KEY )
                .build();
            return portalUrlGeneratorService.apiUrl( apiParams );
        } );

        final HtmlDocument document = HtmlParser.parse( params.getValue() );
        if ( params.getCustomHtmlProcessor() == null )
        {
            defaultProcessing( document, params, null );
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

    private void defaultLinkProcessingForContent( HtmlElement element, ProcessHtmlParams params, String id, String urlParamsString,
                                                  HtmlElementPostProcessor postProcessor )
    {
        final String originalUri = element.getAttribute( getLinkAttribute( element ) );

        final String rawPageUrl = params.getPageBase() != null
            ? configuredPageUrl( new PageUrlParams().id( id ).base( params.getPageBase() ) )
            : portalUrlService.pageUrl( new PageUrlParams().type( params.getType() ).id( id ) );

        final String pageUrl = addQueryParamsIfPresent( rawPageUrl, urlParamsString );

        element.setAttribute( getLinkAttribute( element ), pageUrl );

        if ( postProcessor != null )
        {
            Map<String, String> properties = new HashMap<>();

            properties.put( "type", params.getType() );
            properties.put( "contentId", id );
            properties.put( "uri", originalUri );
            properties.put( "queryParams", urlParamsString );

            postProcessor.process( element, properties );
        }
    }

    private void defaultImageProcessing( HtmlElement element, ProcessHtmlParams params, String id, String urlParamsString,
                                         HtmlElementPostProcessor callback )
    {
        final Map<String, String> urlParams = extractUrlParams( urlParamsString );

        final ImageStyle imageStyle = getImageStyle( imageStylesSupplier.get(), urlParams );

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
            properties.put( "queryParams", urlParamsString );
            if ( imageStyle != null )
            {
                properties.put( "style:name", imageStyle.getName() );
                properties.put( "style:aspectRatio", imageStyle.getAspectRatio() );
                properties.put( "style:filter", imageStyle.getFilter() );
            }

            callback.process( element, properties );
        }
    }

    private void defaultAttachmentProcessing( HtmlElement element, ProcessHtmlParams params, String id, String mode, String urlParamsString,
                                              HtmlElementPostProcessor callback )
    {
        final String originalUri = element.getAttribute( getLinkAttribute( element ) );

        final String attachmentUrl = params.getPageBase() != null
            ? configuredAttachmentUrl( params, id, DOWNLOAD_MODE.equals( mode ) )
            : portalUrlService.attachmentUrl( new AttachmentUrlParams().baseUrl( params.getBaseUrl() )
                                                  .mediaBaseUrl( params.getAttachmentBaseUrl() )
                                                  .type( params.getType() )
                                                  .id( id )
                                                  .download( DOWNLOAD_MODE.equals( mode ) ) );

        element.setAttribute( getLinkAttribute( element ), attachmentUrl );

        if ( callback != null )
        {
            Map<String, String> properties = new HashMap<>();

            properties.put( "type", params.getType() );
            properties.put( "contentId", id );
            properties.put( "uri", originalUri );
            properties.put( "mode", mode );
            properties.put( "queryParams", urlParamsString );

            callback.process( element, properties );
        }
    }


    /**
     * @return the configured Base URL of the selected level followed by the content path relative to it, or that path
     * alone when no Base URL is configured - the root of the level when the content is that level itself
     */
    private String configuredPageUrl( final PageUrlParams pageUrlParams )
    {
        final PageUrlParts parts = portalUrlService.pageUrlParts( pageUrlParams );
        final String url = nullToEmpty( parts.baseUrl() ) + parts.path();
        return url.isEmpty() ? "/" : url;
    }

    /**
     * @return the attachment URL from configuration alone: the given attachment base, the default media base, or the
     * bare media API path when neither is set
     */
    private String configuredAttachmentUrl( final ProcessHtmlParams params, final String id, final boolean download )
    {
        // project and branch come from the context, never from a site request
        final ProjectName projectName = ContentProjectResolver.create().setPreferSiteRequest( false ).build().resolve();
        final Branch branch = ContentBranchResolver.create().setPreferSiteRequest( false ).build().resolve();

        final Supplier<Content> content = () -> ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( projectName.getRepoId() )
            .branch( branch )
            .build()
            .callWith( () -> contentService.getById( ContentId.from( id ) ) );

        final AttachmentUrlParts parts = portalUrlGeneratorService.attachmentUrlParts( AttachmentUrlGeneratorParams.create()
                                                                                          .setContent( content )
                                                                                          .setProjectName( () -> projectName )
                                                                                          .setBranch( () -> branch )
                                                                                          .setDownload( download )
                                                                                          .build() );

        return configuredMediaBaseUrl( params.getAttachmentBaseUrl() ) + parts.path() + parts.queryString();
    }

    /**
     * @return the root of a media API from configuration alone: the given base, the default media base, or nothing when
     * neither is set - so the media API path follows directly, such as {@code /media:image}
     */
    private String mediaApiRoot( final String givenBaseUrl, final DescriptorKey api )
    {
        final StringBuilder url = new StringBuilder( configuredMediaBaseUrl( givenBaseUrl ) );
        UrlBuilderHelper.appendPart( url, api.toString() );
        return url.toString();
    }

    /**
     * @return the given base, the default media base, or an empty string when neither is set - never a site mount
     */
    private String configuredMediaBaseUrl( final String givenBaseUrl )
    {
        final String base = givenBaseUrl != null ? givenBaseUrl : defaultMediaBaseUrl;
        return base == null ? "" : UrlGenerator.removeTrailingSlash( base );
    }

    private String getLinkValue( final HtmlElement element )
    {
        return element.hasAttribute( "href" ) ? element.getAttribute( "href" ) : element.getAttribute( "src" );
    }

    private String getLinkAttribute( final HtmlElement element )
    {
        return element.hasAttribute( "href" ) ? "href" : "src";
    }

    private Map<String, ImageStyle> getImageStyleMap( final StyleDescriptors styleDescriptors )
    {
        return styleDescriptors.stream()
            .flatMap( styleDescriptor -> styleDescriptor.getElements().stream() )
            .filter( element -> element instanceof ImageStyle )
            .map( ImageStyle.class::cast )
            .collect(
                Collectors.toUnmodifiableMap( ImageStyle::getName, elementStyle -> elementStyle, ( existingKey, newKey ) -> existingKey ) );
    }

    private StyleDescriptors getStyleDescriptors()
    {
        final List<ApplicationKey> appKeys = new ArrayList<>();
        appKeys.add( SYSTEM_APPLICATION_KEY );

        final PortalRequest portalRequest = PortalRequestAccessor.get();
        if ( portalRequest != null && portalRequest.getSite() != null )
        {
            SiteConfigsDataSerializer.fromData( portalRequest.getSite().getData().getRoot() )
                .forEach( siteConfig -> appKeys.add( siteConfig.getApplicationKey() ) );
        }
        return styleDescriptorService.getByApplications( ApplicationKeys.from( appKeys ) );
    }

    private ImageStyle getImageStyle( final Map<String, ImageStyle> imageStyleMap, final Map<String, String> urlParams )
    {
        final String styleString = urlParams.get( STYLE_PARAM );
        return styleString != null ? imageStyleMap.get( styleString ) : null;
    }

    private Map<String, String> extractUrlParams( final String urlQuery )
    {
        if ( urlQuery == null )
        {
            return Collections.emptyMap();
        }
        final String query = urlQuery.startsWith( "?" ) ? urlQuery.substring( 1 ) : urlQuery;
        return Splitter.on( '&' ).trimResults().withKeyValueSeparator( "=" ).split( query.replace( "&amp;", "&" ) );
    }

    private String addQueryParamsIfPresent( final String url, final String urlQuery )
    {
        if ( urlQuery == null )
        {
            return url;
        }
        final StringBuilder urlSuffix = new StringBuilder();
        final Map<String, String> queryParamsAsMap = extractUrlParams( urlQuery );

        addComponentToUrlIfValid( queryParamsAsMap.get( "query" ), "?", urlSuffix );
        addComponentToUrlIfValid( queryParamsAsMap.get( "fragment" ), "#", urlSuffix );

        return url + urlSuffix;
    }

    private void addComponentToUrlIfValid( final String value, final String mark, final StringBuilder builder )
    {
        if ( value == null )
        {
            return;
        }
        final String decodedValue = URLDecoder.decode( value, StandardCharsets.UTF_8 );
        if ( decodedValue.chars().allMatch( ch -> Arrays.binarySearch( QUERY_OR_FRAGMENT_ALLOWED_CHARACTERS, ch ) >= 0 ) )
        {
            builder.append( mark ).append( decodedValue );
        }
    }
}
