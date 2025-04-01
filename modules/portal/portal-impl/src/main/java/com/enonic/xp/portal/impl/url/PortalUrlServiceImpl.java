package com.enonic.xp.portal.impl.url;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Suppliers;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.macro.MacroService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.portal.impl.RedirectChecksumService;
import com.enonic.xp.portal.url.AbstractUrlParams;
import com.enonic.xp.portal.url.ApiUrlGeneratorParams;
import com.enonic.xp.portal.url.ApiUrlParams;
import com.enonic.xp.portal.url.AssetUrlParams;
import com.enonic.xp.portal.url.AttachmentUrlGeneratorParams;
import com.enonic.xp.portal.url.AttachmentUrlParams;
import com.enonic.xp.portal.url.BaseUrlParams;
import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.ComponentUrlParams;
import com.enonic.xp.portal.url.GenerateUrlParams;
import com.enonic.xp.portal.url.IdentityUrlParams;
import com.enonic.xp.portal.url.ImageUrlGeneratorParams;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PageUrlGeneratorParams;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ProcessHtmlParams;
import com.enonic.xp.portal.url.ServiceUrlParams;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.Site;
import com.enonic.xp.style.StyleDescriptorService;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPathSegments;
import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendSubPath;

@Component(immediate = true, configurationPid = "com.enonic.xp.portal")
public final class PortalUrlServiceImpl
    implements PortalUrlService
{
    private final ContentService contentService;

    private final ResourceService resourceService;

    private final MacroService macroService;

    private final StyleDescriptorService styleDescriptorService;

    private final RedirectChecksumService redirectChecksumService;

    private final UrlGeneratorParamsAdapter urlStrategyFacade;

    private volatile boolean useLegacyAssetContextPath;

    private volatile boolean useLegacyIdProviderContextPath;

    @Activate
    public PortalUrlServiceImpl( @Reference final ContentService contentService, @Reference final ResourceService resourceService,
                                 @Reference final MacroService macroService, @Reference final StyleDescriptorService styleDescriptorService,
                                 @Reference final RedirectChecksumService redirectChecksumService,
                                 @Reference final UrlGeneratorParamsAdapter urlStrategyFacade )
    {
        this.contentService = contentService;
        this.resourceService = resourceService;
        this.macroService = macroService;
        this.styleDescriptorService = styleDescriptorService;
        this.redirectChecksumService = redirectChecksumService;
        this.urlStrategyFacade = urlStrategyFacade;
    }

    @Activate
    @Modified
    public void activate( final PortalConfig config )
    {
        this.useLegacyAssetContextPath = config.asset_legacyContextPath();
        this.useLegacyIdProviderContextPath = config.idprovider_legacyContextPath();
    }

    @Override
    public String assetUrl( final AssetUrlParams params )
    {
        final AssetUrlBuilder builder = new AssetUrlBuilder();
        builder.setUseLegacyContextPath( useLegacyAssetContextPath );
        return build( builder, params );
    }

    @Override
    public String serviceUrl( final ServiceUrlParams params )
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();

        final ServiceRequestBaseUrlStrategy baseUrlStrategy = ServiceRequestBaseUrlStrategy.create()
            .setPortalRequest( portalRequest )
            .setUrlType( params.getType() )
            .setContextPathType( params.getContextPathType() )
            .build();

        final PathStrategy pathStrategy = () -> {
            final ApplicationKey applicationKey =
                new ApplicationResolver().portalRequest( portalRequest ).application( params.getApplication() ).resolve();

            final StringBuilder url = new StringBuilder();

            UrlBuilderHelper.appendSubPath( url, "service" );
            UrlBuilderHelper.appendPart( url, applicationKey.toString() );
            UrlBuilderHelper.appendPart( url, params.getService() );

            return url.toString();
        };

        final DefaultQueryParamsStrategy queryParamsStrategy = new DefaultQueryParamsStrategy();
        params.getParams().forEach( queryParamsStrategy::put );

        return runWithAdminRole( () -> UrlGenerator.generateUrl( baseUrlStrategy, pathStrategy, queryParamsStrategy ) );
    }

    @Override
    public String baseUrl( final BaseUrlParams params )
    {
        final BaseUrlStrategy baseUrlStrategy = urlStrategyFacade.contentBaseUrlStrategy( params );
        return runWithAdminRole( () -> UrlGenerator.generateUrl( baseUrlStrategy, () -> null, () -> null ) );
    }

    @Override
    public String pageUrl( final PageUrlParams params )
    {
        final BaseUrlStrategy baseUrlStrategy = urlStrategyFacade.pageBaseUrlStrategy( params );

        final PageUrlGeneratorParams.Builder builder = PageUrlGeneratorParams.create().setBaseUrlStrategy( baseUrlStrategy );
        if ( params.getParams() != null )
        {
            builder.addQueryParams( params.getParams().asMap() );
        }
        return pageUrl( builder.build() );
    }

    @Override
    public String componentUrl( final ComponentUrlParams params )
    {
        final Supplier<String> componentPathSupplier = Suppliers.memoize( () -> new ComponentResolver( params.getComponent() ).resolve() );

        final BaseUrlStrategy baseUrlStrategy = urlStrategyFacade.componentBaseUrlStrategy( params, componentPathSupplier );

        final PathStrategy pathStrategy = new ComponentPathStrategy( componentPathSupplier );

        final DefaultQueryParamsStrategy queryParamsStrategy = new DefaultQueryParamsStrategy();
        params.getParams().forEach( queryParamsStrategy::put );

        return runWithAdminRole( () -> UrlGenerator.generateUrl( baseUrlStrategy, pathStrategy, queryParamsStrategy ) );
    }

    @Override
    public String imageUrl( final ImageUrlParams params )
    {
        final Supplier<ProjectName> projectNameSupplier = () -> ContentProjectResolver.create()
            .setProjectName( params.getProjectName() )
            .setPreferSiteRequest( params.getBaseUrl() == null )
            .build()
            .resolve();

        final Supplier<Branch> branchSupplier = () -> ContentBranchResolver.create()
            .setBranch( params.getBranch() )
            .setPreferSiteRequest( params.getBaseUrl() == null )
            .build()
            .resolve();

        final PortalRequest portalRequest = PortalRequestAccessor.get();

        final Supplier<Media> mediaSupplier = () -> {
            final ProjectName projectName = projectNameSupplier.get();
            final Branch branch = branchSupplier.get();

            final MediaResolverResult mediaResolverResult = MediaResolver.create( projectName, branch, contentService )
                .setPortalRequest( portalRequest )
                .setBaseUrl( params.getBaseUrl() )
                .setId( params.getId() )
                .setPath( params.getPath() )
                .build()
                .resolve();

            if ( mediaResolverResult.getContent() instanceof Media media )
            {
                return media;
            }

            throw createContentNotFoundException( projectName, branch, mediaResolverResult.getContentKey() );
        };

        final BaseUrlStrategy baseUrlStrategy = portalRequest == null
            ? urlStrategyFacade.noRequestMediaBaseUrlStrategy( params.getBaseUrl() )
            : urlStrategyFacade.requestMediaBaseUrlStrategy( params.getBaseUrl(), params.getType(), "image" );

        final ImageUrlGeneratorParams generatorParams = ImageUrlGeneratorParams.create()
            .setBaseUrlStrategy( baseUrlStrategy )
            .setMedia( mediaSupplier )
            .setProjectName( projectNameSupplier )
            .setBranch( branchSupplier )
            .setScale( params.getScale() )
            .setFormat( params.getFormat() )
            .setFilter( params.getFilter() )
            .setQuality( params.getQuality() )
            .setBackground( params.getBackground() )
            .addQueryParams( params.getParams().asMap() )
            .build();

        return imageUrl( generatorParams );
    }

    @Override
    public String attachmentUrl( final AttachmentUrlParams params )
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();

        final Supplier<ProjectName> projectNameSupplier = () -> ContentProjectResolver.create()
            .setProjectName( params.getProjectName() )
            .setPreferSiteRequest( params.getBaseUrl() == null )
            .build()
            .resolve();

        final Supplier<Branch> branchSupplier = () -> ContentBranchResolver.create()
            .setBranch( params.getBranch() )
            .setPreferSiteRequest( params.getBaseUrl() == null )
            .build()
            .resolve();

        final Supplier<Content> contentSupplier = () -> {
            final ProjectName projectName = projectNameSupplier.get();
            final Branch branch = branchSupplier.get();

            final MediaResolverResult mediaResolverResult = MediaResolver.create( projectName, branch, contentService )
                .setPortalRequest( portalRequest )
                .setBaseUrl( params.getBaseUrl() )
                .setId( params.getId() )
                .setPath( params.getPath() )
                .build()
                .resolve();

            final Content content = mediaResolverResult.getContent();

            if ( content == null )
            {
                throw createContentNotFoundException( projectName, branch, mediaResolverResult.getContentKey() );
            }

            return content;
        };

        final BaseUrlStrategy baseUrlStrategy = portalRequest == null
            ? urlStrategyFacade.noRequestMediaBaseUrlStrategy( params.getBaseUrl() )
            : urlStrategyFacade.requestMediaBaseUrlStrategy( params.getBaseUrl(), params.getType(), "attachment" );

        final AttachmentUrlGeneratorParams generatorParams = AttachmentUrlGeneratorParams.create()
            .setBaseUrlStrategy( baseUrlStrategy )
            .setProjectName( projectNameSupplier )
            .setBranch( branchSupplier )
            .setContent( contentSupplier )
            .setDownload( params.isDownload() )
            .setName( params.getName() )
            .setLabel( params.getLabel() )
            .addQueryParams( params.getParams().asMap() )
            .build();

        return attachmentUrl( generatorParams );
    }

    @Override
    public String identityUrl( final IdentityUrlParams params )
    {
        final IdentityUrlBuilder builder = new IdentityUrlBuilder( redirectChecksumService::generateChecksum );
        builder.setUseLegacyContextPath( useLegacyIdProviderContextPath );

        return build( builder, params );
    }

    @Override
    public String generateUrl( final GenerateUrlParams params )
    {
        return build( new GenerateUrlBuilder(), params );
    }

    @Override
    public String processHtml( final ProcessHtmlParams params )
    {
        return new RichTextProcessor( styleDescriptorService, this, macroService ).process( params );
    }

    @Override
    public String apiUrl( final ApiUrlParams params )
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();

        BaseUrlStrategy baseUrlStrategy;
        if ( params.getBaseUrl() != null )
        {
            baseUrlStrategy = new CustomBaseUrlStrategy( params.getBaseUrl() );
        }
        else if ( portalRequest == null || portalRequest.getBaseUri().isEmpty() || portalRequest.getBaseUri().startsWith( "/api/" ) )
        {
            baseUrlStrategy = new SlashApiBaseUrlStrategy();
        }
        else
        {
            baseUrlStrategy = new RequestBaseUrlStrategy( portalRequest, contentService );
        }

        String application = params.getApplication();
        if ( portalRequest == null )
        {
            Objects.requireNonNull( application, "Application must be provided" );
        }
        else
        {
            if ( application == null && portalRequest.getApplicationKey() != null )
            {
                application = portalRequest.getApplicationKey().toString();
            }

            if ( application == null )
            {
                throw new IllegalArgumentException( "Application must be provided" );
            }
        }

        final ApiUrlGeneratorParams generatorParams = ApiUrlGeneratorParams.create()
            .setUrlType( params.getType() )
            .setBaseUrlStrategy( baseUrlStrategy )
            .setApplication( application )
            .setApi( params.getApi() )
            .setPath( () -> {
                final StringBuilder path = new StringBuilder();

                appendSubPath( path, params.getPath() );
                appendPathSegments( path, params.getPathSegments() );

                return path.toString();
            } )
            .addQueryParams( params.getQueryParams() )
            .build();

        return apiUrl( generatorParams );
    }

    @Override
    public String imageUrl( final ImageUrlGeneratorParams params )
    {
        final ImageMediaPathStrategyParams imageMediaPathStrategyParams = ImageMediaPathStrategyParams.create()
            .setMedia( params.getMedia() )
            .setProjectName( params.getProjectName() )
            .setBranch( params.getBranch() )
            .setScale( params.getScale() )
            .setFormat( params.getFormat() )
            .build();

        final ApiUrlGeneratorParams.Builder builder = ApiUrlGeneratorParams.create()
            .setBaseUrlStrategy( params.getBaseUrlStrategy() )
            .setApplication( "media" )
            .setApi( "image" )
            .setPath( () -> new ImageMediaPathStrategy( imageMediaPathStrategyParams ).generatePath() );

        if ( params.getQuality() != null )
        {
            builder.addQueryParam( "quality", params.getQuality().toString() );
        }
        if ( params.getBackground() != null )
        {
            builder.addQueryParam( "background", params.getBackground() );
        }
        if ( params.getFilter() != null )
        {
            builder.addQueryParam( "filter", params.getFilter() );
        }

        builder.addQueryParams( params.getQueryParams() );

        final ApiUrlGeneratorParams apiUrlParams = builder.build();

        return apiUrl( apiUrlParams );
    }

    @Override
    public String attachmentUrl( final AttachmentUrlGeneratorParams params )
    {
        final AttachmentMediaPathStrategyParams strategyParams = AttachmentMediaPathStrategyParams.create()
            .setContent( params.getContentSupplier() )
            .setProjectName( params.getProjectName() )
            .setBranch( params.getBranch() )
            .build();

        final ApiUrlGeneratorParams.Builder builder = ApiUrlGeneratorParams.create()
            .setBaseUrlStrategy( params.getBaseUrlStrategy() )
            .setApplication( "media" )
            .setApi( "attachment" )
            .setPath( () -> new AttachmentMediaPathStrategy( strategyParams ).generatePath() )
            .addQueryParams( params.getQueryParams() );

        if ( params.isDownload() )
        {
            builder.addQueryParam( "download", null );
        }

        final ApiUrlGeneratorParams apiUrlParams = builder.build();

        return apiUrl( apiUrlParams );
    }

    @Override
    public String pageUrl( final PageUrlGeneratorParams params )
    {
        final DefaultQueryParamsStrategy queryParamsStrategy = new DefaultQueryParamsStrategy();
        params.getQueryParams().forEach( queryParamsStrategy::putAll );

        return runWithAdminRole( () -> UrlGenerator.generateUrl( params.getBaseUrlStrategy(), () -> "", queryParamsStrategy ) );
    }

    @Override
    public String apiUrl( final ApiUrlGeneratorParams params )
    {
        final BaseUrlStrategy baseUrlStrategy = () -> {
            final BaseUrlStrategy originalBaseUrlStrategy = params.getBaseUrlStrategy();

            final StringBuilder url = new StringBuilder( originalBaseUrlStrategy.generateBaseUrl() );
            UrlBuilderHelper.appendPart( url, params.getApplication() + ":" + params.getApi() );

            if ( originalBaseUrlStrategy instanceof CustomBaseUrlStrategy )
            {
                return url.toString();
            }

            final PortalRequest portalRequest = PortalRequestAccessor.get();
            if (portalRequest != null )
            {
                return UrlBuilderHelper.rewriteUri( portalRequest.getRawRequest(), params.getUrlType() , url.toString() );
            }
            else
            {
                return url.toString();
            }
        };

        final PathStrategy pathStrategy = () -> params.getPath() != null ? params.getPath().get() : null;

        final DefaultQueryParamsStrategy queryParamsStrategy = new DefaultQueryParamsStrategy();
        params.getQueryParams().forEach( queryParamsStrategy::putAll );

        return runWithAdminRole( () -> UrlGenerator.generateUrl( baseUrlStrategy, pathStrategy, queryParamsStrategy ) );
    }

    private <B extends PortalUrlBuilder<P>, P extends AbstractUrlParams> String build( final B builder, final P params )
    {
        builder.setParams( params );
        builder.contentService = this.contentService;
        builder.resourceService = this.resourceService;
        return runWithAdminRole( builder::build );
    }

    private <T> T runWithAdminRole( final Callable<T> callable )
    {
        final Context context = ContextAccessor.current();
        final AuthenticationInfo authenticationInfo =
            AuthenticationInfo.copyOf( context.getAuthInfo() ).principals( RoleKeys.ADMIN ).build();
        return ContextBuilder.from( context ).authInfo( authenticationInfo ).build().callWith( callable );
    }

    private ContentNotFoundException createContentNotFoundException( final ProjectName projectName, final Branch branch,
                                                                     final String contentKey )
    {
        final ContentNotFoundException.Builder ex =
            ContentNotFoundException.create().repositoryId( projectName.getRepoId() ).branch( branch );

        if ( contentKey.startsWith( "/" ) )
        {
            ex.contentPath( ContentPath.from( contentKey ) );
        }
        else
        {
            ex.contentId( ContentId.from( contentKey ) );
        }

        return ex.build();
    }
}
