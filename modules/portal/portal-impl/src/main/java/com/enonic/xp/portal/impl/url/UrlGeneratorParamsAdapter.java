package com.enonic.xp.portal.impl.url;

import java.net.URI;
import java.util.Objects;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.url.ApiUrlGeneratorParams;
import com.enonic.xp.portal.url.ApiUrlParams;
import com.enonic.xp.portal.url.BaseUrlParams;
import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.site.Site;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPathSegments;
import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendSubPath;

@Component(immediate = true, service = UrlGeneratorParamsAdapter.class)
public class UrlGeneratorParamsAdapter
{

    private final ContentService contentService;

    private final ProjectService projectService;

    @Activate
    public UrlGeneratorParamsAdapter( @Reference final ContentService contentService, @Reference final ProjectService projectService )
    {
        this.contentService = contentService;
        this.projectService = projectService;
    }

    public BaseUrlStrategy noRequestMediaBaseUrlStrategy( final String baseUrl )
    {
        if ( baseUrl != null )
        {
            return new CustomBaseUrlStrategy( baseUrl );
        }
        else
        {
            return () -> "/api";
        }
    }

    public BaseUrlStrategy requestMediaBaseUrlStrategy( final String baseUrl, final String urlType, final String mediaType )
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();

        if ( baseUrl != null )
        {
            return noRequestMediaBaseUrlStrategy( baseUrl );
        }
        else if ( "/api".equals( portalRequest.getBaseUri() ) )
        {
            return SlashApiRewritableBaseUrlStrategy.forApi( ApplicationKey.from( "media" ), mediaType, portalRequest, urlType );
        }
        else if ( portalRequest.isSiteBase() )
        {
            return SiteRequestBaseUrlStrategy.create()
                .setContentService( contentService )
                .setPortalRequest( portalRequest )
                .setUrlType( urlType )
                .build();
        }
        else
        {
            return NonSiteRequestBaseUrlStrategy.create().setPortalRequest( portalRequest ).setUrlType( urlType ).build();
        }
    }

    public BaseUrlStrategy contentBaseUrlStrategy( final BaseUrlParams params )
    {
        return () -> {
            final String baseUrl = new ContentBaseUrlResolver( contentService, projectService, params ).resolve( metadata -> null );

            final PortalRequest portalRequest = PortalRequestAccessor.get();
            if ( portalRequest != null && portalRequest.isSiteBase() && params.getProjectName() == null && params.getBranch() == null )
            {
                return UrlBuilderHelper.rewriteUri( portalRequest.getRawRequest(), params.getUrlType(), baseUrl );
            }
            return baseUrl;
        };
    }


    public BaseUrlStrategy pageBaseUrlStrategy( final PageUrlParams params )
    {
        final BaseUrlParams baseUrlParams = BaseUrlParams.create()
            .setUrlType( params.getType() )
            .setProjectName( params.getProjectName() )
            .setBranch( params.getBranch() )
            .setId( params.getId() )
            .setPath( params.getPath() )
            .build();

        return () -> {
            final PortalRequest portalRequest = PortalRequestAccessor.get();

            final boolean preferSiteRequest =
                portalRequest != null && portalRequest.isSiteBase() && params.getProjectName() == null && params.getBranch() == null;

            final String baseUrl = new ContentBaseUrlResolver( contentService, projectService, baseUrlParams ).resolve( metadata -> {
                if ( preferSiteRequest )
                {
                    return new ContentPathResolver().portalRequest( portalRequest )
                        .contentService( this.contentService )
                        .id( params.getId() )
                        .path( params.getPath() )
                        .resolve()
                        .toString();
                }
                else if ( metadata.getBaseUrl() == null )
                {
                    return metadata.getContent().getPath().toString();
                }
                else
                {
                    final Site nearestSite = metadata.getNearestSite();
                    final Content content = metadata.getContent();
                    return nearestSite != null
                        ? content.getPath().toString().substring( nearestSite.getPath().toString().length() )
                        : content.getPath().toString();
                }
            } );

            return preferSiteRequest ? UrlBuilderHelper.rewriteUri( portalRequest.getRawRequest(), params.getType(), baseUrl ) : baseUrl;
        };
    }

    private String resolveApiApplication( final String application, final ApplicationKey applicationFromRequest )
    {
        String result = application;
        if ( application == null && applicationFromRequest != null )
        {
            result = applicationFromRequest.toString();
        }
        if ( result == null )
        {
            throw new IllegalArgumentException( "Application must be provided" );
        }
        return result;
    }

    public ApiUrlGeneratorParams requestApiUrlParams( final ApiUrlParams params )
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();

        final String application = resolveApiApplication( params.getApplication(), portalRequest.getApplicationKey() );

        final BaseUrlStrategy baseUrlStrategy = ApiRequestBaseUrlStrategy.create()
            .setContentService( contentService )
            .setPortalRequest( portalRequest )
            .setUrlType( params.getType() )
            .build();

        return ApiUrlGeneratorParams.create()
            .setBaseUrlStrategy( baseUrlStrategy )
            .setApplication( application )
            .setApi( params.getApi() )
            .setPath( () -> resolveApiPath( params ) )
            .addQueryParams( params.getQueryParams() )
            .build();
    }

    public ApiUrlGeneratorParams offlineApiUrlParams( final ApiUrlParams params )
    {
        final BaseUrlStrategy baseUrlStrategy;
        if ( params.getBaseUrl() != null )
        {
            final String path = UrlTypeConstants.SERVER_RELATIVE.equals( params.getType() )
                ? URI.create( params.getBaseUrl() ).getPath()
                : params.getBaseUrl();
            baseUrlStrategy = () -> path.endsWith( "/" ) ? path.substring( 0, path.length() - 1 ) + "/_/" : path + "/_/";
        }
        else if ( params.getBaseUrlParams() != null )
        {
            final BaseUrlParams baseUrlParams = params.getBaseUrlParams();

            final ProjectName projectName = offlineProjectName( baseUrlParams.getProjectName() );
            final Branch branch = offlineBranch( baseUrlParams.getBranch() );
            final String contentKey = Objects.requireNonNullElse( baseUrlParams.getId(), baseUrlParams.getPath() );

            final Supplier<Content> baseUrlContentSupplier = () -> offlineNearestSite( projectName, branch, contentKey );

            baseUrlStrategy = offlineBaseUrlStrategy( projectName, branch, baseUrlContentSupplier, params.getType() );
        }
        else
        {
            baseUrlStrategy = () -> "/api";
        }

        return ApiUrlGeneratorParams.create()
            .setBaseUrlStrategy( baseUrlStrategy )
            .setApplication( Objects.requireNonNull( params.getApplication(), "Application must be provided" ) )
            .setApi( params.getApi() )
            .setPath( () -> resolveApiPath( params ) )
            .addQueryParams( params.getQueryParams() )
            .build();
    }

    private String resolveApiPath( final ApiUrlParams params )
    {
        final StringBuilder path = new StringBuilder();

        appendSubPath( path, params.getPath() );
        appendPathSegments( path, params.getPathSegments() );

        return path.toString();
    }

    private Site offlineNearestSite( final ProjectName projectName, final Branch branch, final String contentKey )
    {
        return ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( projectName.getRepoId() )
            .branch( branch )
            .build()
            .callWith( () -> contentKey.startsWith( "/" )
                ? contentService.findNearestSiteByPath( ContentPath.from( contentKey ) )
                : contentService.getNearestSite( ContentId.from( contentKey ) ) );
    }

    private BaseUrlStrategy offlineBaseUrlStrategy( final ProjectName projectName, final Branch branch,
                                                    final Supplier<Content> contentSupplier, final String urlType )
    {
        return OfflineBaseUrlStrategy.create()
            .contentService( contentService )
            .projectService( projectService )
            .projectName( projectName )
            .branch( branch )
            .content( contentSupplier )
            .urlType( urlType )
            .build();
    }

    private ProjectName offlineProjectName( final String projectName )
    {
        return projectName != null
            ? ProjectName.from( projectName )
            : ProjectName.from( Objects.requireNonNull( ContextAccessor.current().getRepositoryId(), "Project must be provided" ) );
    }

    private Branch offlineBranch( final String branch )
    {
        return branch != null
            ? Branch.from( branch )
            : Objects.requireNonNullElse( ContextAccessor.current().getBranch(), ContentConstants.BRANCH_MASTER );
    }

}
