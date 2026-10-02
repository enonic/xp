package com.enonic.xp.portal.impl.url;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Strings;
import com.google.common.base.Suppliers;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.macro.MacroService;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.RedirectChecksumService;
import com.enonic.xp.portal.url.ApiUrlGeneratorParams;
import com.enonic.xp.portal.url.ApiUrlParams;
import com.enonic.xp.portal.url.AssetUrlParams;
import com.enonic.xp.portal.url.AttachmentUrlGeneratorParams;
import com.enonic.xp.portal.url.AttachmentUrlParams;
import com.enonic.xp.portal.url.BaseUrlParams;
import com.enonic.xp.portal.url.ComponentUrlParams;
import com.enonic.xp.portal.url.GenerateUrlParams;
import com.enonic.xp.portal.url.IdentityUrlParams;
import com.enonic.xp.portal.url.ImageUrlGeneratorParams;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PageUrlParts;
import com.enonic.xp.portal.url.PageUrlPartsParams;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.PortalUrlGeneratorService;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ProcessHtmlParams;
import com.enonic.xp.portal.url.ProcessHtmlPartsParams;
import com.enonic.xp.portal.url.ProcessedHtml;
import com.enonic.xp.portal.url.ServiceUrlParams;
import com.enonic.xp.portal.url.UrlBase;
import com.enonic.xp.portal.url.UrlBaseParams;
import com.enonic.xp.portal.url.UrlGeneratorParams;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.style.StyleDescriptorService;

import static java.util.Objects.requireNonNull;

