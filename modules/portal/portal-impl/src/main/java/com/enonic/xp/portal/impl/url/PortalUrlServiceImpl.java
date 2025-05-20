package com.enonic.xp.portal.impl.url;

import java.util.function.Supplier;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
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
import com.enonic.xp.macro.MacroService;
import com.enonic.xp.portal.PortalRequest;
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
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.PortalUrlGeneratorService;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ProcessHtmlParams;
import com.enonic.xp.portal.url.ServiceUrlParams;
import com.enonic.xp.portal.url.UrlGeneratorParams;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.style.StyleDescriptorService;

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
        params.getParams().forEach( queryParamsStrategy::put );

        return portalUrlGeneratorService.generateUrl( UrlGeneratorParams.create()
                                                          .setBaseUrl( baseUrlSupplier )
                                                          .setPath( pathSupplier )
                                                          .setQueryString( queryParamsStrategy )
                                                          .build() );
    }

    @Override
    public String serviceUrl( final ServiceUrlParams params )
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();

        final ServiceRequestBaseUrlSupplier baseUrlSupplier =
            ServiceRequestBaseUrlSupplier.create().setPortalRequest( portalRequest ).setUrlType( params.getType() ).build();

        final Supplier<String> pathStrategy = () -> {
            final ApplicationKey applicationKey =
                new ApplicationResolver().portalRequest( portalRequest ).application( params.getApplication() ).resolve();

            final StringBuilder url = new StringBuilder();

            UrlBuilderHelper.appendSubPath( url, "service" );
            UrlBuilderHelper.appendPart( url, applicationKey.toString() );
            UrlBuilderHelper.appendPart( url, params.getService() );

            return url.toString();
        };

        final DefaultQueryParamsSupplier queryParamsStrategy = new DefaultQueryParamsSupplier();
        params.getParams().forEach( queryParamsStrategy::put );

        return portalUrlGeneratorService.generateUrl( UrlGeneratorParams.create()
                                                          .setBaseUrl( baseUrlSupplier )
                                                          .setPath( pathStrategy )
                                                          .setQueryString( queryParamsStrategy )
                                                          .build() );
    }

    @Override
    public String baseUrl( final BaseUrlParams params )
    {
        final Supplier<String> baseUrlStrategy = new ContentBaseUrlSupplier( contentService, projectService, params );
        return portalUrlGeneratorService.generateUrl( UrlGeneratorParams.create().setBaseUrl( baseUrlStrategy ).build() );
    }

    @Override
    public String pageUrl( final PageUrlParams params )
    {
        final Supplier<String> baseUrlSupplier = new PageBaseUrlSupplier( contentService, projectService, params );

        final DefaultQueryParamsSupplier queryParamsStrategy = new DefaultQueryParamsSupplier();
        params.getParams().forEach( queryParamsStrategy::put );

        return portalUrlGeneratorService.generateUrl(
            UrlGeneratorParams.create().setBaseUrl( baseUrlSupplier ).setQueryString( queryParamsStrategy ).build() );
    }

    @Override
    public String componentUrl( final ComponentUrlParams params )
    {
        final Supplier<String> componentPathSupplier = Suppliers.memoize( () -> new ComponentResolver( params.getComponent() ).resolve() );

        final Supplier<String> baseUrlSupplier =
            new ComponentBaseUrlSupplier( contentService, projectService, params, componentPathSupplier );

        final Supplier<String> pathSupplier = new ComponentPathSupplier( componentPathSupplier );

        final DefaultQueryParamsSupplier queryParamsStrategy = new DefaultQueryParamsSupplier();
        params.getParams().forEach( queryParamsStrategy::put );

        return portalUrlGeneratorService.generateUrl( UrlGeneratorParams.create()
                                                          .setBaseUrl( baseUrlSupplier )
                                                          .setPath( pathSupplier )
                                                          .setQueryString( queryParamsStrategy )
                                                          .build() );
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

        final Supplier<Media> mediaSupplier = () -> {
            final ProjectName projectName = projectNameSupplier.get();
            final Branch branch = branchSupplier.get();

            final MediaResolverResult mediaResolverResult = MediaResolver.create( projectName, branch, contentService )
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
            .addQueryParams( params.getParams().asMap() )
            .build();

        return portalUrlGeneratorService.imageUrl( generatorParams );
    }

    @Override
    public String attachmentUrl( final AttachmentUrlParams params )
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

        final Supplier<Content> contentSupplier = () -> {
            final ProjectName projectName = projectNameSupplier.get();
            final Branch branch = branchSupplier.get();

            final MediaResolverResult mediaResolverResult = MediaResolver.create( projectName, branch, contentService )
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

        final AttachmentUrlGeneratorParams generatorParams = AttachmentUrlGeneratorParams.create()
            .setBaseUrl( params.getBaseUrl() )
            .setUrlType( params.getType() )
            .setProjectName( projectNameSupplier )
            .setBranch( branchSupplier )
            .setContent( contentSupplier )
            .setDownload( params.isDownload() )
            .setName( params.getName() )
            .setLabel( params.getLabel() )
            .addQueryParams( params.getParams().asMap() )
            .build();

        return portalUrlGeneratorService.attachmentUrl( generatorParams );
    }

    @Override
    public String identityUrl( final IdentityUrlParams params )
    {
        final Supplier<String> baseUrlSupplier = new IdentityBaseUrlSupplier( params.getType() );

        final Supplier<String> pathSupplier = new IdentityPathSupplier( params );

        final DefaultQueryParamsSupplier queryParamsStrategy = new DefaultQueryParamsSupplier();
        params.getParams().forEach( queryParamsStrategy::put );

        final String redirectionUrl = params.getRedirectionUrl();
        if ( redirectionUrl != null )
        {
            queryParamsStrategy.put( "redirect", redirectionUrl );
            queryParamsStrategy.put( "_ticket", redirectChecksumService.generateChecksum( redirectionUrl ) );
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
            final StringBuilder url = new StringBuilder();
            UrlBuilderHelper.appendAndEncodePathParts( url, params.getPath() );
            return UrlBuilderHelper.rewriteUri( PortalRequestAccessor.get().getRawRequest(), params.getType(), url.toString() );
        } );

        final DefaultQueryParamsSupplier queryParamsStrategy = new DefaultQueryParamsSupplier();
        params.getParams().forEach( queryParamsStrategy::put );

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
    public String apiUrl( final ApiUrlParams params )
    {
        final ApiUrlGeneratorParams generatorParams = ApiUrlGeneratorParams.create()
            .setBaseUrl( params.getBaseUrl() )
            .setUrlType( params.getType() )
            .setApplication( new ApiUrlApplicationResolver( params.getApplication() ) )
            .setApi( params.getApi() )
            .setPath( new ApiUrlPathResolver( params.getPath(), params.getPathSegments() ) )
            .addQueryParams( params.getQueryParams() )
            .build();

        return portalUrlGeneratorService.apiUrl( generatorParams );
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
