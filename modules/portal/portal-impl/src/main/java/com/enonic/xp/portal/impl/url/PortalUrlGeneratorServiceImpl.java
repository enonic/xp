package com.enonic.xp.portal.impl.url;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Strings;
import com.google.common.base.Suppliers;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.impl.ImageScaling;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.portal.url.ApiUrlGeneratorParams;
import com.enonic.xp.portal.url.AttachmentUrlGeneratorParams;
import com.enonic.xp.portal.url.AttachmentUrlParts;
import com.enonic.xp.portal.url.AttachmentUrlPartsParams;
import com.enonic.xp.portal.url.ImageUrlGeneratorParams;
import com.enonic.xp.portal.url.ImageUrlParts;
import com.enonic.xp.portal.url.ImageUrlPartsParams;
import com.enonic.xp.portal.url.PortalUrlGeneratorService;
import com.enonic.xp.portal.url.UrlGeneratorParams;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.webapp.WebappService;

import static java.util.Objects.requireNonNull;

@Component(immediate = true, configurationPid = "com.enonic.xp.portal")
public class PortalUrlGeneratorServiceImpl
    implements PortalUrlGeneratorService
{
    static final DescriptorKey MEDIA_IMAGE_API_DESCRIPTOR_KEY = DescriptorKey.from( ApplicationKey.from( "media" ), "image" );

    static final DescriptorKey MEDIA_ATTACHMENT_API_DESCRIPTOR_KEY =
        DescriptorKey.from( ApplicationKey.from( "media" ), "attachment" );

    private final WebappService webappService;

    private final SiteService siteService;

    private final ContentService contentService;

    private volatile String defaultMediaBaseUrl;

    private volatile boolean mediaApiAutoMount = true;

    @Activate
    public PortalUrlGeneratorServiceImpl( @Reference final WebappService webappService, @Reference final SiteService siteService,
                                          @Reference final ContentService contentService )
    {
        this.webappService = webappService;
        this.siteService = siteService;
        this.contentService = contentService;
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
        final Supplier<Media> media =
            IdentifiedSupplier.of( IdentifiedSupplier.contentId( params.getMedia() ), Suppliers.memoize( params.getMedia()::get ) );

        final Supplier<String> path = ImageMediaPathSupplier.create()
            .setMedia( media )
            .setProjectName( params.getProjectName() )
            .setBranch( params.getBranch() )
            .setScale( params.getScale() )
            .setFormat( params.getFormat() )
            .build();

        final Supplier<String> queryString = () -> queryString(
            imageQueryParams( params.getQueryParams(), isScalable( media ), params.getQuality(), params.getBackground(),
                              params.getFilter() ) );

        return generateUrl( UrlGeneratorParams.create()
                                .setBaseUrl( apiBaseUrl( params.getUrlType(), params.getBaseUrl(), MEDIA_IMAGE_API_DESCRIPTOR_KEY ) )
                                .setPath( path )
                                .setQueryString( queryString )
                                .build() );
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
            .setQueryParams( attachmentQueryParams( params.getQueryParams(), params.isDownload() ) );

        return apiUrl( builder.setBaseUrl( params.getBaseUrl() ).build() );
    }

    @Override
    public ImageUrlParts imageUrlParts( final ImageUrlPartsParams params )
    {
        final Supplier<ProjectName> projectName = projectName( params.getProjectName() );
        final Supplier<Branch> branch = branch( params.getBranch() );
        final Supplier<Media> media = Suppliers.memoize( params.getMedia() != null
                                                             ? params.getMedia()::get
                                                             : () -> MediaLookup.media( contentService, projectName.get(), branch.get(),
                                                                                        key( params.getId(), params.getPath() ) ) );

        return runWithAdminRole( () -> {
            final MediaPathParts parts = ImageMediaPathSupplier.create()
                .setMedia( media )
                .setProjectName( projectName )
                .setBranch( branch )
                .setScale( params.getScale() )
                .setFormat( params.getFormat() )
                .build()
                .parts();

            final String queryString = queryString(
                imageQueryParams( params.getQueryParams(), ImageScaling.isScalable( media.get() ), params.getQuality(),
                                  params.getBackground(), params.getFilter() ) );

            return new ImageUrlParts( parts.path( MEDIA_IMAGE_API_DESCRIPTOR_KEY ), queryString,
                                      UrlBuilderHelper.urlEncodePathSegment( parts.context() ), parts.id(), parts.hash(),
                                      UrlBuilderHelper.urlEncodePathSegment( parts.scale() ),
                                      UrlBuilderHelper.urlEncodePathSegment( parts.name() ) );
        } );
    }

    @Override
    public AttachmentUrlParts attachmentUrlParts( final AttachmentUrlPartsParams params )
    {
        final Supplier<ProjectName> projectName = projectName( params.getProjectName() );
        final Supplier<Branch> branch = branch( params.getBranch() );
        final Supplier<Content> content = params.getContentSupplier() != null
            ? params.getContentSupplier()
            : () -> MediaLookup.content( contentService, projectName.get(), branch.get(), key( params.getId(), params.getPath() ) );

        return runWithAdminRole( () -> {
            final MediaPathParts parts = AttachmentMediaPathSupplier.create()
                .setContent( content )
                .setProjectName( projectName )
                .setBranch( branch )
                .setName( params.getName() )
                .setLabel( params.getLabel() )
                .build()
                .parts();

            return new AttachmentUrlParts( parts.path( MEDIA_ATTACHMENT_API_DESCRIPTOR_KEY ),
                                           queryString( attachmentQueryParams( params.getQueryParams(), params.isDownload() ) ),
                                           UrlBuilderHelper.urlEncodePathSegment( parts.context() ), parts.id(), parts.hash(),
                                           UrlBuilderHelper.urlEncodePathSegment( parts.name() ) );
        } );
    }

    private ApiUrlBaseUrlResolver apiBaseUrl( final String urlType, final String baseUrl, final DescriptorKey descriptorKey )
    {
        return ApiUrlBaseUrlResolver.create()
            .setBaseUrl( baseUrl )
            .setDescriptorKey( descriptorKey )
            .setUrlType( urlType )
            .setDefaultMediaBaseUrl( defaultMediaBaseUrl )
            .setMediaApiAutoMount( mediaApiAutoMount )
            .setWebappService( webappService )
            .setSiteService( siteService )
            .build();
    }

    /**
     * @return the project the params name, or else the project of the current context
     */
    private static Supplier<ProjectName> projectName( final Supplier<ProjectName> projectName )
    {
        return projectName != null
            ? projectName
            : Suppliers.memoize(
                () -> ProjectName.from( requireNonNull( ContextAccessor.current().getRepositoryId(), "Project must be provided" ) ) );
    }

    /**
     * @return the branch the params name, or else the branch of the current context
     */
    private static Supplier<Branch> branch( final Supplier<Branch> branch )
    {
        return branch != null ? branch : Suppliers.memoize( () -> requireNonNull( ContextAccessor.current().getBranch(), "Branch must be provided" ) );
    }

    private static String key( final String id, final String path )
    {
        return id != null ? id : path;
    }

    /**
     * @return the query params of an image URL; an image served as stored takes none of the processing params
     */
    private static Map<String, List<String>> imageQueryParams( final Map<String, List<String>> params, final boolean scalable,
                                                               final Integer quality, final String background, final String filter )
    {
        final Map<String, List<String>> queryParams = new LinkedHashMap<>( params );

        if ( !scalable )
        {
            return queryParams;
        }

        if ( quality != null )
        {
            queryParams.put( "quality", List.of( quality.toString() ) );
        }
        if ( background != null )
        {
            queryParams.put( "background", List.of( background ) );
        }
        if ( filter != null )
        {
            queryParams.put( "filter", List.of( filter ) );
        }

        return queryParams;
    }

    /**
     * @return whether the image resolves and the image API scales it
     */
    private static boolean isScalable( final Supplier<Media> media )
    {
        try
        {
            return ImageScaling.isScalable( MediaLookup.image( media.get() ) );
        }
        catch ( RuntimeException e )
        {
            return false;
        }
    }

    private static Map<String, List<String>> attachmentQueryParams( final Map<String, List<String>> params, final boolean download )
    {
        final Map<String, List<String>> queryParams = new LinkedHashMap<>( params );

        if ( download )
        {
            queryParams.put( "download", List.of() );
        }

        return queryParams;
    }

    private static String queryString( final Map<String, List<String>> queryParams )
    {
        final DefaultQueryParamsSupplier queryParamsStrategy = new DefaultQueryParamsSupplier();
        queryParamsStrategy.params( queryParams );
        return queryParamsStrategy.get();
    }

    @Override
    public String apiUrl( final ApiUrlGeneratorParams params )
    {
        final DefaultQueryParamsSupplier queryParamsStrategy = new DefaultQueryParamsSupplier();
        queryParamsStrategy.params( params.getQueryParams() );

        final UrlGeneratorParams generatorParams = UrlGeneratorParams.create()
            .setBaseUrl( apiBaseUrl( params.getUrlType(), params.getBaseUrl(), params.getDescriptorKey() ) )
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
