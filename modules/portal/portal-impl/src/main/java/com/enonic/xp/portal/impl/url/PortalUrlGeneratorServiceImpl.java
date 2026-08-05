package com.enonic.xp.portal.impl.url;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Strings;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.portal.url.ApiUrlGeneratorParams;
import com.enonic.xp.portal.url.AttachmentUrlGeneratorParams;
import com.enonic.xp.portal.url.AttachmentUrlParts;
import com.enonic.xp.portal.url.ImageUrlGeneratorParams;
import com.enonic.xp.portal.url.ImageUrlParts;
import com.enonic.xp.portal.url.PortalUrlGeneratorService;
import com.enonic.xp.portal.url.UrlGeneratorParams;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.webapp.WebappService;

@Component(immediate = true, configurationPid = "com.enonic.xp.portal")
public class PortalUrlGeneratorServiceImpl
    implements PortalUrlGeneratorService
{
    private static final DescriptorKey MEDIA_IMAGE_API_DESCRIPTOR_KEY = DescriptorKey.from( ApplicationKey.from( "media" ), "image" );

    private static final DescriptorKey MEDIA_ATTACHMENT_API_DESCRIPTOR_KEY =
        DescriptorKey.from( ApplicationKey.from( "media" ), "attachment" );

    private final WebappService webappService;

    private final SiteService siteService;

    private volatile String defaultMediaBaseUrl;

    private volatile boolean mediaApiAutoMount = true;

    @Activate
    public PortalUrlGeneratorServiceImpl( @Reference final WebappService webappService, @Reference final SiteService siteService )
    {
        this.webappService = webappService;
        this.siteService = siteService;
    }

    @Activate
    @Modified
    public void activate( final PortalConfig config )
    {
        this.defaultMediaBaseUrl = Strings.emptyToNull( config.media_defaultBaseUrl() );
        this.mediaApiAutoMount = config.legacy_mediaApiAutoMount_enabled();
    }

    @Override
    public String imageUrl( final ImageUrlGeneratorParams params )
    {
        final ApiUrlGeneratorParams.Builder builder = ApiUrlGeneratorParams.create()
            .setUrlType( params.getUrlType() )
            .setDescriptorKey( MEDIA_IMAGE_API_DESCRIPTOR_KEY )
            .setPath( ImageMediaPathSupplier.create()
                          .setMedia( params.getMedia() )
                          .setProjectName( params.getProjectName() )
                          .setBranch( params.getBranch() )
                          .setScale( params.getScale() )
                          .setFormat( params.getFormat() )
                          .build() );

        builder.setQueryParams( imageQueryParams( params ) );

        final String mediaBaseUrl = resolveMediaBaseUrl( params.getMediaBaseUrl(), params.getBaseUrl() );
        return mediaBaseUrl != null ? generateMediaUrl( mediaBaseUrl, builder.build() ) : apiUrl( builder.build() );
    }

    @Override
    public String attachmentUrl( final AttachmentUrlGeneratorParams params )
    {
        final AttachmentMediaPathSupplier pathStrategy = AttachmentMediaPathSupplier.create()
            .setContent( params.getContentSupplier() )
            .setProjectName( params.getProjectName() )
            .setBranch( params.getBranch() )
            .setName( params.getName() )
            .setLabel( params.getLabel() )
            .build();

        final ApiUrlGeneratorParams.Builder builder = ApiUrlGeneratorParams.create()
            .setUrlType( params.getUrlType() )
            .setDescriptorKey( MEDIA_ATTACHMENT_API_DESCRIPTOR_KEY )
            .setPath( pathStrategy )
            .setQueryParams( params.getQueryParams() );

        if ( params.isDownload() )
        {
            builder.setQueryParams( Map.of( "download", List.of() ) );
        }

        final String mediaBaseUrl = resolveMediaBaseUrl( params.getMediaBaseUrl(), params.getBaseUrl() );
        return mediaBaseUrl != null ? generateMediaUrl( mediaBaseUrl, builder.build() ) : apiUrl( builder.build() );
    }

    @Override
    public ImageUrlParts imageUrlParts( final ImageUrlGeneratorParams params )
    {
        return runWithAdminRole( () -> {
            final MediaPathParts parts = ImageMediaPathSupplier.create()
                .setMedia( params.getMedia() )
                .setProjectName( params.getProjectName() )
                .setBranch( params.getBranch() )
                .setScale( params.getScale() )
                .setFormat( params.getFormat() )
                .build()
                .parts();

            return new ImageUrlParts( mediaPath( MEDIA_IMAGE_API_DESCRIPTOR_KEY, parts ), queryString( imageQueryParams( params ) ),
                                      UrlBuilderHelper.urlEncodePathSegment( parts.context() ), parts.id(), parts.hash(),
                                      UrlBuilderHelper.urlEncodePathSegment( parts.scale() ),
                                      UrlBuilderHelper.urlEncodePathSegment( parts.name() ) );
        } );
    }

    @Override
    public AttachmentUrlParts attachmentUrlParts( final AttachmentUrlGeneratorParams params )
    {
        return runWithAdminRole( () -> {
            final MediaPathParts parts = AttachmentMediaPathSupplier.create()
                .setContent( params.getContentSupplier() )
                .setProjectName( params.getProjectName() )
                .setBranch( params.getBranch() )
                .setName( params.getName() )
                .setLabel( params.getLabel() )
                .build()
                .parts();

            return new AttachmentUrlParts( mediaPath( MEDIA_ATTACHMENT_API_DESCRIPTOR_KEY, parts ),
                                           queryString( attachmentQueryParams( params ) ),
                                           UrlBuilderHelper.urlEncodePathSegment( parts.context() ), parts.id(), parts.hash(),
                                           UrlBuilderHelper.urlEncodePathSegment( parts.name() ) );
        } );
    }

    private static Map<String, List<String>> imageQueryParams( final ImageUrlGeneratorParams params )
    {
        final Map<String, List<String>> queryParams = new LinkedHashMap<>( params.getQueryParams() );

        if ( params.getQuality() != null )
        {
            queryParams.put( "quality", List.of( params.getQuality().toString() ) );
        }
        if ( params.getBackground() != null )
        {
            queryParams.put( "background", List.of( params.getBackground() ) );
        }
        if ( params.getFilter() != null )
        {
            queryParams.put( "filter", List.of( params.getFilter() ) );
        }

        return queryParams;
    }

    private static Map<String, List<String>> attachmentQueryParams( final AttachmentUrlGeneratorParams params )
    {
        if ( params.isDownload() )
        {
            return Map.of( "download", List.of() );
        }
        return params.getQueryParams();
    }

    private static String queryString( final Map<String, List<String>> queryParams )
    {
        final DefaultQueryParamsSupplier queryParamsStrategy = new DefaultQueryParamsSupplier();
        queryParamsStrategy.params( queryParams );
        return queryParamsStrategy.get();
    }

    private static String mediaPath( final DescriptorKey descriptorKey, final MediaPathParts parts )
    {
        final StringBuilder path = new StringBuilder();
        UrlBuilderHelper.appendPart( path, descriptorKey.toString() );
        UrlBuilderHelper.appendPart( path, parts.context() );
        UrlBuilderHelper.appendPart( path, parts.idWithHash() );
        UrlBuilderHelper.appendPart( path, parts.scale() );
        UrlBuilderHelper.appendPart( path, parts.name() );
        return path.toString();
    }

    static String resolveMediaBaseUrl( final String mediaBaseUrl, final String baseUrl )
    {
        if ( mediaBaseUrl != null )
        {
            return mediaBaseUrl;
        }
        if ( baseUrl == null )
        {
            return null;
        }
        // baseUrl points at a mount: media APIs live under its "_" endpoint segment
        final StringBuilder url = new StringBuilder( baseUrl );
        UrlBuilderHelper.appendPart( url, "_" );
        return url.toString();
    }

    private String generateMediaUrl( final String mediaBaseUrl, final ApiUrlGeneratorParams params )
    {
        final DefaultQueryParamsSupplier queryParamsStrategy = new DefaultQueryParamsSupplier();
        queryParamsStrategy.params( params.getQueryParams() );

        // mediaBaseUrl points directly at the API root: no "_" endpoint segment is added
        return generateUrl( UrlGeneratorParams.create()
                                .setBaseUrl( () -> {
                                    final StringBuilder url = new StringBuilder( mediaBaseUrl );
                                    UrlBuilderHelper.appendPart( url, params.getDescriptorKey().toString() );
                                    return url.toString();
                                } )
                                .setPath( params.getPath() )
                                .setQueryString( queryParamsStrategy )
                                .build() );
    }


    @Override
    public String apiUrl( final ApiUrlGeneratorParams params )
    {
        final DefaultQueryParamsSupplier queryParamsStrategy = new DefaultQueryParamsSupplier();
        queryParamsStrategy.params( params.getQueryParams() );

        final UrlGeneratorParams generatorParams = UrlGeneratorParams.create()
            .setBaseUrl( ApiUrlBaseUrlResolver.create()
                             .setBaseUrl( params.getBaseUrl() )
                             .setDescriptorKey( params.getDescriptorKey() )
                             .setUrlType( params.getUrlType() )
                             .setDefaultMediaBaseUrl( defaultMediaBaseUrl )
                             .setMediaApiAutoMount( mediaApiAutoMount )
                             .setWebappService( webappService )
                             .setSiteService( siteService )
                             .build() )
            .setPath( params.getPath() )
            .setQueryString( queryParamsStrategy )
            .build();

        return generateUrl( generatorParams );
    }

    @Override
    public String generateUrl( final UrlGeneratorParams params )
    {
        return runWithAdminRole( () -> UrlGenerator.generateUrl( params ) );
    }

    private <T> T runWithAdminRole( final Callable<T> callable )
    {
        final Context context = ContextAccessor.current();
        final AuthenticationInfo authenticationInfo =
            AuthenticationInfo.copyOf( context.getAuthInfo() ).principals( RoleKeys.ADMIN ).build();
        return ContextBuilder.from( context ).authInfo( authenticationInfo ).build().callWith( callable );
    }
}