@Component(immediate = true, configurationPid = "com.enonic.xp.portal")
public final class PortalUrlServiceImpl
    implements PortalUrlService
{
    private final ContentService contentService;

    private final ResourceService resourceService;

    private final MacroService macroService;

    private final StyleDescriptorService styleDescriptorService;

    private final RedirectChecksumService redirectChecksumService;

    private final ProjectService projectService;

    private final PortalUrlGeneratorService portalUrlGeneratorService;

    @Activate
    public PortalUrlServiceImpl( @Reference final ContentService contentService, @Reference final ResourceService resourceService,
                                 @Reference final MacroService macroService, @Reference final StyleDescriptorService styleDescriptorService,
                                 @Reference final RedirectChecksumService redirectChecksumService,
                                 @Reference final ProjectService projectService,
                                 @Reference final PortalUrlGeneratorService portalUrlGeneratorService )
    {
        this.contentService = contentService;
        this.resourceService = resourceService;
        this.macroService = macroService;
        this.styleDescriptorService = styleDescriptorService;
        this.redirectChecksumService = redirectChecksumService;
        this.projectService = projectService;
        this.portalUrlGeneratorService = portalUrlGeneratorService;
    }

    @Override
    public String assetUrl( final AssetUrlParams params )
    {
        final AssetBaseUrlSupplier baseUrlSupplier = new AssetBaseUrlSupplier( params.getType() );
        final AssetPathSupplier pathSupplier = new AssetPathSupplier( resourceService, params.getApplication(), params.getPath() );

        final DefaultQueryParamsSupplier queryParamsStrategy = new DefaultQueryParamsSupplier();
        queryParamsStrategy.params( params.getParams() );

        return portalUrlGeneratorService.generateUrl( UrlGeneratorParams.create()
                                                          .setBaseUrl( baseUrlSupplier )
                                                          .setPath( pathSupplier )
                                                          .setQueryString( queryParamsStrategy )
                                                          .build() );
    }

    @Override
    public String serviceUrl( final ServiceUrlParams params )
    {
        final ServiceRequestBaseUrlSupplier baseUrlSupplier = ServiceRequestBaseUrlSupplier.create().setUrlType( params.getType() ).build();

        final ServicePathSupplier pathStrategy = new ServicePathSupplier( params.getApplication(), params.getService() );

        final DefaultQueryParamsSupplier queryParamsStrategy = new DefaultQueryParamsSupplier();
        queryParamsStrategy.params( params.getParams() );

        return portalUrlGeneratorService.generateUrl( UrlGeneratorParams.create()
                                                          .setBaseUrl( baseUrlSupplier )
                                                          .setPath( pathStrategy )
                                                          .setQueryString( queryParamsStrategy )
                                                          .build() );
    }

    @Override
    public String baseUrl( final BaseUrlParams params )
    {
        // a base URL is a prefix of other URLs, so a failure is reported to the caller instead
        // of being encoded into the result: an error URL used as a base would silently prefix
        // every URL built from it
        return runWithAdminRole( () -> UrlGenerator.removeTrailingSlash(
            new ContentBaseUrlSupplier( contentService, projectService, params ).get() ) );
    }

    @Override
    public String pageUrl( final PageUrlParams params )
    {
        final Supplier<String> baseUrlSupplier = new PageBaseUrlSupplier( contentService, projectService, params );

        final DefaultQueryParamsSupplier queryParamsStrategy = new DefaultQueryParamsSupplier();
        queryParamsStrategy.params( params.getParams() );

        return portalUrlGeneratorService.generateUrl(
            UrlGeneratorParams.create().setBaseUrl( baseUrlSupplier ).setQueryString( queryParamsStrategy ).build() );
    }

    @Override
    public UrlBase urlBase( final UrlBaseParams params )
    {
        return runWithAdminRole( () -> {
            final BaseUrlMetadata metadata = new BaseUrlExtractor( contentService, projectService ).extractFromConfiguration(
                params.getKey(), params.getProjectName(), params.getBranch() );

            final String baseUrl = metadata.baseUrl();

            return new UrlBase( metadata.projectName(), metadata.branch(), metadata.anchorPath(),
                                Strings.isNullOrEmpty( baseUrl ) ? null : UrlGenerator.removeTrailingSlash( baseUrl ),
                                metadata.siteConfigs() );
        } );
    }

    /**
     * @return the given base, or else the project of the current context
     */
    private UrlBase baseOrProject( final UrlBase base )
    {
        return base != null ? base : urlBase( UrlBaseParams.create().build() );
    }

    @Override
    public PageUrlParts pageUrlParts( final PageUrlPartsParams params )
    {
        final UrlBase base = baseOrProject( params.getBase() );

        return runWithAdminRole( () -> {
            final String path = ContextBuilder.copyOf( ContextAccessor.current() )
                .repositoryId( base.getProjectName().getRepoId() )
                .branch( base.getBranch() )
                .build()
                .callWith( () -> ContentPathResolver.relativeToAnchor( PageBase.contentPath( contentService, params ), base.getPath() ) );

            final StringBuilder escapedPath = new StringBuilder();
            UrlBuilderHelper.appendAndEncodePathParts( escapedPath, path );

            final DefaultQueryParamsSupplier queryParamsStrategy = new DefaultQueryParamsSupplier();
            queryParamsStrategy.params( params.getQueryParams() );

            return new PageUrlParts( base.getBaseUrl(), escapedPath.toString(), queryParamsStrategy.get() );
        } );
    }

    private static <T> T runWithAdminRole( final Callable<T> callable )
    {
        final Context context = ContextAccessor.current();
        final AuthenticationInfo authenticationInfo =
            AuthenticationInfo.copyOf( context.getAuthInfo() ).principals( RoleKeys.ADMIN ).build();
        return ContextBuilder.from( context ).authInfo( authenticationInfo ).build().callWith( callable );
    }

    @Override
    public String componentUrl( final ComponentUrlParams params )
    {
        final Supplier<String> componentPathSupplier = Suppliers.memoize( () -> new ComponentResolver( params.getComponent() ).resolve() );

        final Supplier<String> baseUrlSupplier =
            new ComponentBaseUrlSupplier( contentService, projectService, params, componentPathSupplier );

        final Supplier<String> pathSupplier = new ComponentPathSupplier( componentPathSupplier );

        final DefaultQueryParamsSupplier queryParamsStrategy = new DefaultQueryParamsSupplier();
        queryParamsStrategy.params( params.getParams() );

        return portalUrlGeneratorService.generateUrl( UrlGeneratorParams.create()
                                                          .setBaseUrl( baseUrlSupplier )
                                                          .setPath( pathSupplier )
                                                          .setQueryString( queryParamsStrategy )
                                                          .build() );
    }

    @Override
    public String imageUrl( final ImageUrlParams params )
    {
        final boolean explicitBaseUrl = params.getBaseUrl() != null;

        final Supplier<ProjectName> projectNameSupplier = () -> ContentProjectResolver.create()
            .setProjectName( params.getProjectName() )
            .setPreferSiteRequest( !explicitBaseUrl )
            .build()
            .resolve();

        final Supplier<Branch> branchSupplier = () -> ContentBranchResolver.create()
            .setBranch( params.getBranch() )
            .setPreferSiteRequest( !explicitBaseUrl )
            .build()
            .resolve();

        final Supplier<Media> mediaSupplier = IdentifiedSupplier.of( params.getId(), () -> {
            final ProjectName projectName = projectNameSupplier.get();
            final Branch branch = branchSupplier.get();

            final Content content = MediaResolver.create( projectName, branch, contentService )
                .setHasExplicitBaseUrl( explicitBaseUrl )
                .setId( params.getId() )
                .setPath( params.getPath() )
                .build()
                .resolve();

            if ( content instanceof Media media )
            {
                return media;
            }

            throw ContentNotFoundException.create()
                .repositoryId( projectName.getRepoId() )
                .branch( branch )
                .contentId( content.getId() )
                .build();
        } );

        final ImageUrlGeneratorParams generatorParams = ImageUrlGeneratorParams.create()
            .setBaseUrl( params.getBaseUrl() )
            .setUrlType( params.getType() )
            .setMedia( mediaSupplier )
            .setProjectName( projectNameSupplier )
            .setBranch( branchSupplier )
            .setScale( params.getScale() )
            .setFormat( params.getFormat() )
            .setFilter( params.getFilter() )
            .setQuality( params.getQuality() )
            .setBackground( params.getBackground() )
            .setQueryParams( params.getParams().asMap() )
            .build();

        return portalUrlGeneratorService.imageUrl( generatorParams );
    }

    @Override
    public String attachmentUrl( final AttachmentUrlParams params )
    {
        final boolean explicitBaseUrl = params.getBaseUrl() != null;

        final Supplier<ProjectName> projectNameSupplier = () -> ContentProjectResolver.create()
            .setProjectName( params.getProjectName() )
            .setPreferSiteRequest( !explicitBaseUrl )
            .build()
            .resolve();

        final Supplier<Branch> branchSupplier = () -> ContentBranchResolver.create()
            .setBranch( params.getBranch() )
            .setPreferSiteRequest( !explicitBaseUrl )
            .build()
            .resolve();

        final Supplier<Content> contentSupplier = IdentifiedSupplier.of( params.getId(), () -> {
            final ProjectName projectName = projectNameSupplier.get();
            final Branch branch = branchSupplier.get();

            return MediaResolver.create( projectName, branch, contentService )
                .setHasExplicitBaseUrl( explicitBaseUrl )
                .setId( params.getId() )
                .setPath( params.getPath() )
                .build()
                .resolve();
        } );

        final AttachmentUrlGeneratorParams generatorParams = AttachmentUrlGeneratorParams.create()
            .setBaseUrl( params.getBaseUrl() )
            .setUrlType( params.getType() )
            .setProjectName( projectNameSupplier )
            .setBranch( branchSupplier )
            .setContent( contentSupplier )
            .setDownload( params.isDownload() )
            .setName( params.getName() )
            .setLabel( params.getLabel() )
            .setQueryParams( params.getParams().asMap() )
            .build();

        return portalUrlGeneratorService.attachmentUrl( generatorParams );
    }

    @Override
    public String identityUrl( final IdentityUrlParams params )
    {
        final Supplier<String> baseUrlSupplier = new IdentityBaseUrlSupplier( params.getType() );

        final Supplier<String> pathSupplier = new IdentityPathSupplier( params );

        final DefaultQueryParamsSupplier queryParamsStrategy = new DefaultQueryParamsSupplier();
        queryParamsStrategy.params( params.getParams() );

        final String redirectionUrl = params.getRedirectionUrl();
        if ( redirectionUrl != null )
        {
            queryParamsStrategy.param( "redirect", redirectionUrl );
            queryParamsStrategy.param( "_ticket", redirectChecksumService.generateChecksum( redirectionUrl ) );
        }

        return portalUrlGeneratorService.generateUrl( UrlGeneratorParams.create()
                                                          .setBaseUrl( baseUrlSupplier )
                                                          .setPath( pathSupplier )
                                                          .setQueryString( queryParamsStrategy )
                                                          .build() );
    }

    @Override
    public String generateUrl( final GenerateUrlParams params )
    {
        final Supplier<String> baseUrlSupplier = ( () -> {
            if ( params.getPath() != null && params.getPathSegments() != null )
            {
                throw new IllegalArgumentException( "path and pathSegments cannot be set at the same time" );
            }

            final StringBuilder url = new StringBuilder();
            UrlBuilderHelper.appendAndEncodePathParts( url, params.getPath() );
            UrlBuilderHelper.appendPathSegments( url, params.getPathSegments() );
            return UrlBuilderHelper.rewriteUri( requireNonNull( PortalRequestAccessor.get(), "no request bound" ).getRawRequest(),
                                                params.getType(), url.toString() );
        } );

        final DefaultQueryParamsSupplier queryParamsStrategy = new DefaultQueryParamsSupplier();
        queryParamsStrategy.params( params.getParams() );

        return portalUrlGeneratorService.generateUrl(
            UrlGeneratorParams.create().setBaseUrl( baseUrlSupplier ).setQueryString( queryParamsStrategy ).build() );
    }

    @Override
    public String processHtml( final ProcessHtmlParams params )
    {
        return new RichTextProcessor( styleDescriptorService, this, portalUrlGeneratorService, macroService, contentService ).process(
            params );
    }

    @Override
    public ProcessedHtml processHtmlParts( final ProcessHtmlPartsParams params )
    {
        return new RichTextPartsProcessor( styleDescriptorService, this, portalUrlGeneratorService, macroService, contentService, params,
                                           baseOrProject( params.getBase() ) ).process();
    }

    @Override
    public String apiUrl( final ApiUrlParams params )
    {
        final ApiUrlGeneratorParams generatorParams = ApiUrlGeneratorParams.create()
            .setBaseUrl( params.getBaseUrl() )
            .setUrlType( params.getType() )
            .setDescriptorKey( params.getApi() )
            .setPath( new ApiUrlPathResolver( params.getPath(), params.getPathSegments() ) )
            .setQueryParams( params.getQueryParams() )
            .build();

        return portalUrlGeneratorService.apiUrl( generatorParams );
    }
}
